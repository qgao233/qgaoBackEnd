package com.qgao.springcloud.shiro.service;

import java.util.Map;

public interface ValidateService {

    Long checkUserLoginByMail(String userMail, String md5Pwd)throws Exception;

    Long checkUserLoginByUserId(Long userId, String md5Pwd)throws Exception;

    Map<String,Object> getUserLevelPerm(Long userId) throws Exception;

    void updateUserLoginTime(Long userId) throws Exception;

}
