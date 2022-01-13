package com.qgao.springcloud.dao;

import com.qgao.springcloud.dto.ArticleReplySendDto;
import com.qgao.springcloud.entity.ArticleReply;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * (ArticleReply)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-07 14:09:38
 */
@Component
@Mapper
public interface ArticleReplyDao {

    @Select("select count(id) from article_reply where id=#{replyId}")
    int queryIsReplyExist(Long replyId);

    int insertSelective(ArticleReply articleReply);

    @Delete("delete from article_reply where id=#{replyId}")
    int deleteReplyById(Long replyId);

    @Delete("delete from article_reply where comment_id=#{commentId}")
    int deleteAllByCommentId(Long commentId);

    @Select("select comment_id from article_reply where id=#{replyId}")
    Long queryCommentId(Long replyId);

    @Select("select count(id) from article_reply where id=#{replyId} and user_id=#{userId}")
    int queryReplyByUserId(@Param("replyId") Long replyId, @Param("userId") Long userId);

    int queryReplyByCommentUserId(Map<String,Object> paramsMap);

    int queryReplyByArticleUserId(Map<String,Object> paramsMap);

    List<ArticleReplySendDto> getArticleReplyList(Map<String,Object> paramsMap);

    @Select("select attitude from union_user_articlereply where user_id=#{userId} and reply_id=#{replyId}")
    String queryAttitudeToArticleReply(@Param("userId")Long userId, @Param("replyId")Long replyId);

    @Update("update article_reply set ${operationType}=${operationType}+1 where id=#{replyId}")
    int updateArticleAttitudeReplyUp(@Param("articleId") Long replyId, @Param("operationType") String operationType);

    @Update("update article_reply set ${operationType}=${operationType}-1 where id=#{replyId}")
    int updateArticleAttitudeReplyDown(@Param("articleId") Long replyId, @Param("operationType") String operationType);

    @Delete("delete from union_user_articlereply where user_id=#{userId} and reply_id=#{replyId}")
    int deleteAttitudeToArticleReply(@Param("userId") Long userId, @Param("articleId") Long replyId);

    @Insert("insert into union_user_articlereply(user_id,reply_id,operation) values(#{userId},#{replyId},#{operationType})")
    int insertAttitudeToArticleReply(@Param("userId") Long userId, @Param("articleId") Long replyId,@Param("operationType") String operationType);


    /**************************上面是实际用到的****************************/

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ArticleReply queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ArticleReply> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param articleReply 实例对象
     * @return 对象列表
     */
    List<ArticleReply> queryAll(ArticleReply articleReply);

    /**
     * 新增数据
     *
     * @param articleReply 实例对象
     * @return 影响行数
     */
    int insert(ArticleReply articleReply);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleReply> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<ArticleReply> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleReply> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<ArticleReply> entities);

    /**
     * 修改数据
     *
     * @param articleReply 实例对象
     * @return 影响行数
     */
    int update(ArticleReply articleReply);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

