package com.qgao.springcloud.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (ArticleReply)实体类
 *
 * @author makejava
 * @since 2021-06-07 14:09:37
 */
public class ArticleReply implements Serializable {
    private static final long serialVersionUID = 669416301237814268L;

    private Long id;

    private String replyContent;

    private Integer replyGoodCount;

    private Integer replyBadCount;

    private Date replyTime;

    private String replyIp;
    /**
     * 回复的是'0'评论，还是'1'回复
     */
    private String replyType;
    /**
     * 回复的可能是reply_id，也可能是comment_id
     */
    private Long replyId;
    /**
     * 哪个用户回复的
     */
    private Long userId;
    /**
     * 这条回复是属于哪条评论的
     */
    private Long commentId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public Integer getReplyGoodCount() {
        return replyGoodCount;
    }

    public void setReplyGoodCount(Integer replyGoodCount) {
        this.replyGoodCount = replyGoodCount;
    }

    public Integer getReplyBadCount() {
        return replyBadCount;
    }

    public void setReplyBadCount(Integer replyBadCount) {
        this.replyBadCount = replyBadCount;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyIp() {
        return replyIp;
    }

    public void setReplyIp(String replyIp) {
        this.replyIp = replyIp;
    }

    public String getReplyType() {
        return replyType;
    }

    public void setReplyType(String replyType) {
        this.replyType = replyType;
    }

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

}
