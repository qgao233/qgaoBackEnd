package com.qgao.springcloud.service;

import com.qgao.springcloud.dto.ArticleReplyReceiveDto;
import com.qgao.springcloud.dto.ArticleReplySendDto;

import java.util.List;

public interface ArticleReplyService {

    long addArticleReply(ArticleReplyReceiveDto articleReplyReceiveDto, Long userId)throws Exception;

    long deleteArticleReply(Long replyId, Long userId) throws Exception;

    int deleteArticleReplyList(Long commentId) throws Exception;

    List<ArticleReplySendDto> getArticleReplyList(Long commentId,Integer page,Integer recordLimit) throws Exception;

    void updateArticleReplyGoodOrBad(Long replyId, Long userId, String attitudeStr) throws Exception;
}
