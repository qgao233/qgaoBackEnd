package com.qgao.springcloud.controller;

import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
public class PointController {

    @Resource
    private PointService pointService;

    @GetMapping(value = "/echo/{string}")
    public String echo(@PathVariable String string) {
        return "Hello Nacos Discovery " + string;
    }

    /*
    增加积分
     */
    @RequiresAuthentication
    @PostMapping("/authc/point/add")
    public CommonResult increasePoint(@RequestParam("point")Integer point){
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();

        try {
            pointService.increaseUserPoint(userId,point);
        } catch (Exception e) {
            log.error("increase point failure : "+userId+", "+point+"\n" + e.getMessage());
            return new CommonResult(401,"increase point failure");
        }

        return new CommonResult(200,"increase point success");
    }

    /*
    打赏：给某用户增加积分
     */
    @RequiresAuthentication
    @PostMapping("/authc/point/transfer/for/{userId}")
    public CommonResult transferPointForUser(
            @PathVariable("userId") Long toUserId
            ,@RequestParam("point")Integer point){

        Long fromUserId = (Long) SecurityUtils.getSubject().getPrincipal();

        try {
            pointService.transferUserPoint(fromUserId,toUserId,point);
        } catch (Exception e) {
            log.error("transfer point failure : "+toUserId+", "+point+"\n" + e.getMessage());
            return new CommonResult(401,"transfer point failure");
        }

        return new CommonResult(200,"transfer point success");
    }
}
