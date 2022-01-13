package com.qgao.springcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReceiveDto implements Serializable {

    private static final long serialVersionUID = 109176795969726949L;

    private String nickname;
    private String img;
    private String desc;
    private String realname;
    private String sex;
    private String phone;
    private Date   birthdate;
    private String address;
    private String education;
    private String major;
}
