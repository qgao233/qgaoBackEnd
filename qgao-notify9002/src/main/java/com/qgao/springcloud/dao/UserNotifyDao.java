package com.qgao.springcloud.dao;

import com.qgao.springcloud.utils.entity.UserNotify;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * notify_type: subscribe --> notify_content: null
 * notify_type: article      --> notify_content: article_id
 * notify_type: comment --> notify_content: comment_id
 * notify_type: attitude   --> notify_content: good/bad:article/comment/reply:article_id/comment_id/reply_id(UserNotify)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-24 20:31:12
 */
@Component
@Mapper
public interface UserNotifyDao {

    List<UserNotify> queryByNotifyUserId(Long userId);

    @Delete("delete from user_notify where notify_to_user_id=#{userId}")
    int deleteByNotifyUserId(Long userId);

    @Select("select from_user_id from user_subscribe where to_user_id=#{userId}")
    List<Long> queryFanByIdolUserId(Long userId);

    int insertBatchNotify(@Param("entities") List<UserNotify> entities);

    /********************上用*********************/
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    UserNotify queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<UserNotify> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param userNotify 实例对象
     * @return 对象列表
     */
    List<UserNotify> queryAll(UserNotify userNotify);

    /**
     * 新增数据
     *
     * @param userNotify 实例对象
     * @return 影响行数
     */
    int insert(UserNotify userNotify);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<UserNotify> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<UserNotify> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<UserNotify> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<UserNotify> entities);

    /**
     * 修改数据
     *
     * @param userNotify 实例对象
     * @return 影响行数
     */
    int update(UserNotify userNotify);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

