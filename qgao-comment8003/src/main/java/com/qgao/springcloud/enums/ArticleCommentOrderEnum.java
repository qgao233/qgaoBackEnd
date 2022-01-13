package com.qgao.springcloud.enums;

public enum ArticleCommentOrderEnum {

    TIME("comment_time","时间")
    ,REPLY("comment_reply_count","回复")
    ,GOOD("comment_good_count","点赞")
    ;

    private String orderStr;
    private String orderInfo;

    private ArticleCommentOrderEnum(String orderStr, String orderInfo){
        this.orderStr = orderStr;
        this.orderInfo = orderInfo;
    }

    public static String getOrderStr(String orderInfo){
        for (ArticleCommentOrderEnum orderEnum : values()) {
            if(orderEnum.getOrderInfo().equals(orderInfo)){
                return orderEnum.getOrderStr();
            }
        }
        return null;
    }

    public String getOrderStr() {
        return orderStr;
    }

    public String getOrderInfo() {
        return orderInfo;
    }
}
