package com.qgao.springcloud.enums;

public enum ArticleOrderEnum {

    TIME("article_time","时间")
    ,STORE("article_store_count","收藏")
    ,VIEW("article_view_count","阅读")
    ,COMMENT("article_comment_count","评论")
    ,GOOD("article_good_count","点赞")
    ;

    private String orderStr;
    private String orderInfo;

    private ArticleOrderEnum(String orderStr, String orderInfo){
        this.orderStr = orderStr;
        this.orderInfo = orderInfo;
    }

    public static String getOrderStr(String orderInfo){
        for (ArticleOrderEnum orderEnum : values()) {
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
