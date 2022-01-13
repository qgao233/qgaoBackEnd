package com.qgao.springcloud.dao;

import com.qgao.springcloud.entity.ArticleStorecategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 个人的收藏分类(ArticleStorecategory)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-02 13:22:56
 */
@Component
@Mapper
public interface ArticleStorecategoryDao {


    @Update("update article_storecategory set article_count = article_count + 1 where id = #{id} and user_id=#{userId}")
    int updateCountUpById(@Param("id") long id, @Param("userId") long userId);

    @Update("update article_storecategory set article_count = article_count - 1 where id = #{id} and user_id=#{userId}")
    int updateCountDownById(@Param("id") long id, @Param("userId") long userId);


    /***************************以上是实际用到的***********************************/
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ArticleStorecategory queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ArticleStorecategory> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param articleStorecategory 实例对象
     * @return 对象列表
     */
    List<ArticleStorecategory> queryAll(ArticleStorecategory articleStorecategory);

    /**
     * 新增数据
     *
     * @param articleStorecategory 实例对象
     * @return 影响行数
     */
    int insert(ArticleStorecategory articleStorecategory);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleStorecategory> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<ArticleStorecategory> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleStorecategory> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<ArticleStorecategory> entities);

    /**
     * 修改数据
     *
     * @param articleStorecategory 实例对象
     * @return 影响行数
     */
    int update(ArticleStorecategory articleStorecategory);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

