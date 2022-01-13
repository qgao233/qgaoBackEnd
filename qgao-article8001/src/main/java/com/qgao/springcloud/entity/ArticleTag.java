package com.qgao.springcloud.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (ArticleTag)实体类
 *
 * @author makejava
 * @since 2021-05-29 19:31:44
 */
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTag implements Serializable {
    private static final long serialVersionUID = 259353716892619667L;

    private Long id;

    private String tagName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

}
