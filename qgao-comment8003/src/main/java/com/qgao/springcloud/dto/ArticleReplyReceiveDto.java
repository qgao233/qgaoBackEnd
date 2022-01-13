package com.qgao.springcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleReplyReceiveDto implements Serializable {

    private static final long serialVersionUID = 109176795969726949L;

    private String reply_content;
    private String reply_ip;
    private String reply_type;
    private Long   reply_id;
    private Long   comment_id;
}
