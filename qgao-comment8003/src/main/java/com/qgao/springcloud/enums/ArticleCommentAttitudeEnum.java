package com.qgao.springcloud.enums;

public enum ArticleCommentAttitudeEnum {

    GOOD("0","comment_good_count","赞")
    ,BAD("1","comment_bad_count","踩")
    ;

    private String code;
    private String field;
    private String info;

    ArticleCommentAttitudeEnum(String code,String field, String info) {
        this.code = code;
        this.field = field;
        this.info = info;
    }



    public static String getCode(String info){
        for(ArticleCommentAttitudeEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getField(String info){
        for(ArticleCommentAttitudeEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getField();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(ArticleCommentAttitudeEnum tmpEnum:values()){
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
