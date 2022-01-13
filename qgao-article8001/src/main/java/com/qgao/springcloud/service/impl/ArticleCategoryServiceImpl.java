package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.ArticleCategoryDao;
import com.qgao.springcloud.entity.ArticleCategory;
import com.qgao.springcloud.service.ArticleCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Resource
    private ArticleCategoryDao articleCategoryDao;



    @Override
    public List<ArticleCategory> getArticleCategories() throws Exception {
        return articleCategoryDao.queryAll(null);
    }
}
