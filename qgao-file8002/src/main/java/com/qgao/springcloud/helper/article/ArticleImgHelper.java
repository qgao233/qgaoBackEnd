package com.qgao.springcloud.helper.article;

import com.qgao.springcloud.helper.FileHelper;
import com.qgao.springcloud.utils.util.FileUtil;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import com.qgao.springcloud.utils.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Component
@Slf4j
public class ArticleImgHelper extends FileHelper {

    @Value("${qgao.img.article.path}")
    private String articleImgPath;
    @Value("${qgao.img.name.prefix}")
    private String imgNamePrefix;
    @Resource
    private ArticleImgHelper articleFileHelper;

    public String[] uploadFile(String tempBufferId,MultipartFile[] imgs) throws Exception {
        log.debug("上传图片到临时存储区");

        if(tempBufferId == null){
            throw new RuntimeException("temp bufferId cannot be null");
        }

        String uploadPath = articleImgPath+File.separator+"tmp"+File.separator+tempBufferId;

        //生成图片名字
        int imgCounts = imgs.length;
        String[] imgIdSuffixs = new String[imgCounts];
        for(int i = 0;i<imgCounts;i++){
            String fileName = imgs[i].getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            imgIdSuffixs[i] = imgNamePrefix +"-"+ encodeFileName(tempBufferId)+suffix;//qgao-23sadfon.jpg
        }

        super.uploadFile(uploadPath,imgIdSuffixs,imgs);

        return imgIdSuffixs;
    }

    /*
    当用户更新文章（编辑）时，文章里的图片存放在公共区，新增加的图片放在临时区，删除图片时走2或3
    当用户只删除公共区的图片时，走1
     */
    @Override
    public boolean deleteFile(String tempBufferId,String imgIdSuffix)throws Exception {

        String fileDirPath;
        File file;
        if(tempBufferId == null){//走公共区：1
            fileDirPath = articleImgPath;
            return super.deleteFile(fileDirPath,imgIdSuffix);
        }

        //走临时区：2
        fileDirPath = articleImgPath+File.separator+"tmp"+File.separator+tempBufferId;
        if(super.deleteFile(fileDirPath,imgIdSuffix)){
            return true;
        }

        //走公共区：3
        fileDirPath = articleImgPath;
        boolean isDelete = super.deleteFile(fileDirPath,imgIdSuffix);
        new ThreadLocal<Boolean>().set(isDelete);
        return isDelete;

    }

    /*
    将tempBufferId一起放进文件名中,访问该图片时需要用到
     */
    public String encodeFileName(String tempBufferId){
        String formerPart = StringUtil.hex10to32(SnowFlakeIdUtil.generateID());
        String latterPart = "#"+StringUtil.hex10to32(Long.parseLong(tempBufferId));
        return formerPart+latterPart;
    }

    /*
    返回tempBufferId
     */
    public String decodeFileName(String fileName){
        return String.valueOf(StringUtil.hex32To10(fileName.split("#")[1]));
    }


    public boolean getFile(String imgIdSuffix, HttpServletResponse response) throws Exception {
        log.debug("尝试从公共存储区获得图片");
        if(!super.getFile(articleImgPath+File.separator+imgIdSuffix,response)){
            return true;
        }

        log.debug("尝试从临时存储区获得图片");
        String tempBufferId = decodeFileName(imgIdSuffix.substring(0,imgIdSuffix.lastIndexOf(".")+1));
        String targetTempUri = File.separator+"tmp"+File.separator+tempBufferId+File.separator+imgIdSuffix;

        return super.getFile(articleImgPath+targetTempUri,response);

    }

    public List<String> transferFile(String tempBufferId){
        String tmpfileDirPath = articleImgPath+File.separator+"tmp"+File.separator+tempBufferId;
        String commonFileDirPath = articleImgPath;

        return FileUtil.fileMove(tmpfileDirPath,commonFileDirPath);
    }


}
