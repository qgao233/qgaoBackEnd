package com.qgao.springcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 后台接受前台的article实体，用于文章的修改、保存
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleReceiveDto implements Serializable{

    private static final long serialVersionUID = 109176795969726949L;

    private String article_type;
    private String article_title;
    private String article_content;
    private String article_tag;
    private String private_type;
    private String public_type;
    private String article_url;
    private String article_img;
    private String article_remark;
    private String article_state;
    private String client_ip;
}
