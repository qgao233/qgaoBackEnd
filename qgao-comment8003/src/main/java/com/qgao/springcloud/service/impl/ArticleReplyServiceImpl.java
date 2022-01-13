package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.ArticleReplyDao;
import com.qgao.springcloud.dto.ArticleReplyReceiveDto;
import com.qgao.springcloud.dto.ArticleReplySendDto;
import com.qgao.springcloud.entity.ArticleReply;
import com.qgao.springcloud.enums.ArticleReplyAttitudeEnum;
import com.qgao.springcloud.enums.ArticleReplyTypeEnum;
import com.qgao.springcloud.service.ArticleCommentService;
import com.qgao.springcloud.service.ArticleReplyService;
import com.qgao.springcloud.utils.util.PageUtil;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ArticleReplyServiceImpl implements ArticleReplyService {

    @Resource
    private ArticleReplyDao articleReplyDao;

    @Resource
    private ArticleCommentService articleCommentService;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public long addArticleReply(ArticleReplyReceiveDto articleReplyReceiveDto, Long userId) throws Exception {
        log.debug("add article reply");

        ArticleReply articleReply = new ArticleReply();
        long replyId = SnowFlakeIdUtil.generateID();
        articleReply.setId(replyId);
        articleReply.setReplyContent(articleReplyReceiveDto.getReply_content());
        articleReply.setReplyTime(new Date());
        articleReply.setReplyIp(articleReplyReceiveDto.getReply_ip());
        if(articleReplyDao.queryIsReplyExist(articleReplyReceiveDto.getReply_id()) > 0){
            articleReply.setReplyType(ArticleReplyTypeEnum.REPLY.getCode());
        }else{
            articleReply.setReplyType(ArticleReplyTypeEnum.COMMENT.getCode());
        }
        articleReply.setReplyId(articleReplyReceiveDto.getReply_id());
        articleReply.setCommentId(articleReplyReceiveDto.getComment_id());
        articleReply.setUserId(userId);


        if(articleReplyDao.insertSelective(articleReply) <= 0){
            throw new RuntimeException("insert article reply failure");
        }

        //更新article_comment表的reply_count
        articleCommentService.updateReplyCount(articleReplyReceiveDto.getComment_id(),ArticleCommentService.REPLY_COUNT_UP);

        return replyId;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public long deleteArticleReply(Long replyId, Long userId) throws Exception {
        log.debug("delete article reply.");

        //如果没有该commentId则为非法删除
        Long commentId = articleReplyDao.queryCommentId(replyId);
        if(commentId == null){
            throw new RuntimeException("reply is illegal.");
        }

        Map<String,Object> paramsMap = new HashMap<>(2);
        paramsMap.put("replyId",replyId);
        paramsMap.put("userId",userId);

        if(articleReplyDao.queryReplyByUserId(replyId,userId) > 0){
            //回复本人
        }else if(articleReplyDao.queryReplyByCommentUserId(paramsMap) > 0){
            //回复对应的评论用户
        }else if(articleReplyDao.queryReplyByArticleUserId(paramsMap) > 0){
            //回复对应的文章用户
        }else{
            throw new RuntimeException("illegal delete article reply");
        }

        if(articleReplyDao.deleteReplyById(replyId) <= 0){
            throw new RuntimeException("delete article reply failure");
        }

        //更新article_comment表的reply_count
        articleCommentService.updateReplyCount(commentId,ArticleCommentService.REPLY_COUNT_DOWN);

        return commentId;
    }

    @Transactional(rollbackFor = {Exception.class})
    public int deleteArticleReplyList(Long commentId) throws Exception{
        return articleReplyDao.deleteAllByCommentId(commentId);
    }

    @Override
    public List<ArticleReplySendDto> getArticleReplyList(Long commentId, Integer page, Integer recordLimit) throws Exception {
        log.debug("get article reply list");

        int offset = PageUtil.getOffset(page,recordLimit);

        Map<String, Object> paramsMap = new HashMap<>(3);
        paramsMap.put("commentId",commentId);
        paramsMap.put("offset",offset);
        paramsMap.put("recordLimit",recordLimit);

        List<ArticleReplySendDto> articleReplySendDtos = articleReplyDao.getArticleReplyList(paramsMap);
        for (ArticleReplySendDto articleReplySendDto : articleReplySendDtos){
            articleReplySendDto.setReply_type_str(ArticleReplyTypeEnum.getInfo(articleReplySendDto.getReply_type()));
        }

        return articleReplySendDtos;
    }

    @Override
    public void updateArticleReplyGoodOrBad(Long replyId, Long userId, String attitudeStr) throws Exception {
        log.debug("update good or bad: article reply");

        String attitude = articleReplyDao.queryAttitudeToArticleReply(userId, replyId);

        if(attitude != null){
            if(attitude.equals(ArticleReplyAttitudeEnum.getCode(attitudeStr))){
                articleReplyDao.updateArticleAttitudeReplyDown(replyId, ArticleReplyAttitudeEnum.getField(attitudeStr));
                articleReplyDao.deleteAttitudeToArticleReply(userId,replyId);
            }else {
                throw new RuntimeException("update good or bad failure, user has operated. replyId: "+replyId);
            }
        }else{
            articleReplyDao.updateArticleAttitudeReplyUp(replyId,ArticleReplyAttitudeEnum.getField(attitudeStr));
            articleReplyDao.insertAttitudeToArticleReply(userId,replyId,ArticleReplyAttitudeEnum.getCode(attitudeStr));
        }
    }
}
