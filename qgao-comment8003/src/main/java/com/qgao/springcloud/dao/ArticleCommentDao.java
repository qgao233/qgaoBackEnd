package com.qgao.springcloud.dao;

import com.qgao.springcloud.dto.ArticleCommentSendDto;
import com.qgao.springcloud.entity.ArticleComment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * (ArticleComment)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-05 21:34:40
 */

@Component
@Mapper
public interface ArticleCommentDao {

    int insertSelective(ArticleComment articleComment);

    @Select("select count(id) from article_comment where id=#{commentId} and user_id=#{userId}")
    int queryCommentByUserId(@Param("commentId")Long commentId,@Param("userId")Long userId);

    int queryCommentByArticleUserId(Map<String,Object> paramsMap);

    @Delete("delete from article_comment where id=#{commentId}")
    int deleteCommentById(Long commentId);

    @Select("select id from article_comment where article_id=#{articleId}")
    List<Long> queryCommentsByArticleId(Long articleId);

    @Delete("delete from article_comment where article_id=#{articleId}")
    int deleteCommentsByArticleId(Long articleId);

    @Select("select article_id from article_comment where id=#{commentId}")
    long queryArticleIdByCommentId(Long commentId);

    int queryCommentCountsByType(Map<String,Object> paramsMap);

    List<ArticleCommentSendDto> getArticleCommentList(Map<String,Object> paramsMap);

    @Select("select attitude from union_user_articlecomment where user_id=#{userId} and comment_id=#{commentId}")
    String queryAttitudeToArticleComment(@Param("userId") Long userId, @Param("commentId") Long commentId);

    @Update("update article_comment set ${operationType}=${operationType}+1 where id=#{commentId}")
    int updateArticleAttitudeCommentUp(@Param("articleId") Long commentId, @Param("operationType") String operationType);

    @Update("update article_comment set ${operationType}=${operationType}-1 where id=#{commentId}")
    int updateArticleAttitudeCommentDown(@Param("articleId") Long commentId, @Param("operationType") String operationType);

    @Delete("delete from union_user_articlecomment where user_id=#{userId} and comment_id=#{commentId}")
    int deleteAttitudeToArticleComment(@Param("userId") Long userId, @Param("articleId") Long commentId);

    @Insert("insert into union_user_articlecomment(user_id,comment_id,operation) values(#{userId},#{commentId},#{operationType})")
    int insertAttitudeToArticleComment(@Param("userId") Long userId, @Param("articleId") Long commentId,@Param("operationType") String operationType);

    @Select("select comment_type,comment_good_count,comment_bad_count from article_comment where id=#{commentId}")
    ArticleCommentSendDto queryGoodAndBadCount(Long commentId);

    @Update("update article_comment set comment_type=#{commentType} where id=#{commentId}")
    int updateArticleCommentType(@Param("commentId") Long commentId, @Param("commentType") String commentType);

    List<ArticleCommentSendDto> queryRankOrder(Map<String,Object> paramsMap);

    @Update("update article_comment set comment_reply_count=comment_reply_count+1 where id=#{commentId}")
    int updateReplyCountUp(Long commentId);

    @Update("update article_comment set comment_reply_count=comment_reply_count-1 where id=#{commentId}")
    int updateReplyCountDown(Long commentId);


    /*************************上面是实际用到的******************************/

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ArticleComment queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ArticleComment> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param articleComment 实例对象
     * @return 对象列表
     */
    List<ArticleComment> queryAll(ArticleComment articleComment);

    /**
     * 新增数据
     *
     * @param articleComment 实例对象
     * @return 影响行数
     */
    int insert(ArticleComment articleComment);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleComment> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<ArticleComment> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleComment> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<ArticleComment> entities);

    /**
     * 修改数据
     *
     * @param articleComment 实例对象
     * @return 影响行数
     */
    int update(ArticleComment articleComment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

