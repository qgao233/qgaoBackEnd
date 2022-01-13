package com.qgao.springcloud.utils.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class EncryptUtil {

    private static final String SALT = "gq@123.com";

    /*
     MD5 加密算法
     */
    public final static String calcMD5(String str) {
        if(str == null) return null;
        try {
            str += SALT;
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes("utf-8"));
            // digest()最后确定返回md5 hash值，返回值为8进制字符串。因为md5 hash值是16进制的hex值
            // BigInteger函数则将8进制的字符串转换成16进制hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e){
            System.out.println("md5错误");
            return null;
        }

    }

    public static void main(String[] args) {
        System.out.println(calcMD5("123"));
    }
}
