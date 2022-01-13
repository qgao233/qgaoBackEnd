package com.qgao.springcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 后台发给前台的article实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSendDto implements Serializable{

    private static final long serialVersionUID = 109176795969726949L;

    private Long    articleId;
    private String  articleTitle;
    private String  articleType;
    private String  articleTypeStr;  //额外处理
    private String  articleState;
    private String  articleStateStr;
    private Date    articleTime;
    //概览
    private String  articleImg;
    private String  articleRemark;
    //详细
    private String  articleContent;
    private String  articleRefurl;

    private Integer articleViewCount;
    private Integer articleGoodCount;
    private Integer articleBadCount;
    private Integer articleStoreCount;
    private Integer articleCommentCount;

    //一对一
    private String  categoryId;
    private String  categoryName;
    private String  localcategoryId;
    private String  localcategoryName;
    private Long    userId;
    private String  userImg;
    private String  nickname;


    //多对多
    private String tagIds;
    private String tagNames;
}
