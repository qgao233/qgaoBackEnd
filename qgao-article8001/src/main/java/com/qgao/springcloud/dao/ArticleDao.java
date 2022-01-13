package com.qgao.springcloud.dao;

import com.qgao.springcloud.dto.ArticleSendDto;
import com.qgao.springcloud.entity.Article;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * (Article)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-05 20:43:39
 */
@Component
@Mapper
public interface ArticleDao {

    int insertSelective(Article article);

    int insertUnionArticleTag(Map<String,Object> paramsMap);

    int deleteByIdandUserId(Map<String,Object> paramsMap);

    @Delete("delete from union_article_tag where article_id = #{id}")
    int deleteUnionArticleTagById(Long id);


    @Select("select article_localcategory_id from article where id = #{id}")
    Long queryArticleLocalcategoryByArticleId(Long id);

    int updateArticle(Article article);

    ArticleSendDto queryArticleDetail(Article article);

    List<ArticleSendDto> queryArticleList(Map<String,Object> paramsMap);

    List<ArticleSendDto> userQueryArticleList(Map<String,Object> paramsMap);

    List<ArticleSendDto> queryArticleListBySearch(Map<String,Object> paramsMap);

    /*******其他******/

    @Update("update article set article_view_count=article_view_count+1 where id=#{id}")
    int updateArticleViewUp(Long id);

    @Select("select attitude from union_user_article where user_id=#{userId} and article_id=#{articleId}")
    String queryAttitudeToArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);

    @Update("update article set ${operationType}=${operationType}+1 where id=#{articleId}")
    int updateArticleAttitudeUp(@Param("articleId") Long articleId, @Param("operationType") String operationType);

    @Update("update article set ${operationType}=${operationType}-1 where id=#{articleId}")
    int updateArticleAttitudeDown(@Param("articleId") Long articleId, @Param("operationType") String operationType);

    @Delete("delete from union_user_article where user_id=#{userId} and article_id=#{articleId}")
    int deleteAttitudeToArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);

    @Insert("insert into union_user_article(user_id,article_id,operation) values(#{userId},#{articleId},#{operationType})")
    int insertAttitudeToArticle(@Param("userId") Long userId, @Param("articleId") Long articleId,@Param("operationType") String operationType);

    List<ArticleSendDto> queryRankOrder(Map<String,Object> paramsMap);

    @Insert("insert into union_article_storecategory(storecategory_id,article_id) values(#{storecategoryId},#{articleId})")
    int insertUnionArticleStorecategory(@Param("storecategoryId") long storecategoryId, @Param("articleId") long articleId);

    @Delete("delete from union_article_storecategory where storecategory_id=#{storecategoryId} and article_id=#{articleId}")
    int deleteUnionArticleStorecategory(@Param("storecategoryId") long storecategoryId, @Param("articleId") long articleId);

    @Update("update article set article_store_count = article_store_count + 1 where id=#{articleId}")
    int updateArticleStoreCountUp(long articleId);

    @Update("update article set article_store_count = article_store_count - 1 where id=#{articleId}")
    int updateArticleStoreCountDown(long articleId);

    @Select("select user_id from article where id=#{articleId}")
    long queryUserByArticleId(long articleId);

    @Update("update article set article_comment_count = article_comment_count + #{count} where id=#{articleId}")
    int updateArticleCommentCountUp(@Param("articleId") long articleId, @Param("count")int count);

    @Update("update article set article_comment_count = article_comment_count - #{count} where id=#{articleId}")
    int updateArticleCommentCountDown(@Param("articleId") long articleId, @Param("count")int count);


    /***************************以上是实际用到的***********************************/


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Article queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Article> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param article 实例对象
     * @return 对象列表
     */
    List<Article> queryAll(Article article);

    /**
     * 新增数据
     *
     * @param article 实例对象
     * @return 影响行数
     */
    int insert(Article article);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Article> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Article> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Article> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<Article> entities);

    /**
     * 修改数据
     *
     * @param article 实例对象
     * @return 影响行数
     */
    int update(Article article);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

