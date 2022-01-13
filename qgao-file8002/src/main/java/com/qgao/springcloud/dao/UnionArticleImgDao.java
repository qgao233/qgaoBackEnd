package com.qgao.springcloud.dao;

import com.qgao.springcloud.entity.UnionArticleImg;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * (UnionArticleImg)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-05 15:16:35
 */
@Component
@Mapper
public interface UnionArticleImgDao {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<UnionArticleImg> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<UnionArticleImg> entities);

    /**
     * 通过实体作为筛选条件查询
     *
     * @param unionArticleImg 实例对象
     * @return 对象列表
     */
    List<UnionArticleImg> queryAll(UnionArticleImg unionArticleImg);

    @Delete("delete from union_article_img where article_id=#{articleId}")
    int deleteByArticleId(Long articleId);

    @Delete("delete from union_article_img where article_img_id=#{articleImgId}")
    int deleteByArticleImgId(String articleImgId);

    /***************************上面是实际用到的*******************************/


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    UnionArticleImg queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<UnionArticleImg> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);



    /**
     * 新增数据
     *
     * @param unionArticleImg 实例对象
     * @return 影响行数
     */
    int insert(UnionArticleImg unionArticleImg);


    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<UnionArticleImg> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<UnionArticleImg> entities);

    /**
     * 修改数据
     *
     * @param unionArticleImg 实例对象
     * @return 影响行数
     */
    int update(UnionArticleImg unionArticleImg);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

