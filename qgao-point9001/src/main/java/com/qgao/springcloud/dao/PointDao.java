package com.qgao.springcloud.dao;

import com.qgao.springcloud.dto.LevelPointDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface PointDao {

    LevelPointDto queryUserLevelAndPoint(Long userId);

    List<LevelPointDto> queryLevelPoint();

    @Update("update user set point=#{point} where id=#{userId}")
    int updateUserPoint(@Param("userId") Long userId, @Param("point") Integer point);
}
