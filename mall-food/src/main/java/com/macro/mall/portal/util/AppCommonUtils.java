package com.macro.mall.portal.util;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Map;
import java.util.TreeMap;

/**
 * com.icardpay.bussiness.front.util
 * ott_dev
 *
 * @EMAIL:SHENGMIAO@HKRT.CN
 * @Description: <br/>
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------
 * 2017/3/19      SHENGMIAO          1.0             1.0
 */
public class AppCommonUtils {
    static Gson gson = new Gson();

    /**
     * 解密
     *
     * @param data
     * @param key
     * @param md5
     * @return
     */
    public static String decipher(String data, String key, String md5) {

        byte[] orgData = Base64.decode(data);
        LogUtil.d(new String(orgData));
        String aesKeyStr = MD5Util.encrypt(md5 + key).substring(8, 24).toUpperCase();
        //Log.d("service_check","decipher, aes key = " + aesKeyStr);
        byte[] aesKey = aesKeyStr.getBytes();
        String decData = new String(AES.decrypt(orgData, aesKey));
        LogUtil.d(decData);
        TreeMap<String, Object> treeMap = null;
        try {
            treeMap = gson.fromJson(decData, TreeMap.class);
        } catch (JsonSyntaxException e) {
            LogUtil.d("decrypt error！exception:{}", e);
            return null;
        }
        StringBuilder stb = new StringBuilder();
        for (String tk : treeMap.keySet()) {
            //Log.d("service_check","decipher, key = " + tk);
            //Log.d("service_check","decipher, value = " + treeMap.get(tk));
            stb.append(treeMap.get(tk));
        }
        //Log.d("service_check","decipher, sb = " + stb.toString());

        String calmd5 = MD5Util.encrypt(stb.toString());
        if (calmd5.toUpperCase().equals(md5.toUpperCase())) {

            return decData;
        } else {
            throw new RuntimeException("check sign fail");
        }
    }

    /**
     * 加密
     *
     * @param data json
     * @param key
     * @param md5
     * @return
     */
    public static String encrypted(String data, String key, String md5) {
        //Log.d("service_check","key = " + key);
        //Log.d("service_check","md5 = " + md5);
        //Log.d("service_check","data = " + data);
        String aesKeyStr = MD5Util.encrypt(md5 + key).substring(8, 24).toUpperCase();
        //Log.d("service_check","aes key = " + aesKeyStr);
        LogUtil.d("aes key = " + aesKeyStr);
        byte[] encrypt = AES.encrypt(data.getBytes(), aesKeyStr.getBytes());
        //Log.d("service_check","encrypted encrypt = " + encrypt.toString());
        String result =  Base64.encode(encrypt);
        LogUtil.d("result = " + result);
        //Log.d("service_check","encrypted result = " + result);
        return result;
    }

    public static String getMd5(Object obj) {
        String str = gson.toJson(obj);

        TreeMap<String, String> treeMap = gson.fromJson(str, TreeMap.class);
        StringBuffer stb = new StringBuffer();
        for (String tk : treeMap.keySet()) {
            stb.append(treeMap.get(tk));
        }
        return MD5Util.encrypt(stb.toString()).toUpperCase();
    }

    public static void main(String[] args) {
        String en="";
        String data ="{\"order_id\":\"abc123\"}";
        String md5 =MD5Util.getMD5String("abc123");
        en=encrypted(data,"C974E3A79002EEDD",md5);

         System.out.println(en);
        System.out.println(decipher(en,"C974E3A79002EEDD",md5));

        Map<String,String> dataMap =  (Map<String,String>) JSON.parse(data);
        System.out.println(dataMap);


       /* String ens="Y2+GoqBBPc6EJ9JzXRFoOziKd+DqWg5FiUZY1IYoVBVv0xfFznT9/qanMpiNEamEN2NM7J+hxoRn8VGSJI" +
                "mA2soeip9nYr+VJvTXe7D8j4aXiKyFipuPVCQLiCDg3jkJP+S2EezYMf7crqKY/YAni1CCeIwr2aJfFVT1vYhsW" +
                "Im8t3eyPL//cY4kwombAKhE2gAdEFXz6gVvencz80aRWQ==";
        System.out.println(decipher(ens,"66698851A525C9433","C12F9560769C2CB55E6954935B325916"));
*/    }
}