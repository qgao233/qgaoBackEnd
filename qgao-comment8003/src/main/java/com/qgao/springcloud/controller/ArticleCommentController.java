package com.qgao.springcloud.controller;

import com.qgao.springcloud.dto.ArticleCommentReceiveDto;
import com.qgao.springcloud.dto.ArticleCommentSendDto;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.feign.ArticleFeignService;
import com.qgao.springcloud.service.ArticleCommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class ArticleCommentController {


    private final Long userId = 233L;
    @Resource
    private ArticleCommentService articleCommentService;
    @Resource
    private ArticleFeignService articleFeignService;

    @PostMapping("/authc/article/comment/add")
    public CommonResult addComment(@RequestBody ArticleCommentReceiveDto articleCommentReceiveDto){
        if(StringUtils.isEmpty(articleCommentReceiveDto.getComment_content())
                || articleCommentReceiveDto.getArticle_id() == null){
            return new CommonResult(400,"comment_content or article_id is null.");
        }
        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        Long commentId;
        try {
            commentId = articleCommentService.addArticleComment(articleCommentReceiveDto,userId);
            //更新article表的comment_count
            articleFeignService.increaseArticleCommentCount(1,articleCommentReceiveDto.getArticle_id());
        } catch (Exception e) {
            log.error("add comment failure, error info: "+e.getMessage());
            return new CommonResult(401,"add comment failure");
        }


        return new CommonResult<>(200,"add comment success",commentId);
    }

    @PostMapping("/authc/article/comment/delete/{commentId}")
    public CommonResult deleteComment(@PathVariable("commentId") Long commentId){
        if(commentId == null){
            return new CommonResult(400,"commentId is null.");
        }

        //要么是评论的用户自己删评论，要么是评论的文章的主人将该评论进行删除,在service里判断
        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try {
            int recordCounts = articleCommentService.deleteArticleComment(commentId,userId);
            //更新article表的comment_count
            long article_id = articleCommentService.getArticleIdByCommentId(commentId);
            articleFeignService.decreaseArticleCommentCount(recordCounts,article_id);
        } catch (Exception e) {
            log.error("delete comment failure, error info: "+e.getMessage());
            return new CommonResult(401,"delete comment failure");
        }


        return new CommonResult<>(200,"delete comment success",commentId);
    }

    @PostMapping("/authc/article/comments/delete")
    public CommonResult deleteCommentsByArticleId(@RequestParam("articleId") Long articleId){

        try {
            articleCommentService.deleteArticleCommentList(articleId);
        } catch (Exception e) {
            log.error("delete comments of article failure, error info: "+e.getMessage());
            return new CommonResult(401,"delete comments of article failure");
        }

        return new CommonResult<>(200,"delete comments of article success",articleId);
    }

    /*
    分页获得评论列表
     */
    @GetMapping("/article/comment/list")
    public CommonResult getCommentList(
            @RequestParam("article_id") Long articleId
            ,@RequestParam("page") Integer page){

        int recordLimit = 20;

        List<ArticleCommentSendDto> articleCommentSendDtos;

        try {
            if(userId != null){
                articleCommentSendDtos = articleCommentService.getArticleCommentList(null,page,recordLimit,userId);
            }else {
                articleCommentSendDtos = articleCommentService.getArticleCommentList(articleId,page,recordLimit,null);
            }
        } catch (Exception e) {
            log.error("get comment list failure, error info: "+e.getMessage());
            return new CommonResult(401,"get comment list failure");
        }

        return new CommonResult<>(200,"get comment list success",articleCommentSendDtos);
    }

    /*
    点赞或点踩
     */
    @PostMapping("/authc/article/comment/goodOrBad/{commentId}")
    public CommonResult updateArticleCommentGoodOrBad(
            @PathVariable("commentId") Long commentId
            ,@RequestParam("attitude") String attitudeStr){

        if(StringUtils.isEmpty(attitudeStr)){
            return new CommonResult(400,"attitude type is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try {
            articleCommentService.updateArticleCommentGoodOrBad(commentId,userId,attitudeStr);
        } catch (Exception e) {
            log.error("change article comment good or bad failure, error info: "+e.getMessage());
            return new CommonResult(401,"change article comment good or bad failure");
        }

        return new CommonResult<>(200,"change article comment good or bad success",attitudeStr);
    }

    /*
    排行榜:时间,回复该评论的数量,点赞
     */
    @GetMapping("/article/comment/rank")
    public CommonResult getArticleCommentRank(@RequestParam(value = "order_field")String orderField){
        if(StringUtils.isEmpty(orderField)){
            return new CommonResult<>(400,"orderField is null");
        }

        Integer recordLimits = 10;

        List<ArticleCommentSendDto> articleCommentSendDtos;
        try{
            articleCommentSendDtos = articleCommentService.getArticleCommentRank(recordLimits,orderField);
        }catch (Exception e){
            log.error("get article comment rank failure, error info: " + e.getMessage());
            return new CommonResult<>(401,"get article comment rank failure",orderField);
        }

        return new CommonResult<>(200,"get article comment rank success",articleCommentSendDtos);
    }
}
