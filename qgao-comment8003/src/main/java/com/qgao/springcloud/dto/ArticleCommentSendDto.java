package com.qgao.springcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentSendDto implements Serializable {

    private static final long serialVersionUID = 109176795969726949L;

    private Long    comment_id;
    private String  comment_content;
    private Date    comment_time;
    private String  comment_type;
    private Integer comment_good_count;
    private Integer comment_bad_count;
    private Integer comment_reply_count;
    private Long   user_id;
    private String user_img;
    private String nickname;

}
