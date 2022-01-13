package com.qgao.springcloud.service;

import com.qgao.springcloud.dto.ArticleReceiveDto;
import com.qgao.springcloud.entity.ArticleLocalcategory;

import java.util.List;
import java.util.Map;

public interface ArticleLocalcategoryService {

    void updateArticleCountUp(long id,long userId)throws Exception;

    void updateArticleCountDown(long id,long userId) throws Exception;

    void updateArticleCountUpOrDown(ArticleReceiveDto articleSaveDto, long articleId, long userId) throws Exception;

    int getUserArticleSavedCounts(long userId) throws Exception;

    long createArticleLocalcategory(String localcategoryName, long userId)throws Exception;

    void deleteArticleLocalcategory(long id, long userId) throws Exception;

    void updateArticleLocalcategory(long id, String localcategoryName, long userId) throws Exception;

    List<ArticleLocalcategory> getLocalcategoryList(long userId);

}
