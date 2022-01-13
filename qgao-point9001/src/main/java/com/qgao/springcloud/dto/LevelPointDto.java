package com.qgao.springcloud.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelPointDto implements Serializable{

    private Integer level;
    private Integer point;
}
