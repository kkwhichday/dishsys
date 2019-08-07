package com.macro.mall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

@Component
public class DecodeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecodeUtil.class);


    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/NOPadding";
    @Value("${security.encode.key:1234567890}")
    private String key;

    public  String decryptAES(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), KEY_ALGORITHM);
        IvParameterSpec ivspec = new IvParameterSpec(key.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] result = cipher.doFinal(Base64.decode(data.getBytes("UTF-8")));
        return new String(result, "UTF-8").trim();
    }

}
