package com.macro.mall.portal.controller;

import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.macro.mall.portal.domain.CommonResult;
import com.macro.mall.portal.service.RedisService;
import com.macro.mall.portal.service.WxPayService;
import com.macro.mall.portal.util.GetIPAddrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@Api(tags = "PayController", description = "app微信支付")
@RequestMapping("/thirdpay")
public class PayController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayController.class);

    @Autowired
    WxPayService wxPayService;
    @Autowired
    WXPayConfig wxPayConfig;
    @Autowired
    private RedisService redisService;

    @ApiOperation("开启支付")
    @RequestMapping(value = "/pay", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object orderPay(@RequestParam(required = true,value = "out_trade_no")String out_trade_no,
                         @RequestParam(required = true,value = "total_fee")String total_fee,
            HttpServletRequest req, HttpServletResponse response) throws Exception {
        System.err.println("进入微信支付申请");

        String spbill_create_ip = GetIPAddrUtil.getRemoteHost(req);
//        String spbill_create_ip= InetAddress.getLocalHost().getHostAddress();
        LOGGER.info("ip地址:{}",spbill_create_ip);

        //wxpay
        Map<String,String> result = wxPayService.dounifiedOrder(out_trade_no,total_fee,spbill_create_ip);
        String nonce_str = result.get("nonce_str");



        String prepay_id = result.get("prepay_id");
        Long time =System.currentTimeMillis()/1000;
        String timestamp=time.toString();

        //签名生成算法
        Map<String,String> map = new HashMap<>();
        map.put("appid",wxPayConfig.getAppID());
        map.put("partnerid",wxPayConfig.getMchID());//ott标签修改;

        map.put("package","Sign=WXPay");
        map.put("noncestr",nonce_str);
        map.put("timestamp",timestamp);
        map.put("prepayid",prepay_id);
        String sign = WXPayUtil.generateSignature(map,wxPayConfig.getKey(), WXPayConstants.SignType.MD5);


        map.put("sign",sign);
        String resultString="{\"appid\":\""+wxPayConfig.getAppID()+"\",\"partnerid\":\""+wxPayConfig.getMchID()+"\",\"package\":\"Sign=WXPay\"," +
                "\"noncestr\":\""+nonce_str+"\",\"timestamp\":"+timestamp+"," +
                "\"prepayid\":\""+prepay_id+"\",\"sign\":\""+sign+"\"}";
        System.err.println(resultString);

        return map;    //给前端app返回此字符串，再调用前端的微信sdk引起微信支付

    }



    /**
     * 订单支付异步通知
     */
    @ApiOperation(value = "手机订单支付完成后回调")
    @RequestMapping(value = "/notify",method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String WXPayBack(HttpServletRequest request,HttpServletResponse response){
        String resXml="";
        System.err.println("进入异步通知");
        try{
            //
            InputStream is = request.getInputStream();
            //将InputStream转换成String
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
                    sb.append(line );
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resXml=sb.toString();
            System.err.println(resXml);
//            String result = wxPayService.payBack(resXml);
            String result = wxPayService.payBackOtt(resXml);
            return result;
        }catch (Exception e){
            LOGGER.error("手机支付回调通知失败",e);
            String result = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            return result;
        }
    }

    @ApiOperation("发起退款")
    @RequestMapping(value = "/refund", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object refund (@RequestParam(required = true,value = "out_trade_no")String out_trade_no,
    @RequestParam(required = true,value = "total_fee")String total_fee) throws Exception {

        Map<String, String> data = new HashMap<String, String>();
        System.err.println("进入微信退款申请");

        String out_refund_no = out_trade_no + "_wxrefund";
        String transaction_id =redisService.get(out_trade_no+"_weixin_transactionid");

        if(transaction_id==null){
            return new CommonResult().failed("尚未支付或支付已超过1天,不能退款");
        }




        data.put("out_refund_no", out_refund_no);
        data.put("transaction_id", transaction_id);
        data.put("total_fee", total_fee);
        data.put("refund_fee", total_fee);

       return wxPayService.refund(data);

    }




    @ApiOperation("开启支付")
    @RequestMapping(value = "/payott", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object orderPayOtt(@RequestParam(required = true,value = "out_trade_no")String out_trade_no,
                           @RequestParam(required = true,value = "total_fee")String total_fee,
                           HttpServletRequest req, HttpServletResponse response) throws Exception {
        System.err.println("进入Ott支付申请");



        //wxpay
        /*Map<String,String> result = wxPayService.dounifiedOrder(out_trade_no,total_fee,spbill_create_ip);
        String nonce_str = result.get("nonce_str");
        */


        //ottpay
        Map<String,String> result = wxPayService.dounifiedOrderOtt(out_trade_no,total_fee);
        String nonce_str = result.get("nonceStr");//ott标签修改

        String prepay_id = result.get("prepay_id");

        String wxapikey = result.get("wxapikey");
        Long time =System.currentTimeMillis()/1000;
        String timestamp=time.toString();

        //签名生成算法
        Map<String,String> map = new HashMap<>();
        map.put("appid",wxPayConfig.getAppID());
//        map.put("partnerid",wxPayConfig.getMchID());//ott标签修改
        map.put("partnerid",result.get("partnerId"));

        map.put("package","Sign=WXPay");
        map.put("noncestr",nonce_str);
        map.put("timestamp",timestamp);
        map.put("prepayid",prepay_id);
//        String sign = WXPayUtil.generateSignature(map,wxPayConfig.getKey(), WXPayConstants.SignType.MD5);

        //ott获取微信apikey
        String sign = WXPayUtil.generateSignature(map,wxapikey, WXPayConstants.SignType.MD5);
        map.put("sign",sign);
        String resultString="{\"appid\":\""+wxPayConfig.getAppID()+"\",\"partnerid\":\""+wxPayConfig.getMchID()+"\",\"package\":\"Sign=WXPay\"," +
                "\"noncestr\":\""+nonce_str+"\",\"timestamp\":"+timestamp+"," +
                "\"prepayid\":\""+prepay_id+"\",\"sign\":\""+sign+"\"}";
        System.err.println(resultString+"  wxpaikey===="+wxapikey);

        return map;    //给前端app返回此字符串，再调用前端的微信sdk引起微信支付

    }



}
