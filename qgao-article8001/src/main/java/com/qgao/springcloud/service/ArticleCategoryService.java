package com.qgao.springcloud.service;

import com.qgao.springcloud.entity.ArticleCategory;

import java.util.List;
import java.util.Map;

public interface ArticleCategoryService {

    List<ArticleCategory> getArticleCategories() throws Exception;
}
