package com.qgao.springcloud.controller;

import com.qgao.springcloud.entity.ArticleLocalcategory;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.service.ArticleLocalcategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class LocalcategoryController {

    private final long userId = 233;

    @Resource
    private ArticleLocalcategoryService articleLocalcategoryService;

    @PostMapping("/authc/article/localcategory/add")
    public CommonResult addLocalcategory(
            @RequestParam("localcategory_name")String localcategoryName){

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        Long localcategoryId;
        try{
            localcategoryId = articleLocalcategoryService.createArticleLocalcategory(localcategoryName,userId);
        }catch (Exception e){
            log.error("add localcategory failure, error info: " + e.getMessage());
            return new CommonResult(401,"add localcategory failure");
        }

        return new CommonResult<>(200,"add localcategory success",localcategoryId);

    }

    @PostMapping("/authc/article/privateSort/delete/{localcategoryId}")
    public CommonResult deleteLocalcategory(
            @PathVariable("localcategoryId")Long localcategoryId){

        if(localcategoryId == null){
            return new CommonResult(400,"localcategoryId is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try{
            articleLocalcategoryService.deleteArticleLocalcategory(localcategoryId,userId);
        }catch (Exception e){
            log.error("delete localcategory failure, error info: " + e.getMessage());
            return new CommonResult(401,"delete localcategory failure");
        }

        return new CommonResult<>(200,"delete localcategory success",localcategoryId);

    }

    @PostMapping("/authc/article/localcategory/update/{localcategoryId}")
    public CommonResult updateLocalcategory(
            @PathVariable("localcategoryId")Long localcategoryId
            ,@RequestParam("localcategory_name")String localcategoryName){

        if(localcategoryId == null || StringUtils.isEmpty(localcategoryName)){
            return new CommonResult(400,"localcategoryId or localcategoryName is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try{
            articleLocalcategoryService.updateArticleLocalcategory(localcategoryId,localcategoryName,userId);
        }catch (Exception e){
            log.error("update localcategory failure, error info: " + e.getMessage());
            return new CommonResult(401,"update localcategory failure");
        }

        return new CommonResult<>(200,"update localcategory success",localcategoryId);

    }

    @GetMapping("/authc/article/localcategory/getAll")
    public CommonResult getLocalateroyList(){
        List<ArticleLocalcategory> articleLocalcategoryList = null;

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try{
            articleLocalcategoryList = articleLocalcategoryService.getLocalcategoryList(userId);
        }catch (Exception e){
            log.error("get localcateroyList failure, error info: " + e.getMessage());
            return new CommonResult(401,"get localcateroyList failure");
        }

        return new CommonResult<>(200,"get localcateroyList success",articleLocalcategoryList);

    }

}
