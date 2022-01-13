package com.qgao.springcloud.enums;

public enum ArticleCommentTypeEnum {

    TOP("0","comment_good_count","置顶")
    ,GOOD("1","comment_good_count","热评")
    ,NORMAL("2","comment_time","普通")
    ,BAD("3","comment_bad_count","差评")
    ;

    private String code;
    private String field;
    private String info;

    ArticleCommentTypeEnum(String code,String field, String info) {
        this.code = code;
        this.field = field;
        this.info = info;
    }



    public static String getCode(String info){
        for(ArticleCommentTypeEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(ArticleCommentTypeEnum tmpEnum:values()){
            if(tmpEnum.getCode().equals(code)){
                return tmpEnum.getInfo();
            }
        }
        return null;
    }


    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
