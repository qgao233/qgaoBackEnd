package com.qgao.springcloud.utils.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<String> fileMove(String from, String to) {
        try {
            File dir = new File(from);
            File[] files = dir.listFiles();
            if (files == null) {
                return null;
            }
            File moveDir = new File(to);
            if (!moveDir.exists()) {
                moveDir.mkdirs();
            }
            List<String> fileNames = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                fileNames.add(files[i].getName());
                File moveFile = new File(moveDir.getPath() + dir.separator
                        + files[i].getName());
                if (moveFile.exists()) {
                    moveFile.delete();
                }
                //移动到新的目录
                files[i].renameTo(moveFile);
            }
            dir.delete();//删除原来的目录
            return fileNames;
        } catch (Exception e) {
            return null;
        }
    }

    public static int fileRecursionMove(String from, String to) {
        try {
            File dir = new File(from);
            File[] files = dir.listFiles();
            if (files == null) {
                return -1;
            }
            File moveDir = new File(to);
            if (!moveDir.exists()) {
                moveDir.mkdirs();
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    fileRecursionMove(files[i].getPath(),
                            to + dir.separator + files[i].getName());
                    files[i].delete();
                }
                File moveFile = new File(moveDir.getPath() + dir.separator
                        + files[i].getName());
                if (moveFile.exists()) {
                    moveFile.delete();
                }
                files[i].renameTo(moveFile);
            }
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }
}
