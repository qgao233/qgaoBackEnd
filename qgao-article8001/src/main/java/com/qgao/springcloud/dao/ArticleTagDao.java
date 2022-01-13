package com.qgao.springcloud.dao;

import com.qgao.springcloud.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * (ArticleTag)表数据库访问层
 *
 * @author makejava
 * @since 2021-05-29 19:31:44
 */
@Component
@Mapper
public interface ArticleTagDao {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleTag> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<ArticleTag> entities);


    ArticleTag queryByTagName(String tagName);

    /***************************以上是实际用到的***********************************/

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ArticleTag queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ArticleTag> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param articleTag 实例对象
     * @return 对象列表
     */
    List<ArticleTag> queryAll(ArticleTag articleTag);

    /**
     * 新增数据
     *
     * @param articleTag 实例对象
     * @return 影响行数
     */
    int insert(ArticleTag articleTag);



    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleTag> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<ArticleTag> entities);

    /**
     * 修改数据
     *
     * @param articleTag 实例对象
     * @return 影响行数
     */
    int update(ArticleTag articleTag);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

