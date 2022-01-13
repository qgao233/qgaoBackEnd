package com.qgao.springcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleReplySendDto implements Serializable{

    private static final long serialVersionUID = 109176795969726949L;

    private Long    id;
    private String  reply_content;
    private Date    reply_time;
    private Integer reply_good_count;
    private Integer reply_bad_count;
    private String  reply_type;
    private String  reply_type_str;//额外处理
    private Long    reply_id;

    private Long   user_id;
    private String user_img;
    private String nickname;

    private Long   reply_user_id;
    private String reply_user_img;
    private String reply_nickname;
}
