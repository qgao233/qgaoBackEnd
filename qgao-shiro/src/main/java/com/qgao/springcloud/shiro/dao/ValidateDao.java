package com.qgao.springcloud.shiro.dao;

import com.qgao.springcloud.shiro.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * (User)表数据库访问层
 *
 * @author makejava
 * @since 2021-06-08 17:28:12
 */
@Component
@Mapper
public interface ValidateDao {

    @Select("select id from user where email=#{userMail} and password=#{password}")
    Long checkUserByMail(Map<String, Object> paramsMap);

    @Select("select id from user where id=#{userId} and password=#{password}")
    Long checkUserByUserId(Map<String, Object> paramsMap);

    int queryUserLevel(Long userId);

    List<String> queryUserPermission(Integer level);

    @Update("update user set last_time=login_time where id=#{userId}")
    int updateUserLastTime(Long userId);

    @Update("update user set login_time=#{date} where id=#{userId}")
    int updateUserLoginTime(@Param("userId") Long userId, @Param("date") Date date);

    /***************************上面才是实际用到的*************************/




}

