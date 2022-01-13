package com.qgao.springcloud.utils.enums;

public enum UserNotifyTypeEnum {

    SUBSCRIBE("0","关注")
    ,ARTICLE("1","文章")
    ,COMMENT("2","评论")
    ,ATTITUDE("3","态度")
    ;

    private String code;
    private String info;

    UserNotifyTypeEnum(String code, String info){
        this.code = code;
        this.info = info;
    }


    public static String getCode(String info){
        for(UserNotifyTypeEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(UserNotifyTypeEnum tmpEnum:values()){
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
