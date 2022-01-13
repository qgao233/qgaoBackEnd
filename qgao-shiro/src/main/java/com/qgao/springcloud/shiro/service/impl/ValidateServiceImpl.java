package com.qgao.springcloud.shiro.service.impl;

import com.qgao.springcloud.shiro.dao.ValidateDao;
import com.qgao.springcloud.shiro.service.ValidateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ValidateServiceImpl implements ValidateService {

    @Resource
    private ValidateDao validateDao;


    @Override
    public Long checkUserLoginByMail(String userMail, String md5Pwd) throws Exception {
        log.debug("check user login by mail");

        Map<String,Object> paramsMap = new HashMap<>(2);
        paramsMap.put("userMail",userMail);
        paramsMap.put("password",md5Pwd);

        return validateDao.checkUserByMail(paramsMap);
    }

    @Override
    public Long checkUserLoginByUserId(Long userId, String md5Pwd) throws Exception {
        log.debug("check user login by userId");

        Map<String,Object> paramsMap = new HashMap<>(2);
        paramsMap.put("userId",userId);
        paramsMap.put("password",md5Pwd);

        return validateDao.checkUserByUserId(paramsMap);
    }

    @Override
    public Map<String, Object> getUserLevelPerm(Long userId) throws Exception {
        log.debug("get user level and permissions");

        Map<String,Object> paramsMap = new HashMap<>(2);
        int level = validateDao.queryUserLevel(userId);
        List<String> permsList = validateDao.queryUserPermission(level);

        paramsMap.put("level",level);
        paramsMap.put("permissions",permsList);

        return paramsMap;
    }

    /*
    先更新上次登录时间，再更新这次登录时间
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateUserLoginTime(Long userId) throws Exception {
        validateDao.updateUserLastTime(userId);
        validateDao.updateUserLoginTime(userId,new Date());
    }
}
