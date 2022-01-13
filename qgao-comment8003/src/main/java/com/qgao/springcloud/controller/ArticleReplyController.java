package com.qgao.springcloud.controller;

import com.qgao.springcloud.dto.ArticleReplyReceiveDto;
import com.qgao.springcloud.dto.ArticleReplySendDto;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.feign.ArticleFeignService;
import com.qgao.springcloud.service.ArticleCommentService;
import com.qgao.springcloud.service.ArticleReplyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class ArticleReplyController {

    private final Long userId = 233L;

    @Resource
    private ArticleReplyService articleReplyService;
    @Resource
    private ArticleCommentService articleCommentService;
    @Resource
    private ArticleFeignService articleFeignService;

    @PostMapping("/authc/article/comment/add")
    public CommonResult addComment(@RequestBody ArticleReplyReceiveDto articleReplyReceiveDto){
        if(StringUtils.isEmpty(articleReplyReceiveDto.getReply_content())
                || articleReplyReceiveDto.getReply_id() == null){
            return new CommonResult(400,"comment_content or article_id is null.");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        Long replyId;
        try {
            replyId = articleReplyService.addArticleReply(articleReplyReceiveDto,userId);
            //更新article表的comment_count
            long article_id = articleCommentService.getArticleIdByCommentId(articleReplyReceiveDto.getComment_id());
            articleFeignService.increaseArticleCommentCount(1,article_id);

        } catch (Exception e) {
            log.error("add reply failure, error info: "+e.getMessage());
            return new CommonResult(401,"add reply failure");
        }


        return new CommonResult<>(200,"add reply success",replyId);
    }

    @PostMapping("/authc/article/comment/delete/{replyId}")
    public CommonResult deleteComment(@PathVariable("replyId") Long replyId){
        if(replyId == null){
            return new CommonResult(400,"replyId is null.");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        //要么是评论的用户自己删评论，要么是评论的文章的主人将该评论进行删除,service中处理
        try {
            long commentId = articleReplyService.deleteArticleReply(replyId,userId);
            //更新article表的comment_count
            long article_id = articleCommentService.getArticleIdByCommentId(commentId);
            articleFeignService.decreaseArticleCommentCount(1,article_id);
        } catch (Exception e) {
            log.error("delete reply failure, error info: "+e.getMessage());
            return new CommonResult(401,"delete reply failure");
        }


        return new CommonResult<>(200,"delete reply success",replyId);
    }


    /*
    分页获得评论列表
     */
    @GetMapping("/article/comment/list")
    public CommonResult getCommentList(
            @RequestParam("comment_id") Long commentId
            ,@RequestParam("page") Integer page){

        int recordLimit = 10;

        List<ArticleReplySendDto> articleReplySendDtos;

        try {
            articleReplySendDtos = articleReplyService.getArticleReplyList(commentId,page,recordLimit);
        } catch (Exception e) {
            log.error("get reply list failure, error info: "+e.getMessage());
            return new CommonResult(401,"get reply list failure");
        }

        return new CommonResult<>(200,"get reply list success",articleReplySendDtos);
    }

    /*
    点赞或点踩
     */
    @PostMapping("/authc/article/comment/goodOrBad/{replyId}")
    public CommonResult updateArticleCommentGoodOrBad(
            @PathVariable("replyId") Long replyId
            ,@RequestParam("attitude") String attitudeStr){

        if(StringUtils.isEmpty(attitudeStr)){
            return new CommonResult(400,"attitude type is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try {
            articleReplyService.updateArticleReplyGoodOrBad(replyId,userId,attitudeStr);
        } catch (Exception e) {
            log.error("change article reply good or bad failure, error info: "+e.getMessage());
            return new CommonResult(401,"change article reply good or bad failure");
        }

        return new CommonResult<>(200,"change article reply good or bad success",attitudeStr);
    }

}
