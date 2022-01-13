package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.helper.user.UserImgHelper;
import com.qgao.springcloud.service.UserImgService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
public class UserImgServiceImpl implements UserImgService {

    @Resource
    private UserImgHelper userImgHelper;

    @Override
    public String uploadUserProfilePhoto(Long userId, MultipartFile[] photos, String oldImgName) throws Exception {

        //去删除本地路径下的该文件
        userImgHelper.deleteProfilePhoto(userId,oldImgName);
        //上传新文件
        String[] name = userImgHelper.uploadProfilePhoto(userId,photos);

        return name[0];
    }
}
