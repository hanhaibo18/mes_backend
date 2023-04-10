package com.richfit.mes.common.model.util;

public class Util {
    public static String fillZero(String str, int expLen) throws Exception {
        byte[] expByte = new byte[expLen];
        for (int i = 0; i < expLen; i++) {
            expByte[i] = '0';
        }
        if (str == null || "".equals(str)) {
            return new String(expByte);
        }
        if (str.length() > expLen) {
            throw new Exception("太长了！");
        }
        byte[] bank = str.getBytes();
        for (int i = bank.length; i > 0; i--) {
            expByte[--expLen] = bank[i - 1];
        }
        return new String(expByte);
    }
}
