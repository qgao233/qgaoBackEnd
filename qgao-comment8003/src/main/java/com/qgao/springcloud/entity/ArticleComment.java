package com.qgao.springcloud.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (ArticleComment)实体类
 *
 * @author makejava
 * @since 2021-06-05 21:34:37
 */
public class ArticleComment implements Serializable {
    private static final long serialVersionUID = -22396755367619398L;

    private Long id;

    private String commentContent;

    private Date commentTime;

    private String commentIp;
    /**
     * '0'置顶，'1'热评，'2'普通，'3'差评
     */
    private String commentType;

    private Integer commentGoodCount;

    private Integer commentBadCount;

    private Integer commentReplyCount;

    private Long userId;

    private Long articleId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentIp() {
        return commentIp;
    }

    public void setCommentIp(String commentIp) {
        this.commentIp = commentIp;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public Integer getCommentGoodCount() {
        return commentGoodCount;
    }

    public void setCommentGoodCount(Integer commentGoodCount) {
        this.commentGoodCount = commentGoodCount;
    }

    public Integer getCommentBadCount() {
        return commentBadCount;
    }

    public void setCommentBadCount(Integer commentBadCount) {
        this.commentBadCount = commentBadCount;
    }

    public Integer getCommentReplyCount() {
        return commentReplyCount;
    }

    public void setCommentReplyCount(Integer commentReplyCount) {
        this.commentReplyCount = commentReplyCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

}
