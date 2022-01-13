package com.qgao.springcloud.feign;

import com.qgao.springcloud.utils.entity.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient("user-service")
public interface UserFeignService {

    @GetMapping("/user/img/get/{userId}")
    CommonResult getUserImg(@PathVariable("userId")Long userId);

    @PostMapping("/authc/user/img/update")
    CommonResult updateUserImg(@RequestParam("imgName") String imgName);
}
