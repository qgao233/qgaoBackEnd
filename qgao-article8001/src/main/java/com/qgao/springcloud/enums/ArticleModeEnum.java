package com.qgao.springcloud.enums;

public enum ArticleModeEnum {

    PRIVATE("0","私有")
    ,PUBLIC("1","公开")
    ,FAN("2","粉丝")
    ;

    private String code;
    private String info;

    ArticleModeEnum(String code, String info){
        this.code = code;
        this.info = info;
    }


    public static String getCode(String info){
        for(ArticleModeEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(ArticleModeEnum tmpEnum:values()){
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
