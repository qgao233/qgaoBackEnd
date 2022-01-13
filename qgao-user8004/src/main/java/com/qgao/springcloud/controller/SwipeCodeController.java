package com.qgao.springcloud.controller;

import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.helper.CodeHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/*
滑动验证码
 */
@RestController
@Slf4j
public class SwipeCodeController {

    @Resource
    private CodeHelper codeHelper;

    @GetMapping("/code/swipe/get")
    public CommonResult getSwipeCode(){

        Map<String,Object> paramsMap;
        try {
            paramsMap = codeHelper.getRandomCodeMap();
        }catch (Exception e){
            log.error("get code failure, "+e.getMessage());
            return new CommonResult(401,"get code failure");
        }
        return new CommonResult<>(200,"get code success",paramsMap);
    }

    @GetMapping("/code/swipe/check")
    public CommonResult checkSwipeCode(
            @RequestParam("distance")Float distance
            , @RequestParam("session_id") String sessionId){
        if(distance == null || StringUtils.isEmpty(sessionId)){
            return new CommonResult<>(400,"illegal request: code or session_id is null");
        }

        try {
            if(codeHelper.checkSwipeCode(distance,sessionId)){
                return new CommonResult<>(200,"code is right");
            }else{
                return new CommonResult<>(201,"code is false");
            }
        }catch (Exception e){
            log.error("check code failure, "+e.getMessage());
            return new CommonResult(401,"check code failure");
        }
    }
}
