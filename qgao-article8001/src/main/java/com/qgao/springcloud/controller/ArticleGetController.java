package com.qgao.springcloud.controller;

import com.qgao.springcloud.dto.ArticleSendDto;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 对文章的内容的各种查询
 */
@RestController
@Slf4j
public class ArticleGetController {

    private final long userId = 233;

    @Resource
    private ArticleService articleService;

    /**
      * 根据articleId查询文章详细
      */
    @GetMapping("/article/get/{articleId}")
    public CommonResult getArticleByArticleId(
            @PathVariable("articleId")Long articleId
            ,@RequestParam(value = "state",required = false)String state){

        if(articleId == null){
            return new CommonResult(400,"articleId is null");
        }

        String defaultState = "发布";
        ArticleSendDto articleSendDto = null;
        try{
            String realState;
            if(state == null || state.equals(defaultState)){
                realState = defaultState;
                articleSendDto = articleService.getArticleByArticleId(articleId,realState,null);
            }else {
                throw new RuntimeException("illegal retrieve");
            }

        }catch (Exception e){
            log.error("get article failure, articleId: " +articleId+ ", error info: " + e.getMessage());
            return new CommonResult(401,"get article failure");
        }

        return new CommonResult<>(200,"get article success",articleSendDto);
    }

    /**
     * 用户根据articleId查询文章详细
     */
    @GetMapping("/authc/article/get/{articleId}")
    public CommonResult getArticleOfUserByArticleId(
            @PathVariable("articleId")Long articleId
            ,@RequestParam(value = "state",required = false)String state){

        if(articleId == null){
            return new CommonResult(400,"articleId is null");
        }

        ArticleSendDto articleSendDto = null;
        try{
            articleSendDto = articleService.getArticleByArticleId(articleId,state,userId);

        }catch (Exception e){
            log.error("get article failure, articleId: " +articleId+ ", error info: " + e.getMessage());
            return new CommonResult(401,"get article failure");
        }

        return new CommonResult<>(200,"get article success",articleSendDto);
    }




    /**
     * 首页文章展示：多个分类，各分类下有文章列表（5）
     * 返回参数为：Map<Long, Map<String, List<ArticleSendDto>>>
     */
    @GetMapping("/article/viewList")
    public CommonResult getArticleViewList(){
        int recordLimit = 5;
        String state = "发布";

        Map<Integer,Object> paramsMap;
        try{
            paramsMap = articleService.getArticleViewList(recordLimit,state);
        }catch (Exception e){
            log.error("get articleViewList failure, error info: " + e.getMessage());
            return new CommonResult(401,"get articleViewList failure");
        }

        return new CommonResult<>(200,"get articleViewList success",paramsMap);
    }

    /**
     * 文章列表（20）
     * 返回参数为：List<ArticleSendDto>
     */
    @GetMapping("/article/list")
    public CommonResult getArticleList(
            @RequestParam(value = "user_id",required = false) Long lookUserId
            , @RequestParam(value="category_id", required = false) Integer categoryId
            , @RequestParam(value = "page", required = true) Integer page
            , @RequestParam(value = "order", required = false) String order){
        //排序默认是降序

        int recordLimit = 20;
        String state = "发布";

        List<ArticleSendDto> articleSendDtos;
        try{
            articleSendDtos = articleService.getArticleList(lookUserId,recordLimit,categoryId,page,order,state);
        }catch (Exception e){
            log.error("get articleList failure, error info: " + e.getMessage());
            return new CommonResult(401,"get articleList failure");
        }

        return new CommonResult<>(200,"get articleList success",articleSendDtos);
    }

    /*
    用户自己查看自己的文章列表
     */
    @RequiresAuthentication
    @GetMapping("/authc/article/list")
    public CommonResult userGetArticleList(
            @RequestParam(value="localcategory_id", required = false) Long localcategoryId
            , @RequestParam(value = "page", required = true) Integer page
            , @RequestParam(value = "order", required = false) String order
            , @RequestParam(value = "state",required = false) String state){

        int recordLimit = 20;
        long userId = (long) SecurityUtils.getSubject().getPrincipal();

        List<ArticleSendDto> articleSendDtos;

        try{
            articleSendDtos = articleService.userGetArticleList(recordLimit,localcategoryId,page,order,state,userId);
        }catch (Exception e){
            log.error("get articleList failure, error info: " + e.getMessage());
            return new CommonResult(401,"get articleList failure");
        }

        return new CommonResult<>(200,"get articleList success",articleSendDtos);

    }

    /**
     * 根据searchKey搜索含有相关标签与标题的文章列表
     */
    @GetMapping("/article/list/search")
    public CommonResult searchArticleList(
            @RequestParam(value = "search_key",required = false)String searchKey
            , @RequestParam(value="category_id", required = false) Integer categoryId
            , @RequestParam(value = "page", required = true) Integer page
            , @RequestParam(value = "order", required = false) String order)throws Exception{
        if(StringUtils.isEmpty(searchKey)){
            return new CommonResult(400,"searchKey is null");
        }
        int recordLimit = 20;
        String state = "发布";

        List<ArticleSendDto> articleSendDtos;
        try{
            articleSendDtos = articleService.getArticleListBySearch(recordLimit,categoryId,page,order,state,searchKey);
        }catch (Exception e){
            log.error("search articleList failure, error info: " + e.getMessage());
            return new CommonResult(401,"search articleList failure");
        }

        return new CommonResult<>(200,"search articleList success",articleSendDtos);
    }


}
