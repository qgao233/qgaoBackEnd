package com.qgao.springcloud.controller;

import com.qgao.springcloud.dto.ArticleSendDto;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.feign.PointFeignService;
import com.qgao.springcloud.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对文章的其他操作：如点赞，文章的排行，文章的状态改变等
 */
@RestController
@Slf4j
public class ArticleOtherController {

    private final long userId = 233;


    @Resource
    private ArticleService articleService;
    @Resource
    private PointFeignService pointFeignService;

    /**获得某用户已保存文章的数量*/
    @GetMapping("/article/counts/get/{userId}")
    public CommonResult getUserArticleSavedCounts(@PathVariable("userId")Long userId){

        if(userId == null){
            return new CommonResult<>(400,"userId is null");
        }

        int articleSavedCounts;
        try{
            articleSavedCounts = articleService.getUserArticleSavedCounts(userId);
        }catch (Exception e){
            log.error("get article rank failure, error info: " + e.getMessage());
            return new CommonResult<>(401,"get UserArticleSavedCounts failure",userId);
        }

        return new CommonResult<>(200,"get UserArticleSavedCounts success",articleSavedCounts);
    }


    /*
    排行榜:时间,收藏,阅读,评论,点赞
     */
    @GetMapping("/article/rank")
    public CommonResult getArticleRank(@RequestParam(value = "order_field")String orderField) throws Exception{
        if(StringUtils.isEmpty(orderField)){
            return new CommonResult<>(400,"orderField is null");
        }

        String state = "发布";
        Integer recordLimits = 10;

        List<ArticleSendDto> articleSendDtos;
        try{
            articleSendDtos = articleService.getArticleRank(state,recordLimits,orderField);
        }catch (Exception e){
            log.error("get article rank failure, error info: " + e.getMessage());
            return new CommonResult<>(401,"get article rank failure",orderField);
        }

        return new CommonResult<>(200,"get article rank success",articleSendDtos);
    }

    /*******************三连：点赞、打赏（积分）、收藏*******************************/

    /*
    点赞或点踩
     */
    @PostMapping("/authc/article/goodOrBad/{articleId}")
    public CommonResult updateArticleGoodOrBad(
            @PathVariable(value = "articleId")Long articleId
            ,@RequestParam(value = "attitude")String attitudeStr){

        if(StringUtils.isEmpty(attitudeStr)){
            return new CommonResult(400,"attitude type is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try{
            articleService.updateArticleGoodOrBad(articleId,userId,attitudeStr);
        }catch (Exception e){
            log.error("change article good or bad failure, error info: " + e.getMessage());
            return new CommonResult(401,"change article good or bad failure");
        }

        return new CommonResult<>(200,"change article good or bad success",attitudeStr);
    }

    /*
    打赏文章积分，默认1点积分
     */
    @PostMapping("/authc/article/rewardPoints/{articleId}")
    public CommonResult rewardArticlePoints(
            @PathVariable(value = "articleId")Long articleId
            ,@RequestParam(value = "point",required = false)Integer point){

        if(point == null)
            point = 1;

        try{
            //文章服务：需要判断当前的登录状态，然后查出文章对应的用户调用“积分服务”
            long toUserId = articleService.getUserByArticleId(articleId);
            //积分服务：打赏用户积分是否足够，然后给文章对应的用户添加积分，打赏的用户扣除积分
            pointFeignService.transferPointForUser(toUserId,point);
        }catch (Exception e){
            log.error("reward article failure, error info: " + e.getMessage());
            return new CommonResult<>(401,"reward article failure");
        }

        return new CommonResult<>(200,"reward article success",articleId+" += "+point);
    }

    /*
    收藏文章或者移除收藏文章
     */
    @PostMapping("/authc/article/store/{articleId}")
    public CommonResult storeArticle(
            @PathVariable(value = "articleId")Long articleId
            ,@RequestParam("store_category_id") Long storeCategoryId
            ,@RequestParam("operate") String operate){
        if(storeCategoryId == null || StringUtils.isEmpty(operate)){
            return new CommonResult<>(400,"storeCategoryId or operate is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try{
            articleService.storeArticle(articleId,userId,storeCategoryId,operate);
        }catch (Exception e){
            log.error("store article failure, error info: " + e.getMessage());
            return new CommonResult<>(401,"store article failure");
        }

        return new CommonResult<>(200,"store article success",storeCategoryId+" : "+operate+" "+articleId);
    }

    /*
    更改文章状态
     */
    @PostMapping("/authc/article/changeState/{articleId}")
    public CommonResult changeArticleState(
            @PathVariable(value = "articleId")Long articleId
            ,@RequestParam(value = "state")String state){

        if(articleId == null || StringUtils.isEmpty(state)){
            return new CommonResult<>(400,"articleId or state is null");
        }

        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        try{
            articleService.updateArticleState(articleId,userId,state);
        }catch (Exception e){
            log.error("change article state failure, error info: " + e.getMessage());
            return new CommonResult<>(401,"change article state failure");
        }

        return new CommonResult<>(200,"change article state success",state);

    }


    @PostMapping("/authc/article/commentcount/increase")
    public CommonResult increaseArticleCommentCount(
            @RequestParam(value = "count",required = false) Integer count,
            @RequestParam("articleId") Long articleId){

        if(count == null)
            count = 1;

        try {
            articleService.updateArticleCommentCount(articleId,count,"plus");
        } catch (Exception e) {
            log.error("increase article commentCount failure, error info: " + e.getMessage());
            return new CommonResult<>(401,"increase article commentCount failure");
        }

        return new CommonResult<>(200,"increase article commentCount success",articleId);
    }

    @PostMapping("/authc/article/commentcount/decrease")
    public CommonResult decreaseArticleCommentCount(
            @RequestParam(value = "count",required = false) Integer count,
            @RequestParam("articleId") Long articleId){

        if(count == null)
            count = 1;

        try {
            articleService.updateArticleCommentCount(articleId,count,"minus");
        } catch (Exception e) {
            log.error("decrease article commentCount failure, error info: " + e.getMessage());
            return new CommonResult<>(401,"decrease article commentCount failure");
        }

        return new CommonResult<>(200,"decrease article commentCount success",articleId);
    }

}
