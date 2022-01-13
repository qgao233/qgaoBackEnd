package com.qgao.springcloud.controller;

import com.qgao.springcloud.entity.ArticleCategory;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.service.ArticleCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class CategoryController {

    @Resource
    private ArticleCategoryService articleCategoryService;

    @GetMapping("/article/category/getAll")
    public CommonResult getCateroyList(){
        List<ArticleCategory> articleCategoryList = null;
        try{
            articleCategoryList = articleCategoryService.getArticleCategories();
        }catch (Exception e){
            log.error("get cateroyList failure, error info: " + e.getMessage());
            return new CommonResult(401,"get cateroyList failure");
        }

        return new CommonResult<>(200,"get cateroyList success",articleCategoryList);

    }
}
