package com.qgao.springcloud.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserImgService {

    String uploadUserProfilePhoto(Long userId, MultipartFile[] photos,String oldImgName) throws Exception;
}
