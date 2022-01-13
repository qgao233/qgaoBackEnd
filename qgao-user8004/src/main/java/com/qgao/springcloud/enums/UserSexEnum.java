package com.qgao.springcloud.enums;

public enum UserSexEnum {

    CREATE("0","男")
    ,COPY("1","女")
    ,TRANSLATE("2","隐私")
    ;

    private String code;
    private String info;

    UserSexEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }



    public static String getCode(String info){
        for(UserSexEnum tmpEnum:values()){
            if(tmpEnum.getInfo().equals(info)){
                return tmpEnum.getCode();
            }
        }
        return null;
    }

    public static String getInfo(String code){
        for(UserSexEnum tmpEnum:values()){
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
