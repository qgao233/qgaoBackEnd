package com.qgao.springcloud.entity;

import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (UnionArticleImg)实体类
 *
 * @author makejava
 * @since 2021-06-05 15:16:32
 */
@NoArgsConstructor
public class UnionArticleImg implements Serializable {
    private static final long serialVersionUID = -50784996253612927L;

    private Integer id;

    private String articleImgId;

    private Long articleId;

    public UnionArticleImg(Long articleId){
        this.articleId = articleId;
    }

    public UnionArticleImg(String articleImgId, Long articleId){
        this.articleImgId = articleImgId;
        this.articleId = articleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArticleImgId() {
        return articleImgId;
    }

    public void setArticleImgId(String articleImgId) {
        this.articleImgId = articleImgId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

}
