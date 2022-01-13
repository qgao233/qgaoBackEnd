package com.qgao.springcloud.controller;

import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.helper.CookieHelper;
import com.qgao.springcloud.shiro.service.ValidateService;
import com.qgao.springcloud.shiro.token.MailToken;
import com.qgao.springcloud.shiro.token.UserIdToken;
import com.qgao.springcloud.utils.util.CookieUtil;
import com.qgao.springcloud.utils.util.EncryptUtil;
import com.qgao.springcloud.utils.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class LoginController {

    @Resource
    private ValidateService validateService;

    @Resource
    private CookieHelper cookieHelper;

    @PostMapping("/testPostCookie")
    public CommonResult testPostCookie(HttpServletRequest request){
        Cookie cookie = CookieUtil.getTokenOfCookie(request);
        System.out.println(cookie.getName()+"#"+cookie.getValue());
        return new CommonResult<>(200,"login success");

    }

    @GetMapping("/testGetCookie")
    public CommonResult testGetCookie(HttpServletRequest request){
        Cookie cookie = CookieUtil.getTokenOfCookie(request);
        System.out.println(cookie.getName()+"#"+cookie.getValue());
        return new CommonResult<>(200,"login success");

    }

    @PostMapping("/login")
    public CommonResult login(
            HttpServletRequest request
            ,HttpServletResponse response
            ,@RequestParam("username") String idMailInput
            ,@RequestParam("password") String password
            ,@RequestParam(value = "is_remember",required = false) String isRemember) throws Exception{

        Subject subject = SecurityUtils.getSubject();
        Long userId;
        String md5Pwd = EncryptUtil.calcMD5(password);

        try{
            if(StringUtil.isMailFormat(idMailInput)){
                MailToken mailToken = new MailToken(idMailInput,md5Pwd);
                subject.login(mailToken);
                userId = validateService.checkUserLoginByMail(idMailInput,md5Pwd);
            }else if(StringUtil.isOnlyNumber(idMailInput)){
                UserIdToken userIdToken = new UserIdToken(Long.parseLong(idMailInput),md5Pwd);
                subject.login(userIdToken);
                userId = Long.parseLong(idMailInput);
            }else {
                return new CommonResult<>(400,"username is illegal.",idMailInput);
            }
        }catch (Exception e) {
            log.error("login failure, "+e.getMessage());
            return new CommonResult(402,"validate failure");
        }


        //以上如果登录成功，shiro会把userId,md5Pwd以键值对的方式存入redis，没登录成功会抛shiro对应的异常
        //因此这里需要手动将其拼接成字符串"token={userId},{md5Pwd}"的形式放进cookie
        //如果前台点击记住我，则cookie设为一个月，没有则设为一天

        boolean isremember = false;
        if(isRemember!=null){
            isremember = true;
        }

        Map<String,Object> paramsMap = new HashMap<>(2);
        paramsMap.put("key","token");
        paramsMap.put("value",String.valueOf(userId)+StringUtil.COOKIE_SEPARATOR+md5Pwd);
        cookieHelper.addCookie(paramsMap,isremember,request,response);


        return new CommonResult<>(200,"login success");
    }

    @RequiresAuthentication
    @GetMapping("/authc/logout")
    public CommonResult logout(HttpServletResponse response){
        SecurityUtils.getSubject().logout();
        cookieHelper.removeCookie("token",response);
        return new CommonResult<>(200,"logout success");
    }
}
