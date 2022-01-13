package com.qgao.springcloud.handler;


import com.qgao.springcloud.utils.entity.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler({Exception.class})
    public CommonResult processException(Exception e){
        log.error("throw an exception: "+ e.getMessage());
        return new CommonResult(400,"Exception occurs.");
    }

}
