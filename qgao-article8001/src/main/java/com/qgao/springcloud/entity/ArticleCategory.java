package com.qgao.springcloud.entity;

import java.io.Serializable;

/**
 * (ArticleCategory)实体类
 *
 * @author makejava
 * @since 2021-06-01 15:53:27
 */
public class ArticleCategory implements Serializable {
    private static final long serialVersionUID = 109176795969726949L;

    private Integer id;
    /**
     * 分类名
     */
    private String categoryName;

    private String categoryDescription;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

}
