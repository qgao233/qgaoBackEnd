package com.qgao.springcloud.enums;

public enum ArticleStateEnum {
    TRASH("0","垃圾")
    ,DRAFT("1","草稿")
    ,REVIEW("2","审核")
    ,PUBLISHED("3","发布")
    ;

    private String code;
    private String info;

    ArticleStateEnum(String code, String info){
        this.code = code;
        this.info = info;
    }


    public static String getCode(String info){
        for(ArticleStateEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(ArticleStateEnum tmpEnum:values()){
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

