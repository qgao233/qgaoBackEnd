package com.qgao.springcloud.dao;

import com.qgao.springcloud.entity.ArticleLocalcategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * (ArticleLocalcategory)表数据库访问层
 *
 * @author makejava
 * @since 2021-05-29 19:31:06
 */
@Component
@Mapper
public interface ArticleLocalcategoryDao {

    @Update("update article_localcategory set article_count = article_count + 1 where id = #{id} and user_id=#{userId}")
    int updateCountUpById(@Param("id") long id, @Param("userId") long userId);

    @Update("update article_localcategory set article_count = article_count - 1 where id = #{id} and user_id=#{userId} and article_count > 0")
    int updateCountDownById(@Param("id") long id, @Param("userId") long userId);

    @Select("select sum(article_count) from article_localcategory where user_id=#{userId}")
    int queryArticleCountSum(long userId);

    int insertSelective(ArticleLocalcategory articleLocalcategory);

    int deleteByIdAndUserId(Map<String,Object> paramsMap);

    int update(ArticleLocalcategory articleLocalcategory);

    List<ArticleLocalcategory> queryAll(ArticleLocalcategory articleLocalcategory);


    /***************************以上是实际用到的***********************************/

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ArticleLocalcategory queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<ArticleLocalcategory> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);



    /**
     * 新增数据
     *
     * @param articleLocalcategory 实例对象
     * @return 影响行数
     */
    int insert(ArticleLocalcategory articleLocalcategory);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleLocalcategory> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<ArticleLocalcategory> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<ArticleLocalcategory> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<ArticleLocalcategory> entities);



    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

