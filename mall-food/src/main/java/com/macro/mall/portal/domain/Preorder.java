package com.macro.mall.portal.domain;

import lombok.Data;

@Data
public class preorder {

    /**
    {"appId":"wx4280bdcc078ecd98", "bizType":"WECHATPAY",
            "merCode":"231d7a31ff47cb314a15e82468962ef7",
            "nonceStr":"pBtSCP4EilQj0EhV",
            "package":"Sign=WXPay",
            "paySign":"B96D174D93AC7462486B9AA2D837443A",
            "prepay_id":"wx2400482391916650408267d41088612978",
            "reAmount":"1.00",
            "timeStamp":"2018102312",
            "tradeNo":"1540313479100420"}
     **/

    private  String appId= "";
    private  String bizType="";
    private  String merCode="";
    private  String nonceStr="";
    private  String package_="";
    private  String paySign="";
    private  String prepay_id="";
    private  String reAmount="";
    private  String timeStamp="";
    private  String tradeNo="";
    private  String partnerId="";
    private  String wxapikey="";


}