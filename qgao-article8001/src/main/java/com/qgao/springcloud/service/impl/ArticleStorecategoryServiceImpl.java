package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.ArticleStorecategoryDao;
import com.qgao.springcloud.service.ArticleStorecategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class ArticleStorecategoryServiceImpl implements ArticleStorecategoryService {

    @Resource
    private ArticleStorecategoryDao articleStorecategoryDao;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateArticleCountUp(long storecategoryId, long userId) throws Exception {
        log.debug("update: table articleStorecategory: count up.");

        int rows;
        if((rows = articleStorecategoryDao.updateCountUpById(storecategoryId,userId)) <= 0){
            throw new RuntimeException("update article count in storecategory failure, affected rows: "+rows);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateArticleCountDown(long storecategoryId, long userId) throws Exception {
        log.debug("update: table articleStorecategory: count down.");

        int rows;
        if((rows = articleStorecategoryDao.updateCountDownById(storecategoryId,userId)) <= 0){
            throw new RuntimeException("update article count in storecategory failure, affected rows: "+rows);
        }
    }
}
