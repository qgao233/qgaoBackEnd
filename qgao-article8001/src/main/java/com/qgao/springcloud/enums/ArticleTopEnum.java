package com.qgao.springcloud.enums;

public enum ArticleTopEnum {

    NOTOP("0","不置顶")
    ,TOP("1","置顶")
    ;

    private String code;
    private String info;

    ArticleTopEnum(String code, String info){
        this.code = code;
        this.info = info;
    }


    public static String getCode(String info){
        for(ArticleTopEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(ArticleTopEnum tmpEnum:values()){
            if(tmpEnum.getCode().equals(code)){
                return tmpEnum.getInfo();
            }
        }
        return null;
    }



    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
