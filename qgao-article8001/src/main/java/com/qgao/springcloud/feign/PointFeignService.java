package com.qgao.springcloud.feign;

import com.qgao.springcloud.utils.entity.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient("point-service")
public interface PointFeignService {

    @GetMapping(value = "/echo/{string}")
    String echo(@PathVariable("string") String string);

    @PostMapping("/authc/point/add")
    CommonResult increasePoint(@RequestParam("point")Integer point);

    @PostMapping("/authc/point/transfer/for/{userId}")
    public CommonResult transferPointForUser(
            @PathVariable("userId") Long toUserId
            ,@RequestParam("point")Integer point);
}
