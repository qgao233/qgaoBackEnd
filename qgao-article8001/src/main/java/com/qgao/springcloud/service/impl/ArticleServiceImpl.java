package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.ArticleDao;
import com.qgao.springcloud.dto.ArticleReceiveDto;
import com.qgao.springcloud.dto.ArticleSendDto;
import com.qgao.springcloud.entity.Article;
import com.qgao.springcloud.entity.ArticleCategory;
import com.qgao.springcloud.entity.ArticleTag;
import com.qgao.springcloud.enums.*;
import com.qgao.springcloud.service.*;
import com.qgao.springcloud.utils.util.PageUtil;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import com.qgao.springcloud.utils.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService,BeanFactoryAware {

    @Resource
    private ArticleDao articleDao;

    @Resource
    private ArticleTagService articleTagService;
//    @Resource
//    private ArticleService articleService;
    @Resource
    private ArticleLocalcategoryService articleLocalcategoryService;
    @Resource
    private ArticleCategoryService articleCategoryService;
    @Resource
    private ArticleStorecategoryService articleStorecategoryService;

    private BeanFactory beanFactory;


    /**
     * 保存单表：  article表，tag表
       保存联合表：union_article_tag
       更改单表：  article_localcategory的个人分类下的文章数目
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public long createArticle(ArticleReceiveDto articleSaveDto, long userId)throws Exception {
        log.debug("insert: article.");

        //article实体
        Article article = new Article();
        //article类型
        article.setArticleType(ArticleTypeEnum.getCode(articleSaveDto.getArticle_type()));
        //article标题
        article.setArticleTitle(articleSaveDto.getArticle_title());
        //article内容
        article.setArticleContent(articleSaveDto.getArticle_content());
        //article参考url
        article.setArticleRefurl(articleSaveDto.getArticle_url());
        //用户id
        article.setUserId(userId);
        //文章id
        long articleId = SnowFlakeIdUtil.generateID(false,null);
        article.setId(articleId);
        //创建时间
        article.setArticleTime(new Date());
        //ip
        article.setArticleIp(articleSaveDto.getClient_ip());
        //文章预览图片
        article.setArticleImg(articleSaveDto.getArticle_img());
        //文章简介
        article.setArticleRemark(articleSaveDto.getArticle_remark());
        //文章状态
        article.setArticleState(ArticleStateEnum.getCode(articleSaveDto.getArticle_state()));
        //文章分类id
        article.setArticleCategoryId(Integer.parseInt(articleSaveDto.getPublic_type()));
        //文章个人分类id
        article.setArticleLocalcategoryId(Long.parseLong(articleSaveDto.getPrivate_type()));
        //article
        int rows;
        if((rows = articleDao.insertSelective(article)) <= 0) {
            throw new RuntimeException("insert article failure, affected rows: "+rows);
        }


        //article_tag
        List<ArticleTag> tags = articleTagService.createArticleTags(articleSaveDto);
        if(tags != null) {
            //union_article_tag
            ArticleService thisArticleService = (ArticleService)beanFactory.getBean("articleServiceImpl");
            thisArticleService.createUnionArticleTag(tags,articleId);
        }


        //article_localcategory
        articleLocalcategoryService.updateArticleCountUp(Long.parseLong(articleSaveDto.getPrivate_type()),userId);

        return articleId;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void createUnionArticleTag(List<ArticleTag> tags, long articleId)throws Exception {
        log.debug("insert: table union_articleTag.");

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("articleId",articleId);
        paramsMap.put("tags",tags);

        int rows;
        if((rows = articleDao.insertUnionArticleTag(paramsMap)) <= 0){
            throw new RuntimeException("insert articleTag failure, affected rows: "+rows);
        }
    }


    /**
     *
     * 先更新单表: article_localcategory相关个人分类的文章数目减一
     * 后删除单表: article,
     * 后删除联合表: union_article_tag
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public long removeArticleByArticleId(long articleId, long userId) throws Exception {
        log.debug("delete: table article.");

        Long localcategoryId = articleDao.queryArticleLocalcategoryByArticleId(articleId);
        if(localcategoryId == null){
            throw new RuntimeException("delete article failure, this record row was created illegally.");
        }
        articleLocalcategoryService.updateArticleCountDown(localcategoryId,userId);

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("id",articleId);
        paramsMap.put("userId",userId);

        int rows;
        if((rows = articleDao.deleteByIdandUserId(paramsMap))<=0){
            throw new RuntimeException("delete article failure, rows: "+rows);
        }
        ArticleService thisArticleService = (ArticleService)beanFactory.getBean("articleServiceImpl");
        thisArticleService.removeUnionArticleTag(articleId);

        return articleId;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removeUnionArticleTag(long articleId) throws Exception {
        log.debug("delete: table articleTag.");
        int rows;
        if((rows = articleDao.deleteUnionArticleTagById(articleId))<=0){
            throw new RuntimeException("delete articleTag failure, rows: "+rows);
        }

    }


    /**
     * 更新单表: article,
     * 更新单表: article_tag(在name字段上设置了唯一性索引,使用insert ignore into插入)
     * 更新单表: article_localcategory
     * 联合表(先删除,后插入): union_article_tag
     *
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public long updateArticleByArticleId(ArticleReceiveDto articleSaveDto, long articleId, long userId) throws Exception {
        log.debug("update: article.");

        //article_localcategory
        articleLocalcategoryService.updateArticleCountUpOrDown(articleSaveDto,articleId,userId);


        //article实体
        Article article = new Article();
        //article类型
        article.setArticleType(ArticleTypeEnum.getCode(articleSaveDto.getArticle_type()));
        //article标题
        article.setArticleTitle(articleSaveDto.getArticle_title());
        //article内容
        article.setArticleContent(articleSaveDto.getArticle_content());
        article.setArticleRefurl(articleSaveDto.getArticle_url());
        //用户id
        article.setUserId(userId);
        //文章id
        article.setId(articleId);
        //创建时间
        article.setArticleUpdateTime(new Date());
        //ip
        article.setArticleIp(articleSaveDto.getClient_ip());
        //文章预览图片
        article.setArticleImg(articleSaveDto.getArticle_img());
        //文章简介
        article.setArticleRemark(articleSaveDto.getArticle_remark());
        //文章状态
        article.setArticleState(ArticleStateEnum.getCode(articleSaveDto.getArticle_state()));
        //文章分类id
        article.setArticleCategoryId(Integer.parseInt(articleSaveDto.getPublic_type()));
        //文章个人分类id
        article.setArticleLocalcategoryId(Long.parseLong(articleSaveDto.getPrivate_type()));




        //article
        int rows;
        if((rows = articleDao.updateArticle(article)) <= 0) {
            throw new RuntimeException("update article failure, affected rows: "+rows);
        }

        //article_tag
        List<ArticleTag> tags = articleTagService.createArticleTags(articleSaveDto);


        if(tags != null) {
            //union_article_tag
            ArticleService thisArticleService = (ArticleService)beanFactory.getBean("articleServiceImpl");
            thisArticleService.removeUnionArticleTag(articleId);
            thisArticleService.createUnionArticleTag(tags,articleId);
        }


        return articleId;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateArticleViewUp(long articleId) throws Exception{
        log.debug("update view up: table article");

        int rows;
        if((rows = articleDao.updateArticleViewUp(articleId))<=0){
            throw new RuntimeException("update article view failure, articleId: "+articleId+", affected rows: "+rows);
        }
    }

    /**
     * 先更新阅读量
     * 再查article
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ArticleSendDto getArticleByArticleId(long articleId, String state,Long userId) throws Exception {
        log.debug("get: table article.");

        if(StringUtils.isNotEmpty(state) && state.equals("发布") && userId == null){
            //更新文章阅读次数
            ArticleService thisArticleService = (ArticleService)beanFactory.getBean("articleServiceImpl");
            thisArticleService.updateArticleViewUp(articleId);
        }


        Article article = new Article();
        article.setId(articleId);
        article.setUserId(userId);
        if(state != null)
            article.setArticleState(ArticleStateEnum.getCode(state));
        ArticleSendDto articleSendDto;
        articleSendDto = articleDao.queryArticleDetail(article);
        if(articleSendDto != null){
            articleSendDto.setArticleTypeStr(ArticleTypeEnum.getInfo(articleSendDto.getArticleType()));
            articleSendDto.setArticleStateStr((ArticleStateEnum.getInfo(articleSendDto.getArticleState())));
        }


        return articleSendDto;

    }

    /*
    先查询所有分类：article_category
    然后根据分类查找对应的文章list
     */
    @Override
    public Map<Integer, Object> getArticleViewList(int recordLimits, String state) throws Exception {
        log.debug("get: table article_category");

        Map<Integer, Object> idMap = new HashMap<>();
        Map<String, Object> nameMap;

        List<ArticleCategory> articleCategories = articleCategoryService.getArticleCategories();
        ArticleService thisArticleService = (ArticleService)beanFactory.getBean("articleServiceImpl");
        for(ArticleCategory category : articleCategories){
            nameMap = new HashMap<>();
            nameMap.put(category.getCategoryName(),thisArticleService.getArticleList(null,recordLimits,category.getId(),0,null,state));
            idMap.put(category.getId(),nameMap);
        }

        return idMap;
    }

    /*
    排序：默认降序
    涉及到的表：article, user, article_category, union_article_tag
     */
    @Override
    public List<ArticleSendDto> getArticleList(Long userId,int recordLimits, Integer categoryId, int page, String order, String state) throws Exception {
        log.debug("get: tables --> article, user, article_category, union_article_tag");

        String orderKey = ArticleOrderEnum.getOrderStr("时间");
        if(order != null)
            orderKey = ArticleOrderEnum.getOrderStr(order);


        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("userId",userId);
        paramsMap.put("recordLimits",recordLimits);
        paramsMap.put("categoryId",categoryId);
        paramsMap.put("offset", PageUtil.getOffset(page,recordLimits));
        paramsMap.put("orderKey",orderKey);
        if(state!=null)
            paramsMap.put("articleState",ArticleStateEnum.getCode(state));

        List<ArticleSendDto> articleSendDtos = articleDao.queryArticleList(paramsMap);
        for(ArticleSendDto a:articleSendDtos){
            a.setArticleTypeStr(ArticleTypeEnum.getInfo(a.getArticleType()));
            a.setArticleStateStr(ArticleStateEnum.getInfo(a.getArticleState()));
        }

        return articleSendDtos;
    }

    @Override
    public List<ArticleSendDto> userGetArticleList(int recordLimits, Long localcategoryId, int page, String order, String state,Long userId) throws Exception {
        log.debug("get: tables --> article, user, article_category, union_article_tag");

        String orderKey = ArticleOrderEnum.getOrderStr("时间");
        if(order != null)
            orderKey = ArticleOrderEnum.getOrderStr(order);

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("recordLimits",recordLimits);
        paramsMap.put("localcategoryId",localcategoryId);
        paramsMap.put("offset",PageUtil.getOffset(page,recordLimits));
        paramsMap.put("orderKey",orderKey);
        if(state!=null)
            paramsMap.put("articleState",ArticleStateEnum.getCode(state));
        paramsMap.put("userId",userId);

        List<ArticleSendDto> articleSendDtos = articleDao.userQueryArticleList(paramsMap);
        for(ArticleSendDto a:articleSendDtos){
            a.setArticleTypeStr(ArticleTypeEnum.getInfo(a.getArticleType()));
            a.setArticleStateStr(ArticleStateEnum.getInfo(a.getArticleState()));
        }

        return articleSendDtos;
    }

    @Override
    public List<ArticleSendDto> getArticleListBySearch(int recordLimits, Integer categoryId, int page, String order, String state, String searchKey) throws Exception {
        log.debug("get: tables --> article, user, article_category, union_article_tag");

        String orderKey = ArticleOrderEnum.getOrderStr("时间");
        if(order != null)
            orderKey = ArticleOrderEnum.getOrderStr(order);

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("recordLimits",recordLimits);
        paramsMap.put("categoryId",categoryId);
        paramsMap.put("offset",PageUtil.getOffset(page,recordLimits));
        paramsMap.put("orderKey",orderKey);
        if(state!=null)
            paramsMap.put("articleState",ArticleStateEnum.getCode(state));
        paramsMap.put("searchKey", searchKey);

        List<ArticleSendDto> articleSendDtos = articleDao.queryArticleListBySearch(paramsMap);
        for(ArticleSendDto a:articleSendDtos){
            a.setArticleTypeStr(ArticleTypeEnum.getInfo(a.getArticleType()));
            a.setArticleStateStr(ArticleStateEnum.getInfo(a.getArticleState()));
        }

        return articleSendDtos;
    }

    @Override
    public void updateArticleState(long articleId, long userId, String state) {
        log.debug("update state: article");

        Article article = new Article();
        article.setId(articleId);
        article.setUserId(userId);
        if(state!=null)
            article.setArticleState(ArticleStateEnum.getCode(state));
        int rows;
        if((rows = articleDao.updateArticle(article))<=0){
            throw new RuntimeException("update article state failure, articleId: "+articleId+", affected rows: "+rows);
        }
    }

    /*
    首先先根据userId和articleId查union_user_article有没有记录,
    1.有记录就对比入参type,相同就article对应记录的入参type减1,并删除记录,不相同就抛无法修改异常
    2.没记录就article对应记录的入参type加1,并添加记录
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateArticleGoodOrBad(long articleId, long userId, String attitudeStr) throws Exception {
        log.debug("update good or bad: article");

        String attitude = articleDao.queryAttitudeToArticle(userId,articleId);

        if(attitude != null){
            if(attitude.equals(ArticleAttitudeEnum.getCode(attitudeStr))){
                articleDao.updateArticleAttitudeDown(articleId, ArticleAttitudeEnum.getField(attitudeStr));
                articleDao.deleteAttitudeToArticle(userId,articleId);
            }else {
                throw new RuntimeException("update good or bad failure, user has operated. articleId: "+articleId);
            }
        }else{
            articleDao.updateArticleAttitudeUp(articleId,ArticleAttitudeEnum.getField(attitudeStr));
            articleDao.insertAttitudeToArticle(userId,articleId,ArticleAttitudeEnum.getCode(attitudeStr));
        }
    }

    @Override
    public List<ArticleSendDto> getArticleRank(String state, int recordLimits, String orderField) throws Exception{
        String articleState = ArticleStateEnum.getCode(state);
        String orderKey = ArticleOrderEnum.getOrderStr(orderField);
        String orderValue = StringUtil.lineToHump(orderKey);

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("articleState",articleState);
        paramsMap.put("recordLimits",recordLimits);
        paramsMap.put("orderKey",orderKey);
        paramsMap.put("orderValue",orderValue);

        return articleDao.queryRankOrder(paramsMap);
    }

    /*
    先更新用户收藏分类中文章的数量（可以顺便检验一下参数的合法性）：article_storecategory
    再插入或删除记录：union_article_storecategory
    最后再更新被收藏文章的收藏数：article
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void storeArticle(long articleId, long userId, long storecategoryId,String operate) throws Exception {
        log.debug("store article in "+ storecategoryId);


        if(operate.equals("收藏")){
            articleStorecategoryService.updateArticleCountUp(storecategoryId,userId);

            int rows;
            if((rows = articleDao.insertUnionArticleStorecategory(storecategoryId,articleId)) <= 0){
                throw new RuntimeException("insert unionArticleStorecategory failure, affected rows: "+rows);
            }

            if((rows = articleDao.updateArticleStoreCountUp(articleId)) <= 0){
                throw new RuntimeException("update article store count failure, affected rows: "+rows);
            }
        }else if(operate.equals("移除收藏")){
            articleStorecategoryService.updateArticleCountDown(storecategoryId,userId);

            int rows;
            if((rows = articleDao.deleteUnionArticleStorecategory(storecategoryId,articleId)) <= 0){
                throw new RuntimeException("insert unionArticleStorecategory failure, affected rows: "+rows);
            }

            if((rows = articleDao.updateArticleStoreCountDown(articleId)) <= 0){
                throw new RuntimeException("update article store count failure, affected rows: "+rows);
            }
        }else{
            throw new RuntimeException("params: operate is illegal.");
        }


    }

    @Override
    public int getUserArticleSavedCounts(long userId) throws Exception {
        return articleLocalcategoryService.getUserArticleSavedCounts(userId);
    }

    @Override
    public long getUserByArticleId(long articleId) throws Exception {
        return articleDao.queryUserByArticleId(articleId);
    }

    @Override
    public void updateArticleCommentCount(long articleId, int count, String plusOrMinus) throws Exception {
        if(plusOrMinus.equals("plus")){
            articleDao.updateArticleCommentCountUp(articleId,count);
        }else{
            articleDao.updateArticleCommentCountDown(articleId,count);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
