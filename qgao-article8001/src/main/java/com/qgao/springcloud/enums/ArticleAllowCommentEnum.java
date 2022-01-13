package com.qgao.springcloud.enums;

public enum ArticleAllowCommentEnum {

    PERMIT("0","允许")
    ,FORBID("1","禁止")
    ;

    private String code;
    private String info;

    ArticleAllowCommentEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }



    public static String getCode(String info){
        for(ArticleAllowCommentEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(ArticleAllowCommentEnum tmpEnum:values()){
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
