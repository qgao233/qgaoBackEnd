package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.ArticleCommentDao;
import com.qgao.springcloud.dto.ArticleCommentReceiveDto;
import com.qgao.springcloud.dto.ArticleCommentSendDto;
import com.qgao.springcloud.entity.ArticleComment;
import com.qgao.springcloud.enums.ArticleCommentAttitudeEnum;
import com.qgao.springcloud.enums.ArticleCommentOrderEnum;
import com.qgao.springcloud.enums.ArticleCommentTypeEnum;
import com.qgao.springcloud.service.ArticleCommentService;
import com.qgao.springcloud.service.ArticleReplyService;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class ArticleCommentServiceImpl implements ArticleCommentService {


    @Resource
    private ArticleCommentDao articleCommentDao;
    @Resource
    private ArticleReplyService articleReplyService;

    @Override
    public long addArticleComment(ArticleCommentReceiveDto articleCommentReceiveDto, Long userId) throws Exception {
        log.debug("add article comment");

        ArticleComment articleComment = new ArticleComment();
        long commentId = SnowFlakeIdUtil.generateID();
        articleComment.setId(commentId);
        articleComment.setCommentContent(articleCommentReceiveDto.getComment_content());
        articleComment.setCommentTime(new Date());
        articleComment.setUserId(userId);
        articleComment.setArticleId(articleCommentReceiveDto.getArticle_id());

        if(articleCommentDao.insertSelective(articleComment) <= 0){
            throw new RuntimeException("insert article comment failure");
        }

        return commentId;
    }

    /*
    判断删除评论的是评论者本人,还是该评论对应的文章所属用户,这两者都可以删除评论
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteArticleComment(Long commentId, Long userId) throws Exception {
        log.debug("delete article comment.");

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("commentId",commentId);
        paramsMap.put("userId",userId);


        if(articleCommentDao.queryCommentByUserId(commentId,userId) > 0){
            //是评论者本人
        }else if(articleCommentDao.queryCommentByArticleUserId(paramsMap) > 0){
            //是该评论对应的文章所属用户
        }else{
            throw new RuntimeException("illegal delete article comment");
        }

        int rows = 0;
        if((rows = articleCommentDao.deleteCommentById(commentId)) <= 0){
            throw new RuntimeException("delete article comment failure");
        }


        //删除所有对应该comment_id的回复
        rows += articleReplyService.deleteArticleReplyList(commentId);

        return rows;
    }

    /*
    文章被删除后的操作
     */
    @Transactional(rollbackFor = {Exception.class})
    public void deleteArticleCommentList(Long articleId) throws Exception{
        List<Long> commentIds = articleCommentDao.queryCommentsByArticleId(articleId);

        //先删除回复
        for (Long commentId : commentIds){
            articleReplyService.deleteArticleReplyList(commentId);
        }
        //再删除评论
        articleCommentDao.deleteCommentsByArticleId(articleId);
    }

    @Override
    public long getArticleIdByCommentId(Long commentId) throws Exception {
        return articleCommentDao.queryArticleIdByCommentId(commentId);
    }

    @Override
    public List<ArticleCommentSendDto> getArticleCommentList(Long articleId, Integer page,Integer recordLimit, Long userId) throws Exception {
        log.debug("get article comment list.");
        //1.置顶(点赞降序) 2.热评(点赞降序) 3.普通(时间降序) 4.差评（点踩升序,不隐藏）

        int wastePage = 0;
        int pageOffset = 0;

        Map<String,Object> paramsMap = new HashMap<>();
        if(articleId != null)
            paramsMap.put("articleId",articleId);
        if(userId != null)
            paramsMap.put("userId",userId);
        int commentCount;
        int excludeSum = 0;
        int i = 0;      //当前页该取哪一个类型的评论
        ArticleCommentTypeEnum[] articleCommentTypeEnums = ArticleCommentTypeEnum.values();
        int len = articleCommentTypeEnums.length;
        for(; i < len; i++){
            paramsMap.put("commentType",articleCommentTypeEnums[i].getCode());
            commentCount = articleCommentDao.queryCommentCountsByType(paramsMap);
            excludeSum += commentCount;
            if(commentCount / recordLimit >= page - 1){
                excludeSum -= commentCount;
                wastePage = page - 1;
                break;
            }
        }

        int selfOffset; //获取到评论的类型后，该类型的评论该从哪里开始取
        int excludePage = excludeSum / recordLimit;
        if(excludeSum % recordLimit == 0){
            selfOffset = (wastePage - excludePage) * recordLimit;
        }else {
            selfOffset = (wastePage - excludePage) * recordLimit + (excludeSum % recordLimit);
        }

        //取评论
        paramsMap.put("recordLimit",recordLimit);
        String order = "desc";
        boolean first = true;
        List<ArticleCommentSendDto> articleCommentSendDtos = new ArrayList<>();
        List<ArticleCommentSendDto> tmpCommentSendDtos;
        for (; i < len; i++){
            if(i == len-1){
                order = "asc";
            }
            paramsMap.put("order",order);
            paramsMap.put("orderField",articleCommentTypeEnums[i].getField());
            if(first){
                paramsMap.put("offset",selfOffset);
                first = false;
            }
            paramsMap.put("offset",0);
            paramsMap.put("commentType",articleCommentTypeEnums[i].getCode());
            tmpCommentSendDtos = articleCommentDao.getArticleCommentList(paramsMap);
            articleCommentSendDtos.addAll(tmpCommentSendDtos);
            pageOffset += tmpCommentSendDtos.size();

            if(pageOffset >= recordLimit) break;

        }


        return articleCommentSendDtos;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateArticleCommentGoodOrBad(Long commentId, Long userId, String attitudeStr) throws Exception {
        log.debug("update good or bad: article comment");

        String attitude = articleCommentDao.queryAttitudeToArticleComment(userId, commentId);

        if(attitude != null){
            if(attitude.equals(ArticleCommentAttitudeEnum.getCode(attitudeStr))){
                articleCommentDao.updateArticleAttitudeCommentDown(commentId, ArticleCommentAttitudeEnum.getField(attitudeStr));
                articleCommentDao.deleteAttitudeToArticleComment(userId,commentId);
            }else {
                throw new RuntimeException("update good or bad failure, user has operated. commentId: "+commentId);
            }
        }else{
            articleCommentDao.updateArticleAttitudeCommentUp(commentId,ArticleCommentAttitudeEnum.getField(attitudeStr));
            articleCommentDao.insertAttitudeToArticleComment(userId,commentId,ArticleCommentAttitudeEnum.getCode(attitudeStr));
        }

        //判断该评论是热评还是差评
        int goodCriterion = 20;
        int badCriterion = 10;
        int normalCriterion = 5;
        ArticleCommentSendDto articleCommentSendDto = articleCommentDao.queryGoodAndBadCount(commentId);
        if(articleCommentSendDto.getComment_good_count() >= goodCriterion){
            articleCommentDao.updateArticleCommentType(commentId,ArticleCommentTypeEnum.GOOD.getCode());
        }else if(articleCommentSendDto.getComment_bad_count() >= badCriterion
                && articleCommentSendDto.getComment_good_count() <= normalCriterion){
            articleCommentDao.updateArticleCommentType(commentId,ArticleCommentTypeEnum.BAD.getCode());
        }else if(!articleCommentSendDto.getComment_type().equals(ArticleCommentTypeEnum.NORMAL)){
            articleCommentDao.updateArticleCommentType(commentId,ArticleCommentTypeEnum.NORMAL.getCode());
        }

    }

    @Override
    public List<ArticleCommentSendDto> getArticleCommentRank(int recordLimits, String orderField) throws Exception {
        log.debug("get article comment rank.");

        String orderKey = ArticleCommentOrderEnum.getOrderStr(orderField);

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("recordLimits",recordLimits);
        paramsMap.put("orderKey",orderKey);

        return articleCommentDao.queryRankOrder(paramsMap);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateReplyCount(Long commentId, String upOrDown) throws Exception {
        if(upOrDown.equals(REPLY_COUNT_UP)){
            articleCommentDao.updateReplyCountUp(commentId);
        }else if(upOrDown.equals(REPLY_COUNT_DOWN)){
            articleCommentDao.updateReplyCountDown(commentId);
        }
    }


}
