package com.qgao.springcloud.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (Article)实体类
 *
 * @author makejava
 * @since 2021-06-05 20:43:39
 */
public class Article implements Serializable {
    private static final long serialVersionUID = 891807147782952643L;
    /**
     * 文章id，采用雪花算法生成
     */
    private Long id;
    /**
     * 文章类型：0为原创，1为转载，2为翻译
     */
    private String articleType;
    /**
     * 文章状态：0为垃圾箱，1为草稿箱，2为审核，3为已发布
     */
    private String articleState;
    /**
     * 文章模式:0为私有，1为公开，2为仅粉丝查看
     */
    private String articleMode;
    /**
     * 是否置顶:0为否，1为是
     */
    private String articleTop;
    /**
     * '0'允许评论，'1'不允许评论
     */
    private String articleAllowComment;
    /**
     * 文章标题
     */
    private String articleTitle;
    /**
     * 文章内容10921个字
     */
    private String articleContent;
    /**
     * 参考的网址
     */
    private String articleRefurl;
    /**
     * 文章创建时间
     */
    private Date articleTime;
    /**
     * 文章创建地点
     */
    private String articleIp;
    /**
     * 文章更新时间
     */
    private Date articleUpdateTime;
    /**
     * 文章删除时间
     */
    private Date articleDropTime;
    /**
     * 文章观看(点击)次数
     */
    private Integer articleViewCount;
    /**
     * 称赞次数
     */
    private Integer articleGoodCount;
    /**
     * 不屑次数
     */
    private Integer articleBadCount;
    /**
     * 评论数量
     */
    private Integer articleCommentCount;
    /**
     * 收藏次数
     */
    private Integer articleStoreCount;

    private String articleImg;
    /**
     * 文章概览，从文章内容截取的一部分
     */
    private String articleRemark;
    /**
     * 逻辑外键：分类id
     */
    private Integer articleCategoryId;
    /**
     * 逻辑外键：个人分类id
     */
    private Long articleLocalcategoryId;
    /**
     * 逻辑外键:文章关联的用户id
     */
    private Long userId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getArticleState() {
        return articleState;
    }

    public void setArticleState(String articleState) {
        this.articleState = articleState;
    }

    public String getArticleMode() {
        return articleMode;
    }

    public void setArticleMode(String articleMode) {
        this.articleMode = articleMode;
    }

    public String getArticleTop() {
        return articleTop;
    }

    public void setArticleTop(String articleTop) {
        this.articleTop = articleTop;
    }

    public String getArticleAllowComment() {
        return articleAllowComment;
    }

    public void setArticleAllowComment(String articleAllowComment) {
        this.articleAllowComment = articleAllowComment;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticleRefurl() {
        return articleRefurl;
    }

    public void setArticleRefurl(String articleRefurl) {
        this.articleRefurl = articleRefurl;
    }

    public Date getArticleTime() {
        return articleTime;
    }

    public void setArticleTime(Date articleTime) {
        this.articleTime = articleTime;
    }

    public String getArticleIp() {
        return articleIp;
    }

    public void setArticleIp(String articleIp) {
        this.articleIp = articleIp;
    }

    public Date getArticleUpdateTime() {
        return articleUpdateTime;
    }

    public void setArticleUpdateTime(Date articleUpdateTime) {
        this.articleUpdateTime = articleUpdateTime;
    }

    public Date getArticleDropTime() {
        return articleDropTime;
    }

    public void setArticleDropTime(Date articleDropTime) {
        this.articleDropTime = articleDropTime;
    }

    public Integer getArticleViewCount() {
        return articleViewCount;
    }

    public void setArticleViewCount(Integer articleViewCount) {
        this.articleViewCount = articleViewCount;
    }

    public Integer getArticleGoodCount() {
        return articleGoodCount;
    }

    public void setArticleGoodCount(Integer articleGoodCount) {
        this.articleGoodCount = articleGoodCount;
    }

    public Integer getArticleBadCount() {
        return articleBadCount;
    }

    public void setArticleBadCount(Integer articleBadCount) {
        this.articleBadCount = articleBadCount;
    }

    public Integer getArticleCommentCount() {
        return articleCommentCount;
    }

    public void setArticleCommentCount(Integer articleCommentCount) {
        this.articleCommentCount = articleCommentCount;
    }

    public Integer getArticleStoreCount() {
        return articleStoreCount;
    }

    public void setArticleStoreCount(Integer articleStoreCount) {
        this.articleStoreCount = articleStoreCount;
    }

    public String getArticleImg() {
        return articleImg;
    }

    public void setArticleImg(String articleImg) {
        this.articleImg = articleImg;
    }

    public String getArticleRemark() {
        return articleRemark;
    }

    public void setArticleRemark(String articleRemark) {
        this.articleRemark = articleRemark;
    }

    public Integer getArticleCategoryId() {
        return articleCategoryId;
    }

    public void setArticleCategoryId(Integer articleCategoryId) {
        this.articleCategoryId = articleCategoryId;
    }

    public Long getArticleLocalcategoryId() {
        return articleLocalcategoryId;
    }

    public void setArticleLocalcategoryId(Long articleLocalcategoryId) {
        this.articleLocalcategoryId = articleLocalcategoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
