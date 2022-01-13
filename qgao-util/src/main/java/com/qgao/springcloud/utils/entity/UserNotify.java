package com.qgao.springcloud.utils.entity;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * notify_type: subscribe --> notify_content: null
 * notify_type: article      --> notify_content: article_id
 * notify_type: comment --> notify_content: comment_id
 * notify_type: attitude   --> notify_content: good/bad:article/comment/reply:article_id/comment_id/reply_id(UserNotify)实体类
 *
 * @author makejava
 * @since 2021-06-24 20:31:10
 */
@NoArgsConstructor
public class UserNotify implements Serializable {
    private static final long serialVersionUID = 667813363592623291L;

    private Integer id;
    /**
     * 通知的类型：0subscribe,1article，2comment，3attitude点赞
     */
    private String notifyType;
    /**
     * 要通知谁
     */
    private Long notifyToUserId;
    /**
     * 谁操作导致的通知
     */
    private Long notifyFromUserId;
    /**
     * 根据通知类型的不同，存放的内容也不同，详情看上方“注释”标签
     */
    private String notifyContent;
    /**
     * 通知的时间，用来清理长期没有获取的通知
     */
    private Date notifyDate;

    public UserNotify(String notifyType, Long notifyToUserId, Long notifyFromUserId, String notifyContent, Date notifyDate) {
        this.notifyType = notifyType;
        this.notifyToUserId = notifyToUserId;
        this.notifyFromUserId = notifyFromUserId;
        this.notifyContent = notifyContent;
        this.notifyDate = notifyDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public Long getNotifyToUserId() {
        return notifyToUserId;
    }

    public void setNotifyToUserId(Long notifyToUserId) {
        this.notifyToUserId = notifyToUserId;
    }

    public Long getNotifyFromUserId() {
        return notifyFromUserId;
    }

    public void setNotifyFromUserId(Long notifyFromUserId) {
        this.notifyFromUserId = notifyFromUserId;
    }

    public String getNotifyContent() {
        return notifyContent;
    }

    public void setNotifyContent(String notifyContent) {
        this.notifyContent = notifyContent;
    }

    public Date getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(Date notifyDate) {
        this.notifyDate = notifyDate;
    }

}
