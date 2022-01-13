package com.qgao.springcloud.helper;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public abstract class FileHelper {

    protected void uploadFile(String path, String[] name, MultipartFile[] imgs)throws Exception{
        //查找目录
        File imgDir = new File(path);
        //没有目录，就多级新建目录
        if(!imgDir.exists()){
            imgDir.mkdirs();
        }
        //存储图片
        File file;
        for(int i = 0;i<name.length;i++){
            file = new File(path+File.separator+name[i]);
            FileUtils.copyInputStreamToFile(imgs[i].getInputStream(),file);
        }
    }

    protected boolean deleteFile(String path, String name) throws Exception{
        File file = new File(path+File.separator+name);
        return file.exists() && file.isFile() && file.delete();
    }

    protected boolean getFile(String path,  HttpServletResponse response) throws Exception{
        File file = new File(path);
        if(!file.exists()){
            return false;
        }
        FileUtils.copyFile(file,response.getOutputStream());
        return true;
    }

}
