package com.ottapppay.driver.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.TreeMap;

import static com.ottapppay.driver.util.AppCommonUtils.gson;

public class MD5Util {

    private static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
            nsaex.printStackTrace();
        }
    }

    public static String getFileMD5String(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        messagedigest.update(byteBuffer);
        in.close();
        return bufferToHex(messagedigest.digest());
    }

    public static String getMD5String(String s) {
        String result = "";
        try {
            result = getMD5String(s.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getMD5StrFromJson(JsonObject jsonObject) {
        String jsonStr = sort(jsonObject);
        String md5Str = getMD5String(jsonStr).toUpperCase();
        return md5Str;
    }

    public static String getMd5(Object obj){
        String str = gson.toJson(obj);

        TreeMap<String, Object> treeMap = gson.fromJson(str, TreeMap.class);
        StringBuffer stb = new StringBuffer();
        for (String tk : treeMap.keySet()) {
            stb.append(treeMap.get(tk));
        }
        return  MD5Util.encrypt(stb.toString()).toUpperCase();
    }

    public static String getDataStrFromJson(JsonObject jsonObject) {
        Gson gson = new Gson();
        String dataStr = gson.toJson(jsonObject);
        LogUtil.d("原串数据:" + dataStr);
        return dataStr;
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static boolean checkPassword(String password, String md5PwdStr) {
        String s = getMD5String(password);
        return s.equals(md5PwdStr);
    }

    public static boolean verify(String text, String sign, String key) {
        text = text + key;
        String mysign = getMD5String(text);
        if (mysign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getStreamMD5String(InputStream input) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            byte[] b = new byte[1024 * 10];
            int length = -1;

            while ((length = input.read(b)) > -1) {
                messagedigest.update(b, 0, length);
            }
            return bufferToHex(messagedigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("InputStream md5 name error");
        }
    }

    /**
     * 按照字段名称（0-9A-z）升序排列取得对应字段值拼接
     *
     * @param object
     * @return
     */
    public static String sort(Object object) {
        Gson g = new Gson();
        String json = g.toJson(object);
        TreeMap<String, String> map = g.fromJson(json, TreeMap.class);
        StringBuilder stb = new StringBuilder();
        for (String str : map.keySet()) {
            stb.append(map.get(str));
        }
        String data = stb.toString();
        //Log.d("service_check"," after sort: str="+data);
        //LogUtil.d("原串数据：" + data);
        return data;
    }

    public static String getJsonStr(HashMap<String, String> map) {
        Gson g = new Gson();
        String json = g.toJson(map);
        return json;
    }

    public static void main(String[] args) {

        System.out.print(getMD5String("1000910000000113800138000201306090001103687DcyyIC967c3308iuytCn"));
    }

    public static String encrypt(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes("UTF8"));
            byte s[] = m.digest();
            String result = "";
            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00)
                        .substring(6);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}