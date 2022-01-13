package com.qgao.springcloud.feign;

import com.qgao.springcloud.utils.entity.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient("comment-service")
public interface ArticleCommentFeignService {

    @PostMapping("/authc/article/comments/delete")
    CommonResult deleteCommentsByArticleId(@RequestParam("articleId") Long articleId);
}
