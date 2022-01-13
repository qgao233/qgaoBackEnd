package com.qgao.springcloud.enums;

public enum ArticleTypeEnum {
    CREATE("0","原创")
    ,COPY("1","转载")
    ,TRANSLATE("2","翻译")
    ;

    private String code;
    private String info;

    ArticleTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }



    public static String getCode(String info){
        for(ArticleTypeEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(ArticleTypeEnum tmpEnum:values()){
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
