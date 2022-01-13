package com.qgao.springcloud.service;

import com.qgao.springcloud.dto.ArticleReceiveDto;
import com.qgao.springcloud.dto.ArticleSendDto;
import com.qgao.springcloud.entity.ArticleTag;

import java.util.List;
import java.util.Map;

public interface ArticleService {

    /**-------------------------增加-------------------------**/
    long createArticle(ArticleReceiveDto articleSaveDto, long userId)throws Exception;

    void createUnionArticleTag(List<ArticleTag> tags, long articleId)throws Exception;


    /**-------------------------删除-------------------------**/
    long removeArticleByArticleId(long articleId, long userId)throws Exception;

    void removeUnionArticleTag(long articleId)throws Exception;


    /**-------------------------更新-------------------------**/
    long updateArticleByArticleId(ArticleReceiveDto articleSaveDto, long articleId, long userId)throws Exception;

    /**-------------------------查询-------------------------**/
    ArticleSendDto getArticleByArticleId(long articleId, String state,Long userId) throws Exception;

    Map<Integer,Object> getArticleViewList(int recordLimits, String state) throws Exception;

    List<ArticleSendDto> getArticleList(Long userId,int recordLimits, Integer categoryId, int page, String order, String state) throws Exception;

    List<ArticleSendDto> userGetArticleList(int recordLimits, Long localcategoryId, int page, String order, String state,Long userId) throws Exception;

    List<ArticleSendDto> getArticleListBySearch(int recordLimits, Integer categoryId, int page, String order, String state, String searchKey) throws Exception;

    /**-------------------------其他-------------------------**/
    void updateArticleViewUp(long articleId) throws Exception;

    void updateArticleState(long articleId, long userId, String state) throws Exception;

    void updateArticleGoodOrBad(long articleId, long userId, String attitudeStr) throws Exception;

    List<ArticleSendDto> getArticleRank(String state, int recordLimits, String orderField) throws Exception;

    void storeArticle(long articleId, long userId, long storecategoryId,String operate) throws Exception;

    int getUserArticleSavedCounts(long userId) throws Exception;

    long getUserByArticleId(long articleId) throws Exception;

    void updateArticleCommentCount(long articleId, int count, String plusOrMinus) throws Exception;

}
