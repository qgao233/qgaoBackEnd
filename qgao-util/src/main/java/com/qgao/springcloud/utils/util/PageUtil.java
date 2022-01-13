package com.qgao.springcloud.utils.util;

public class PageUtil {

    public static int getOffset(int page, int recordLimit){
        return (page - 1) * recordLimit;
    }
}
