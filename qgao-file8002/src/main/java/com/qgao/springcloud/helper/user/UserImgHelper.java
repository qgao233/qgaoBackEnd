package com.qgao.springcloud.helper.user;

import com.qgao.springcloud.helper.FileHelper;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import com.qgao.springcloud.utils.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Component
@Slf4j
public class UserImgHelper extends FileHelper{

    @Value("${qgao.img.user.path}")
    private String userImgPath;
    @Value("${qgao.img.name.prefix}")
    private String imgNamePrefix;

    public String[] uploadProfilePhoto(Long userId, MultipartFile[] imgs) throws Exception {
        String uploadPath = userImgPath+ File.separator + String.valueOf(userId);

        //生成图片名字
        int imgCounts = imgs.length;
        String[] imgIdSuffixs = new String[imgCounts];
        for(int i = 0;i<imgCounts;i++){
            String fileName = imgs[i].getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            imgIdSuffixs[i] = imgNamePrefix +"-"+ StringUtil.hex10to32(SnowFlakeIdUtil.generateID())+suffix;//qgao-23sadfon.jpg
        }

        super.uploadFile(uploadPath,imgIdSuffixs,imgs);

        return imgIdSuffixs;

    }

    public boolean deleteProfilePhoto(Long userId, String imgIdSuffix) throws Exception {
        String deletePath = userImgPath+ File.separator + String.valueOf(userId);
        return super.deleteFile(deletePath,imgIdSuffix);
    }

    public boolean getProfilePhoto(Long userId,String targetName, HttpServletResponse response) throws Exception {
        String photoPath = userImgPath+ File.separator + String.valueOf(userId)+File.separator+targetName;
        return super.getFile(photoPath,response);
    }
}
