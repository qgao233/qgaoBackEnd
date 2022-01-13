package com.qgao.springcloud.feign;

import com.qgao.springcloud.utils.entity.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Component
@FeignClient("file-service")
public interface ArticleImgFeignService {

    @GetMapping("/authc/static/article/img/common/delete/{articleId}")
    CommonResult deleteCommonImg(@PathVariable("articleId")Long articleId);

    @GetMapping("/authc/static/article/img/transfer/{articleId}")
    CommonResult transferTempImg(@PathVariable("articleId")Long articleId);
}
