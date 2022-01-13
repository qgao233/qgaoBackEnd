package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.enums.UserSexEnum;
import com.qgao.springcloud.dao.UserDao;
import com.qgao.springcloud.dto.UserReceiveDto;
import com.qgao.springcloud.entity.User;
import com.qgao.springcloud.service.UserService;
import com.qgao.springcloud.utils.util.EncryptUtil;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;


    @Override
    public void checkMailExistence(String mail) throws Exception {

        if(userDao.queryUserByMail(mail) > 0){
            throw new RuntimeException("mail has existed.");
        }
    }

    @Override
    public User createUserByMail(String mail) throws Exception {
        Long userId = SnowFlakeIdUtil.generateID();
        User user = new User();
        user.setId(userId);
        user.setEmail(mail);
        user.setPassword(EncryptUtil.calcMD5(mail));
        user.setLevelId(1);
        user.setPoint(100);
        user.setNickname(String.valueOf(userId));
        Date date = new Date();
        user.setRegisterTime(date);

        userDao.insertSelective(user);


        return user;
    }

    @Override
    public String updateUser(Long userId,String nickname, String password) throws Exception {
        User user = new User();
        String md5Pwd = null;
        if(password != null){
            md5Pwd = EncryptUtil.calcMD5(password);
            user.setPassword(md5Pwd);
        }
        if(nickname != null){
            user.setNickname(nickname);
        }
        user.setUpdateTime(new Date());
        user.setId(userId);

        userDao.updateUser(user);

        return md5Pwd;
    }

    @Override
    public Date getUserLastTime(Long userId) throws Exception {
        return userDao.queryUserLastTime(userId);
    }

    @Override
    public User getUserLittleMsg(Long userId) throws Exception {
        return userDao.queryUserLittleMsg(userId);
    }

    @Override
    public User getUserDetail(Long userId) throws Exception {
        return userDao.queryUserDetailById(userId);
    }

    @Override
    public void updateUserDetail(Long userId, UserReceiveDto userReceiveDto) throws Exception {
        User user = new User();
        user.setId(userId);
        user.setNickname(userReceiveDto.getNickname());
        user.setImg(userReceiveDto.getImg());
        user.setDescription(userReceiveDto.getDesc());
        user.setRealname(userReceiveDto.getRealname());
        user.setSex(UserSexEnum.getCode(userReceiveDto.getSex()));
        user.setPhone(userReceiveDto.getPhone());
        user.setBirthday(userReceiveDto.getBirthdate());
        user.setAddress(userReceiveDto.getAddress());
        user.setEducation(userReceiveDto.getEducation());
        user.setMajor(userReceiveDto.getMajor());
        user.setUpdateTime(new Date());

        userDao.updateUser(user);
    }
}
