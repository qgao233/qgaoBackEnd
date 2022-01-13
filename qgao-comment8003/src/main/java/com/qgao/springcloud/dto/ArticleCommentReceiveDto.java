package com.qgao.springcloud.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentReceiveDto implements Serializable {

    private static final long serialVersionUID = 109176795969726949L;

    private String comment_content;
    private String comment_ip;
    private Long   article_id;
}
