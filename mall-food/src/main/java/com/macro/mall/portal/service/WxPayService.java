package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.CommonResult;

import java.util.Map;

public interface WxPayService {
    String payBack(String notifyData);
    String payBackOtt(String notifyData);
    Map<String, String> dounifiedOrder(String out_trade_no, String total_fee,String spbill_create_ip)throws Exception;
    Map<String, String> dounifiedOrderOtt( String out_trade_no,String total_fee)throws Exception;
    public CommonResult refund(Map<String, String> data) throws Exception ;
    }
