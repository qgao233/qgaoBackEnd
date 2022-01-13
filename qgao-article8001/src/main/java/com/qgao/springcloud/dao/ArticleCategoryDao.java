package com.qgao.springcloud.dao;

import com.qgao.springcloud.entity.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * (ArticleCategory)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-01 15:53:27
 */
@Component
@Mapper
public interface ArticleCategoryDao {

    List<ArticleCategory> queryAll(ArticleCategory articleCategory);

    /***************************以上是实际用到的***********************************/


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ArticleCategory queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ArticleCategory> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);



    /**
     * 新增数据
     *
     * @param articleCategory 实例对象
     * @return 影响行数
     */
    int insert(ArticleCategory articleCategory);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleCategory> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<ArticleCategory> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleCategory> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<ArticleCategory> entities);

    /**
     * 修改数据
     *
     * @param articleCategory 实例对象
     * @return 影响行数
     */
    int update(ArticleCategory articleCategory);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

