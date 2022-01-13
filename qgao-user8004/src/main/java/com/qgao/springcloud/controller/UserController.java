package com.qgao.springcloud.controller;

import com.qgao.springcloud.dto.UserReceiveDto;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.entity.User;
import com.qgao.springcloud.helper.CookieHelper;
import com.qgao.springcloud.service.UserService;
import com.qgao.springcloud.shiro.token.UserIdToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private CookieHelper cookieHelper;

    /*
    修改用户昵称和密码
     */
    @RequiresAuthentication
    @PostMapping("/authc/user/pwd/modify")
    public CommonResult updateUser(
            @RequestParam(value = "nick_name",required = false)String nickName
            ,@RequestParam(value = "pass_word",required = false)String password
            , HttpServletRequest request
            , HttpServletResponse response){
        if(StringUtils.isEmpty(nickName) && StringUtils.isEmpty(password)){
            return new CommonResult<>(200,"modify success");
        }

        Subject subject = SecurityUtils.getSubject();
        Long userId = (Long)subject.getPrincipal();
        try {
            String md5Pwd = userService.updateUser(userId,nickName,password);

            if(StringUtils.isNotEmpty(md5Pwd)){
                subject.logout();
                UserIdToken userIdToken = new UserIdToken(userId,md5Pwd);
                subject.login(userIdToken);
                Map<String,Object> paramsMap = new HashMap<>(2);
                paramsMap.put("key","token");
                paramsMap.put("value",String.valueOf(userId)+","+md5Pwd);
                cookieHelper.addCookie(paramsMap,false,request,response);
            }
        } catch (Exception e) {
            log.error("modify user failure, "+e.getMessage());
            return new CommonResult(401,"modify user failure.");
        }
        return new CommonResult<>(200,"modify user success.",userId);
    }

    /*
    获得用户上一次的登录时间
     */
    @RequiresAuthentication
    @GetMapping("/authc/user/lasttime/get")
    public CommonResult getUserLastTime(){
        Long userId = (Long)SecurityUtils.getSubject().getPrincipal();
        Date lastTime;
        try {
            lastTime = userService.getUserLastTime(userId);
        } catch (Exception e) {
            log.error("get last time failure, "+e.getMessage());
            return new CommonResult(401,"get last time failure");
        }
        return new CommonResult<>(200,"get last time success.",lastTime);
    }

    @GetMapping("/user/get/{userId}")
    public CommonResult getUserAbstractMsg(@PathVariable("userId")Long userId){
        if(userId == null) return new CommonResult(400,"userId is null");
        User user;
        try {
            user = userService.getUserLittleMsg(userId);
        } catch (Exception e) {
            log.error("get user info failure, "+e.getMessage());
            return new CommonResult(401,"get user info failure");
        }
        return new CommonResult<>(200,"get user info success.",user);
    }



    @RequiresAuthentication
    @GetMapping("/authc/user/detail/get")
    public CommonResult getUserDetail(){
        Long userId = (Long)SecurityUtils.getSubject().getPrincipal();
        User user;
        try {
            user = userService.getUserDetail(userId);
        } catch (Exception e) {
            log.error("get user detail failure, "+e.getMessage());
            return new CommonResult(401,"get user detail failure");
        }
        return new CommonResult<>(200,"get user detail success.",user);
    }

    /**
     * 更新用户详细
     */
    @RequiresAuthentication
    @PostMapping("/authc/user/detail/update")
    public CommonResult updateUserDetail(@RequestBody UserReceiveDto userReceiveDto)throws Exception{

        Long userId = (Long)SecurityUtils.getSubject().getPrincipal();

        try {
            userService.updateUserDetail(userId,userReceiveDto);
        } catch (Exception e) {
            log.error("update user detail failure, "+e.getMessage());
            return new CommonResult(401,"update user detail failure");
        }
        return new CommonResult<>(200,"update user detail success.",userId);
    }

    @GetMapping("/user/img/get/{userId}")
    public CommonResult getUserImg(@PathVariable("userId")Long userId){
        if(userId == null) return new CommonResult(400,"userId is null");
        User user;
        try {
            user = userService.getUserLittleMsg(userId);
        } catch (Exception e) {
            log.error("get user info failure, "+e.getMessage());
            return new CommonResult(401,"get user info failure");
        }
        return new CommonResult<>(200,"get user info success.",user.getImg());
    }

    @PostMapping("/authc/user/img/update")
    public CommonResult updateUserImg(@RequestParam("imgName") String imgName){

        Long userId = (Long)SecurityUtils.getSubject().getPrincipal();

        UserReceiveDto userReceiveDto = new UserReceiveDto();
        userReceiveDto.setImg(imgName);

        try {
            userService.updateUserDetail(userId,userReceiveDto);
        } catch (Exception e) {
            log.error("get user info failure, "+e.getMessage());
            return new CommonResult(401,"get user info failure");
        }
        return new CommonResult<>(200,"get user info success.",imgName);
    }

}
