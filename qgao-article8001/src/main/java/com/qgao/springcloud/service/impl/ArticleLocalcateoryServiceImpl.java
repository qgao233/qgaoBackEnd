package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.ArticleDao;
import com.qgao.springcloud.dao.ArticleLocalcategoryDao;
import com.qgao.springcloud.dto.ArticleReceiveDto;
import com.qgao.springcloud.entity.ArticleLocalcategory;
import com.qgao.springcloud.service.ArticleLocalcategoryService;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class ArticleLocalcateoryServiceImpl implements ArticleLocalcategoryService,BeanFactoryAware {

    @Resource
    private ArticleDao articleDao;
    @Resource
    private ArticleLocalcategoryDao articleLocalcategoryDao;
//    @Resource
//    private ArticleLocalcategoryService articleLocalcategoryService;


    private BeanFactory beanFactory;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateArticleCountUp(long id,long userId) throws Exception{
        log.debug("update: table articleLocalcategory: count up.");

        int rows;
        if((rows = articleLocalcategoryDao.updateCountUpById(id,userId)) <= 0){
            throw new RuntimeException("update article count in localcategory failure, affected rows: "+rows);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateArticleCountDown(long id,long userId) throws Exception {
        log.debug("update: table articleLocalcategory: count down.");

        int rows;
        if((rows = articleLocalcategoryDao.updateCountDownById(id,userId)) <= 0){
            throw new RuntimeException("update article count in localcategory failure, affected rows: "+rows);
        }
    }

    /**
     * 先找出旧的localcategory,然后和新的对比,旧的不重复的-1,新的不重复的+1
     * @param articleSaveDto
     * @param userId
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateArticleCountUpOrDown(ArticleReceiveDto articleSaveDto, long articleId, long userId) throws Exception {
        log.debug("update: table articleLocalcategory: count up or down.");


        Long oldLocalcategoryId = articleDao.queryArticleLocalcategoryByArticleId(articleId);
        if(oldLocalcategoryId == null)
            throw new RuntimeException("query union_article_localcategory failure, this record row was created illegally.");

        Long newLocalcategoryId = Long.parseLong(articleSaveDto.getPrivate_type());

        ArticleLocalcategoryService thisArticleLocalcategoryService = (ArticleLocalcategoryService)beanFactory.getBean("articleLocalcategoryServiceImpl");
        if((long)oldLocalcategoryId != (long) newLocalcategoryId){
            thisArticleLocalcategoryService.updateArticleCountDown(oldLocalcategoryId,userId);
            thisArticleLocalcategoryService.updateArticleCountUp(newLocalcategoryId,userId);
        }

//        Set<Long> oldLocalIds = new HashSet<>(oldLocalcategoryIds);
//        Set<Long> newLocalIds = new HashSet<>();
//        String[] oldLocalcategoryIdsStr = articleSaveDto.getPrivate_type().split(",");
//        for (String str:oldLocalcategoryIdsStr){
//            newLocalIds.add(Long.parseLong(str));
//        }
//
//        //找重复的
//        Set<Long> sameLocalIds = new HashSet<>();
//
//        Iterator old = oldLocalIds.iterator();
//        while (old.hasNext()){
//            Long item = (Long)old.next();
//            if(newLocalIds.contains(item)){
//                sameLocalIds.add(item);
//            }
//        }
//
//        //移除重复的
//        oldLocalIds.removeAll(sameLocalIds);
//        newLocalIds.removeAll(sameLocalIds);
//
//        old = oldLocalIds.iterator();
//        while (old.hasNext()){
//            articleLocalcategoryService.updateArticleCountDown((Long)old.next());
//        }
//
//        old = newLocalIds.iterator();
//        while (old.hasNext()){
//            articleLocalcategoryService.updateArticleCountUp((Long)old.next());
//        }
    }

    @Override
    public int getUserArticleSavedCounts(long userId) throws Exception {
        return articleLocalcategoryDao.queryArticleCountSum(userId);
    }

    @Override
    public long createArticleLocalcategory(String localcategoryName, long userId)throws Exception {
        log.debug("create: article localcategory");

        long id = SnowFlakeIdUtil.generateID();
        ArticleLocalcategory articleLocalcategory = new ArticleLocalcategory();
        articleLocalcategory.setId(id);
        articleLocalcategory.setLocalcategoryName(localcategoryName);
        articleLocalcategory.setUserId(userId);

        int rows;
        if((rows = articleLocalcategoryDao.insertSelective(articleLocalcategory)) <= 0){
            throw new RuntimeException("insert localcategory failure, affected rows: "+rows);
        }

        return id;
    }

    /*
    删之前先查一下有没有文章属于这个个人分类，如果有，不能删(写在sql判断中了)
     */
    @Override
    public void deleteArticleLocalcategory(long id, long userId) throws Exception {
        log.debug("delete: article localcategory");

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("id",id);
        paramsMap.put("userId",userId);

        int rows;
        if((rows = articleLocalcategoryDao.deleteByIdAndUserId(paramsMap)) <= 0){
            throw new RuntimeException("delete localcategory failure, affected rows: "+rows);
        }
    }

    @Override
    public void updateArticleLocalcategory(long id, String localcategoryName, long userId) throws Exception {
        log.debug("update: article localcategory");

        ArticleLocalcategory articleLocalcategory = new ArticleLocalcategory();
        articleLocalcategory.setId(id);
        articleLocalcategory.setLocalcategoryName(localcategoryName);
        articleLocalcategory.setUserId(userId);

        int rows;
        if((rows = articleLocalcategoryDao.update(articleLocalcategory)) <= 0){
            throw new RuntimeException("update localcategory failure, affected rows: "+rows);
        }
    }

    @Override
    public List<ArticleLocalcategory> getLocalcategoryList(long userId) {
        log.debug("update: article localcategory");

        ArticleLocalcategory articleLocalcategory = new ArticleLocalcategory();
        articleLocalcategory.setUserId(userId);

        return articleLocalcategoryDao.queryAll(articleLocalcategory);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
