package com.qgao.springcloud.controller;

import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.entity.User;
import com.qgao.springcloud.helper.CookieHelper;
import com.qgao.springcloud.helper.MailHelper;
import com.qgao.springcloud.service.UserService;
import com.qgao.springcloud.shiro.token.UserIdToken;
import com.qgao.springcloud.utils.util.EncryptUtil;
import com.qgao.springcloud.utils.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class RegistController {

    @Resource
    private UserService userService;
    @Resource
    private MailHelper mailHelper;
    @Resource
    private CookieHelper cookieHelper;

    /*
    检查邮箱是否已被注册
     */
    @PostMapping("/regist/mail/exist")
    public CommonResult checkMail(@RequestParam("mail") String mail){
        if(!StringUtil.isMailFormat(mail)){
            return new CommonResult<>(400,"mail format error",mail);
        }

        try{
            userService.checkMailExistence(mail);
        }catch (Exception e){
            log.error("check if mail exists error, "+e.getMessage());
            return new CommonResult<>(401,"check if mail exists error.");
        }
        return new CommonResult<>(200,"mail is available",mail);
    }

    /*
    给用户发一条注册链接
     */
    @PostMapping("/regist/mail/send")
    public CommonResult registBySendMail(
            @RequestParam("toMail")String toMail
            , @RequestParam(required = false) String frontPageHost
            , HttpServletRequest request) throws IOException {

        if(!StringUtil.isMailFormat(toMail)){
            return new CommonResult<>(400,"mail format error",toMail);
        }

        if(frontPageHost == null){
            String url = request.getRequestURL().toString();
            frontPageHost = url.substring(0,url.indexOf("/",8));
        }



        String subject = "注册qgao";
        try {
            mailHelper.sendMailWithBanner(toMail,subject,frontPageHost);
        } catch (Exception e) {
            log.error("send mail failure, "+e.getMessage());
            return new CommonResult(401,"send mail failure.");
        }

        return new CommonResult<>(200,"mail send success");
    }

    /*
    前台在jump页，会进行同步ajax请求，将mail传进来进行注册，默认密码就是mail，并且返回userId
    同时登录
     */
    @PostMapping("/regist/mail/create")
    public CommonResult registByMail(
            @RequestParam("mail") String mail
            , @RequestParam("deadline") String deadline
            , @RequestParam("applytoken") String applyToken
            , HttpServletRequest request
            , HttpServletResponse response){
        if(!StringUtil.isMailFormat(mail)){
            return new CommonResult<>(400,"mail format error",mail);
        }
        if(!EncryptUtil.calcMD5(mail+deadline).equals(applyToken)){
            return new CommonResult<>(400,"illegal construction",deadline);
        }

        long currentTime = System.currentTimeMillis();
        long deadTime = Long.valueOf(deadline);
        if(currentTime > deadTime){
            return new CommonResult<>(400,"the link has expired.",deadline);
        }

        User user;
        try {
            user = userService.createUserByMail(mail);

            Long userId = user.getId();
            String md5Pwd = user.getPassword();
            SecurityUtils.getSubject().login(new UserIdToken(userId,md5Pwd));
            Map<String,Object> paramsMap = new HashMap<>(2);
            paramsMap.put("key","token");
            paramsMap.put("value",String.valueOf(userId)+StringUtil.COOKIE_SEPARATOR+md5Pwd);
            cookieHelper.addCookie(paramsMap,false,request,response);
        } catch (Exception e) {
            log.error("create user failure, "+e.getMessage());
            return new CommonResult(401,"create user failure.");
        }


        return new CommonResult<>(200,"regist success",user.getId());
    }


}
