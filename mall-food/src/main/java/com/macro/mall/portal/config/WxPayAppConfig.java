package com.macro.mall.portal.config;

import com.github.wxpay.sdk.WXPayConfig;

import com.macro.mall.portal.domain.OttPayConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.*;

@Configuration
public class WxPayAppConfig {
    @Value("${weixinpay.appId}")
    private String appId;
    @Value("${weixinpay.mchId}")
    private String mchId;
    @Value("${weixinpay.key}")
    private String key;
    @Value("${weixinpay.certPath}")
    private String certPath;
    @Value("${ottpay.ottOperatorId}")
    private String ottOperatorId;

    @Value("${ottpay.ottPayUrl}")
    private String ottPayUrl;

    @Value("${ottpay.keystore}")
    private String keystore;

    @Value("${ottpay.signkey}")
    private String signkey;


    @Bean
    WXPayConfig returnWxconfg() throws FileNotFoundException {


        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        byte[] certData = new byte[(int) file.length()];
        try {
            certStream.read(certData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new WXPayConfig(){

            @Override
            public String getAppID() {
                return appId;
            }

            @Override
            public String getMchID() {
                return mchId;
            }

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public InputStream getCertStream() {
                ByteArrayInputStream certBis = new ByteArrayInputStream(certData);
                return certBis;
            }

            @Override
            public int getHttpConnectTimeoutMs() {
                return 8000;
            }

            @Override
            public int getHttpReadTimeoutMs() {
                return 10000;
            }
        };
    }

    @Bean
    OttPayConfig returnOtt(){
        OttPayConfig opc = new OttPayConfig();
        opc.setOttOperatorId(ottOperatorId);
        opc.setOttPayUrl(ottPayUrl);
        opc.setKeystore(keystore);
        opc.setSignkey(signkey);
        return opc;
    }
}
