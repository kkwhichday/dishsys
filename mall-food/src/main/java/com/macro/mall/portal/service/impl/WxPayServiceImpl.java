package com.macro.mall.portal.service.impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.macro.mall.portal.domain.CommonResult;
import com.macro.mall.portal.service.OmsPortalOrderService;
import com.macro.mall.portal.service.RedisService;
import com.macro.mall.portal.service.WxPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付实现类
 */
@Service
public class WxPayServiceImpl implements WxPayService{

    private static final Logger LOGGER = LoggerFactory.getLogger(WxPayServiceImpl.class);

    @Autowired
    OmsPortalOrderService omsPortalOrderService;
    @Autowired
    WXPayConfig wxPayConfig;

    @Value("${weixinpay.callback.url}")
    String payCallbackUrl;

    @Autowired
    RedisService redisService;

    /**
     *  支付结果通知
     * @param notifyData    异步通知后的XML数据
     * @return
     */
    @Override
    public String payBack(String notifyData) {

        WXPay wxpay = new WXPay(wxPayConfig);
        String xmlBack="";
        Map<String, String> notifyMap = null;
        try {
            notifyMap = WXPayUtil.xmlToMap(notifyData);         // 转换成map
            if (wxpay.isPayResultNotifySignatureValid(notifyMap)) {
                // 签名正确
                // 进行处理。
                // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
                String  return_code = notifyMap.get("return_code");//状态
                String out_trade_no = notifyMap.get("out_trade_no");//订单号

                String transaction_id = notifyMap.get("transaction_id");//微信支付订单号

                if(return_code.equals("SUCCESS")){
                    if(out_trade_no!=null){
                        //处理订单逻辑
                        /**
                         *          更新数据库中支付状态。
                         *          特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功。
                         *          此处需要判断一下。后面写入库操作的时候再写
                         *
                         */

                        omsPortalOrderService.paySuccess(out_trade_no);

                        String transKey = out_trade_no+"_weixin_transactionid";
                        redisService.set(transKey,transaction_id);
                        redisService.expire(transKey,24*3600);
                        System.err.println(">>>>>支付成功");

                        LOGGER.info("微信手机支付回调成功订单号:{}",out_trade_no);
                        xmlBack = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                    }else {
                        LOGGER.info("微信手机支付回调失败订单号:{}",out_trade_no);
                        xmlBack = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                    }

                }
                return xmlBack;
            }
            else {
                // 签名错误，如果数据里没有sign字段，也认为是签名错误
                LOGGER.error("手机支付回调通知签名错误");
                xmlBack = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                return xmlBack;
            }
        } catch (Exception e) {
            LOGGER.error("手机支付回调通知失败",e);
            xmlBack = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
        return xmlBack;
    }

    @Override
    public Map<String, String> dounifiedOrder(String out_trade_no, String total_fee,String spbill_create_ip) throws Exception {
        Map<String, String> fail = new HashMap<>();

        WXPay wxpay = new WXPay(wxPayConfig);
        Map<String, String> data = new HashMap<String, String>();
        data.put("appid", wxPayConfig.getAppID());
        data.put("mch_id", wxPayConfig.getMchID());
        data.put("nonce_str",WXPayUtil.generateNonceStr());
        String body="订单支付";
        data.put("body", body);
        data.put("out_trade_no", out_trade_no);
        data.put("total_fee", total_fee);
        data.put("spbill_create_ip",spbill_create_ip);
        //异步通知地址（请注意必须是外网）
        data.put("notify_url", payCallbackUrl);

        data.put("trade_type", "APP");

        data.put("sign", WXPayUtil.generateSignature(data,wxPayConfig.getKey(), WXPayConstants.SignType.MD5));
        StringBuffer url= new StringBuffer();
        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);
            System.out.println(resp);
            String returnCode = resp.get("return_code");    //获取返回码
            String returnMsg = resp.get("return_msg");

            if("SUCCESS".equals(returnCode)){       //若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
                String resultCode = (String)resp.get("result_code");
                String errCodeDes = (String)resp.get("err_code_des");
                System.out.print(errCodeDes);
                if("SUCCESS".equals(resultCode)){
                    //获取预支付交易回话标志
                    Map<String,String> map = new HashMap<>();
                    String prepay_id = resp.get("prepay_id");
                    String signType = "MD5";
                    map.put("prepay_id",prepay_id);
                    map.put("signType",signType);
                    String sign = WXPayUtil.generateSignature(map,wxPayConfig.getKey(), WXPayConstants.SignType.MD5);
                    resp.put("realsign",sign);
                    url.append("prepay_id="+prepay_id+"&signType="+signType+ "&sign="+sign);
                    return resp;
                }else {
                    LOGGER.info("订单号：{},错误信息：{}",out_trade_no,errCodeDes);
                    url.append(errCodeDes);
                }
            }else {
                LOGGER.info("订单号：{},错误信息：{}",out_trade_no,returnMsg);
                url.append(returnMsg);
            }

        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return fail;
    }


    /**
     * 申请退款
     *
     * @param data 包含商户订单号、商户退款单号、订单金额、退款金额
     * @return
     */
    @Override
    public CommonResult refund(Map<String, String> data) throws Exception {

        WXPay wxpay = new WXPay(wxPayConfig);
        data.put("appid", wxPayConfig.getAppID());
        data.put("mch_id", wxPayConfig.getMchID());
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("sign", WXPayUtil.generateSignature(data,wxPayConfig.getKey(), WXPayConstants.SignType.MD5));

        Map<String, String> resp = null;
        try {
            resp = wxpay.refund(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println(resp);
        String return_code = resp.get("return_code");   //返回状态码
        String return_msg = resp.get("return_msg");     //返回信息

        String resultReturn = null;
        if ("SUCCESS".equals(return_code)) {
            String result_code = resp.get("result_code");       //业务结果
            String err_code_des = resp.get("err_code_des");     //错误代码描述
            if ("SUCCESS".equals(result_code)) {
                //表示退款申请接受成功，结果通过退款查询接口查询
                //修改用户订单状态为退款申请中（暂时未写）
                resultReturn = "退款申请成功";
                return new CommonResult().success(resultReturn);
            } else {
                LOGGER.info("订单号:{}错误信息:{}", err_code_des);
                resultReturn = err_code_des;
            }
        } else {
            LOGGER.info("订单号:{}错误信息:{}", return_msg);
            resultReturn = return_msg;
        }
        return new CommonResult().failed(resultReturn);
    }
}
