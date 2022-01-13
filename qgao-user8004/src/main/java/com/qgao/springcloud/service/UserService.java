package com.qgao.springcloud.service;

import com.qgao.springcloud.dto.UserReceiveDto;
import com.qgao.springcloud.entity.User;

import java.util.Date;


public interface UserService {

    void checkMailExistence(String mail) throws Exception;

    User createUserByMail(String mail) throws Exception;

    String updateUser(Long userId,String nickname, String password) throws Exception;

    Date getUserLastTime(Long userId) throws Exception;

    User getUserLittleMsg(Long userId) throws Exception;

    User getUserDetail(Long userId) throws Exception;

    void updateUserDetail(Long userId, UserReceiveDto userReceiveDto) throws Exception;
}
