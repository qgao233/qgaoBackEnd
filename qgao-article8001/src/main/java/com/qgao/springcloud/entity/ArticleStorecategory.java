package com.qgao.springcloud.entity;

import java.io.Serializable;

/**
 * 个人的收藏分类(ArticleStorecategory)实体类
 *
 * @author makejava
 * @since 2021-06-02 13:22:56
 */
public class ArticleStorecategory implements Serializable {
    private static final long serialVersionUID = -78911193773617184L;

    private Long id;
    /**
     * 收藏分类name
     */
    private String storecategoryName;
    /**
     * 当前收藏分类下，收藏的文章数量
     */
    private Integer articleCount;

    private Long userId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStorecategoryName() {
        return storecategoryName;
    }

    public void setStorecategoryName(String storecategoryName) {
        this.storecategoryName = storecategoryName;
    }

    public Integer getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Integer articleCount) {
        this.articleCount = articleCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
