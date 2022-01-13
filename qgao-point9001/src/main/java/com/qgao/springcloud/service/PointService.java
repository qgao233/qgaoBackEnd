package com.qgao.springcloud.service;

import java.util.Map;

public interface PointService {

    void increaseUserPoint(Long userId, Integer point) throws Exception;

    void decreaseUserPoint(Long userId, Integer point) throws Exception;

    Map<Integer,Integer> getLevelAndPoint() throws Exception;

    void transferUserPoint(Long fromUserId, Long toUserId, Integer point) throws Exception;

    String test() throws Exception;
}
