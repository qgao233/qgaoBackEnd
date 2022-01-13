package com.qgao.springcloud.service;

import com.qgao.springcloud.dto.ArticleCommentReceiveDto;
import com.qgao.springcloud.dto.ArticleCommentSendDto;

import java.util.List;

public interface ArticleCommentService {

    String REPLY_COUNT_UP = "up";
    String REPLY_COUNT_DOWN = "down";

    long addArticleComment(ArticleCommentReceiveDto articleCommentReceiveDto, Long userId)throws Exception;

    int deleteArticleComment(Long commentId, Long userId)throws Exception;

    void deleteArticleCommentList(Long articleId) throws Exception;

    long getArticleIdByCommentId(Long commentId) throws Exception;

    List<ArticleCommentSendDto> getArticleCommentList(Long articleId, Integer page, Integer recordLimit, Long userId)throws Exception;

    void updateArticleCommentGoodOrBad(Long commentId, Long userId, String attitudeStr) throws Exception;

    List<ArticleCommentSendDto> getArticleCommentRank(int recordLimits, String orderField) throws Exception;

    void updateReplyCount(Long commentId,String upOrDown)throws Exception ;


}
