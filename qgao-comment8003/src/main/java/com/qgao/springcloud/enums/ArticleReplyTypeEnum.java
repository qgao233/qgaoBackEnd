package com.qgao.springcloud.enums;

public enum ArticleReplyTypeEnum {

    COMMENT("0","评论")
    ,REPLY("1","回复")
    ;

    private String code;
    private String info;

    ArticleReplyTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }



    public static String getCode(String info){
        for(ArticleReplyTypeEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(ArticleReplyTypeEnum tmpEnum:values()){
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
