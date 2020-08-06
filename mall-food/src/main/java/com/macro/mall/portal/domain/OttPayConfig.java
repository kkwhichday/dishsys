package com.macro.mall.portal.domain;


import lombok.Data;

@Data
public class OttPayConfig {


    private String ottOperatorId;

    private String ottPayUrl;

    private String keystore;

    private String signkey;
}
