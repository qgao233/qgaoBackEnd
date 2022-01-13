package com.qgao.springcloud.controller;

import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.feign.UserFeignService;
import com.qgao.springcloud.helper.user.UserImgHelper;
import com.qgao.springcloud.service.UserImgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
public class UserImgController {

    @Resource
    private UserImgService userImgService;
    @Resource
    private UserImgHelper userImgHelper;
    @Resource
    private UserFeignService userFeignService;


    @RequiresAuthentication
    @PostMapping("/authc/static/user/profile/photo/upload")
    public CommonResult uploadProfilePhoto(@RequestParam("photo") MultipartFile[] photos){
        if(photos == null || photos.length == 0){
            return new CommonResult(400,"upload file failure");
        }

        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();

        String imgIdSuffix;
        try {
            // upload前先去用户服务查看是否已有头像，有的话就返回头像名字
            CommonResult userAbstractInfo = userFeignService.getUserImg(userId);
            // 删除旧文件，上传新文件
            imgIdSuffix = userImgService.uploadUserProfilePhoto(userId,photos,(String) userAbstractInfo.getData());
            // 重新让用户服务更新头像的名字
            userFeignService.updateUserImg(imgIdSuffix);
        } catch (Exception e) {
            log.error("upload profile photo failure, "+e.getMessage());
            return new CommonResult(401,"upload profile photo failure");
        }
        return new CommonResult<>(200,"upload profile photo success",imgIdSuffix);

    }

    @GetMapping("/static/user/profile/photo/get/{imgIdSuffix:.+}")
    public CommonResult getProfilePhoto(
            @PathVariable("imgIdSuffix")String imgIdSuffix
            , @RequestParam("user_id") Long userId
            , HttpServletResponse response){

        if(StringUtils.isEmpty(imgIdSuffix)){
            return new CommonResult(400,"imgIdSuffix is null");
        }

        try {
            userImgHelper.getProfilePhoto(userId,imgIdSuffix,response);
            response.flushBuffer();
        } catch (Exception e) {
            log.error("get profile photo failure, "+e.getMessage());
            return new CommonResult(401,"get profile photo failure");
        }
        return new CommonResult<>(200,"get profile photo success",imgIdSuffix);

    }
}
