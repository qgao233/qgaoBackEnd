package com.qgao.springcloud.feign;

import com.qgao.springcloud.utils.entity.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient("article-service")
public interface ArticleFeignService {

    @PostMapping("/authc/article/commentcount/increase")
    public CommonResult increaseArticleCommentCount(
            @RequestParam(value = "count",required = false) Integer count,
            @RequestParam("articleId") Long articleId);

    @PostMapping("/authc/article/commentcount/decrease")
    public CommonResult decreaseArticleCommentCount(
            @RequestParam(value = "count",required = false) Integer count,
            @RequestParam("articleId") Long articleId);
}
