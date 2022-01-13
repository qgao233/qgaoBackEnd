package com.qgao.springcloud.controller;

import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.service.ArticleImgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
public class ArticleImgController {

    private final long userId = 233;

    @Resource
    private ArticleImgService articleImgService;

    /*
    进入编写文章的页面就会调用这个接口
     */
    @GetMapping("/authc/article/img/createTempBufferFolder")
    public CommonResult createTempBufferFolder(){

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        String tempBufferId;
        try{
            tempBufferId = articleImgService.createTempBufferFolder(userId);
        }catch (Exception e){
            log.error("createTempBufferFolder failure, "+e.getMessage());
            return new CommonResult(401,"createTempBufferFolder failure.");
        }
        return new CommonResult<>(200,"create Temp buffer Folder success",tempBufferId);
    }

    @PostMapping("/authc/static/article/img/upload")
    public CommonResult uploadTempArticleImg(@RequestParam("img") MultipartFile[] imgs){

        if(imgs == null || imgs.length == 0){
            return new CommonResult(400,"upload file failure");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        String[] imgIdSuffix;
        try {
            imgIdSuffix = articleImgService.uploadArticleImg(userId,imgs);
        } catch (Exception e) {
            log.error("upload file failure, "+e.getMessage());
            return new CommonResult(401,"upload file failure");
        }
        return new CommonResult<>(200,"upload file success",imgIdSuffix);

    }

    @PostMapping("/authc/static/article/img/delete/{imgIdSuffix:.+}")
    public CommonResult deleteTempArticleImg(@PathVariable("imgIdSuffix")String imgIdSuffix){

        if(StringUtils.isEmpty(imgIdSuffix)){
            return new CommonResult(400,"imgIdSuffix is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try {
            articleImgService.deleteArticleImg(userId,imgIdSuffix);
        } catch (Exception e) {
            log.error("delete file failure, "+e.getMessage());
            return new CommonResult(401,"delete file failure");
        }
        return new CommonResult<>(200,"delete file success",imgIdSuffix);

    }


    /*
    访问这个接口不需要用户认证
     */
    @GetMapping("/static/article/img/get/{imgIdSuffix:.+}")
    public CommonResult getArticleImg(
            @PathVariable("imgIdSuffix")String imgIdSuffix
            ,HttpServletResponse response){

        if(StringUtils.isEmpty(imgIdSuffix)){
            return new CommonResult(400,"imgIdSuffix is null");
        }


        try {
            articleImgService.getArticleImg(imgIdSuffix,response);
            response.flushBuffer();
        } catch (Exception e) {
            log.error("get file failure, "+e.getMessage());
            return new CommonResult(401,"get file failure");
        }
        return new CommonResult(200,"get file success");
    }

    /*
    从临时存储区转移到公共存储区
     */
    @GetMapping("/authc/static/article/img/transfer/{articleId}")
    public CommonResult transferTempImg(@PathVariable("articleId")Long articleId){

        if(articleId == null){
            return new CommonResult(400,"articleId is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try {
            articleImgService.transferArticleImg(userId,articleId);
        } catch (Exception e) {
            log.error("transfer file failure, "+e.getMessage());
            return new CommonResult(401,"transfer file failure");
        }
        return new CommonResult(200,"transfer file success");

    }

    /*
    删除公共区的图片
     */
    @GetMapping("/authc/static/article/img/common/delete/{articleId}")
    public CommonResult deleteCommonImg(@PathVariable("articleId")Long articleId){
        if(articleId == null){
            return new CommonResult(400,"articleId is null");
        }


        try {
            articleImgService.deleteCommonArticleImg(articleId);
        } catch (Exception e) {
            log.error("delete file failure, "+e.getMessage());
            return new CommonResult(401,"delete file failure");
        }
        return new CommonResult(200,"delete file success");

    }

}
