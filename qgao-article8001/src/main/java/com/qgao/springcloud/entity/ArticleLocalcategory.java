package com.qgao.springcloud.entity;

import java.io.Serializable;

/**
 * (ArticleLocalcategory)实体类
 *
 * @author makejava
 * @since 2021-05-29 19:31:06
 */
public class ArticleLocalcategory implements Serializable {
    private static final long serialVersionUID = 999461382199647143L;

    private Long id;

    private String localcategoryName;

    private Integer articleCount;

    private Long userId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalcategoryName() {
        return localcategoryName;
    }

    public void setLocalcategoryName(String localcategoryName) {
        this.localcategoryName = localcategoryName;
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
