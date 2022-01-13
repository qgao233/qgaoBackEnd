package com.qgao.springcloud.controller;

import com.qgao.springcloud.dto.ArticleReceiveDto;
import com.qgao.springcloud.mq.service.ArticleProducer;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.feign.ArticleCommentFeignService;
import com.qgao.springcloud.feign.ArticleImgFeignService;
import com.qgao.springcloud.feign.PointFeignService;
import com.qgao.springcloud.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 对文章的：增、删、改
 */
@RestController
@Slf4j
public class ArticlePostController {

    private final long userId = 233;


    @Resource
    private ArticleService articleService;

    @Resource
    private PointFeignService pointFeignService;
    @Resource
    private ArticleCommentFeignService articleCommentFeignService;
    @Resource
    private ArticleImgFeignService articleImgFeignService;
    @Resource
    private ArticleProducer articleProducer;

    //test
    @GetMapping(value = "/echo")
    public String echo() {
        return pointFeignService.echo("333");
    }

    //test
    @GetMapping("/testMQ")
    public String sendMessage(){
        articleProducer.sendMessageWithNewArticle(0l,333l);
        return "success";
    }

    /**
     * 保存文章
     */
    @RequiresAuthentication
    @PostMapping("/authc/article/save")
    public CommonResult saveArticle(@RequestBody ArticleReceiveDto articleSaveDto){

        if(StringUtils.isEmpty(articleSaveDto.getArticle_title())
                || StringUtils.isEmpty(articleSaveDto.getArticle_content())){
            return new CommonResult<>(400,"title or content is null");
        }

        if(StringUtils.isEmpty(articleSaveDto.getPublic_type())
                || StringUtils.isEmpty(articleSaveDto.getPrivate_type())){
            return new CommonResult<>(400,"category or localcategory is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();
        long articleId;

        try {
            articleId = articleService.createArticle(articleSaveDto, userId);
            int point = 1;
            //增加积分
            pointFeignService.increasePoint(point);
            //转移文章图片的存储空间
            articleImgFeignService.transferTempImg(articleId);

            //通知粉丝
            articleProducer.sendMessageWithNewArticle(userId,articleId);
        }catch (Exception e){
            log.error("save article failure : " + e.getMessage());
            return new CommonResult(401,"save article failure");
        }
        log.debug("save article success");
        return new CommonResult<>(200,"save article success",articleId);
    }


    /**
     * 根据文章ID删除文章
     */
    @PostMapping("/authc/article/delete/{articleId}")
    public CommonResult deleteArticle(@PathVariable("articleId")Long articleId){
        if(articleId == null){
            return new CommonResult(400,"articleId is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try {
            articleService.removeArticleByArticleId(articleId,userId);
            articleCommentFeignService.deleteCommentsByArticleId(articleId);
            articleImgFeignService.deleteCommonImg(articleId);
        } catch (Exception e) {
            log.error("remove article failure, articleId: " +articleId+ ", error info: " + e.getMessage());
            return new CommonResult(401,"remove article failure");
        }
        return new CommonResult<>(200,"remove article success",articleId);
    }

    /**
     * 修改文章
     */
    @PostMapping("/authc/article/update/{articleId}")
    public CommonResult updateArticle(@RequestBody ArticleReceiveDto articleSaveDto
            , @PathVariable("articleId")Long articleId){

        if(articleId == null){
            return new CommonResult(400,"articleId is null");
        }

        if(articleSaveDto.getArticle_title() == null || articleSaveDto.getArticle_title().equals("")
                || articleSaveDto.getArticle_content() == null || articleSaveDto.getArticle_content().equals("")){
            return new CommonResult(400,"title or content is null");
        }

        if(articleSaveDto.getPublic_type() == null || articleSaveDto.getPublic_type().equals("")
                || articleSaveDto.getPrivate_type() == null || articleSaveDto.getPrivate_type().equals("")){
            return new CommonResult(400,"category or localcategory is null");
        }


        try {
            articleService.updateArticleByArticleId(articleSaveDto, articleId, userId);
            articleImgFeignService.transferTempImg(articleId);
        }catch (Exception e){
            log.error("update article failure, articleId: " +articleId+ ", error info: " + e.getMessage());
            return new CommonResult(401,"update article failure");
        }
        log.debug("update article success");
        return new CommonResult<>(200,"update article success",articleId);

    }




}
