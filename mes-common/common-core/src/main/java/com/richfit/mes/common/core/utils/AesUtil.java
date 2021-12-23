package com.richfit.mes.common.core.utils;

import org.apache.xmlbeans.impl.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @author sun
 * @Description AES工具类
 */
public class AesUtil {
    private static final String KEY_ALGORITHM = "AES";

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static String decryptAES(String data, String pass, String ivSpec) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(pass.getBytes(), KEY_ALGORITHM), new IvParameterSpec(ivSpec.getBytes()));
        byte[] result = cipher.doFinal(Base64.decode(data.getBytes(StandardCharsets.UTF_8)));
        return new String(result, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(decryptAES("mRkMoKN1Qxh0Wl2Pyectji1GBCCQXOg1v7vnsDR5os6iH64eVdEdexUEPUyrHzR3", "1234123412ABCDEF", "0102030405060708"));
    }
}
