package com.qgao.springcloud.service;

public interface ArticleStorecategoryService {

    void updateArticleCountUp(long storecategoryId, long userId)throws Exception;

    void updateArticleCountDown(long storecategoryId, long userId)throws Exception;
}
