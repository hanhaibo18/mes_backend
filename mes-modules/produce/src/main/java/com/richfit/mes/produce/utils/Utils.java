package com.richfit.mes.produce.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 工具类
 *
 * @Author: zhiqiang.lu
 * @Date: 2022/8/2 10:25
 */
public class Utils {
    public static void main(String[] args) {
        String code = "afdasf00001";
        String codeAfter = Utils.stringNumberAdd(code, 1);
        System.out.println(codeAfter);

        String code2 = "3233afdasf0000132das09";
        String codeAfter2 = Utils.stringNumberAdd(code2, 1);
        System.out.println(codeAfter2);

        String code3 = "afdasf0000132das99";
        String codeAfter3 = Utils.stringNumberAdd(code3, 2);
        System.out.println(codeAfter3);

        String test = ",33331-33332";
        String test1 = "33332";
        System.out.println(test.replaceAll("[-]" + test1, ""));

    }

    /**
     * 将oldNumber字符串后面的数字加addNumber的数字
     *
     * @param oldNumber 字符串
     * @param addNumber 递增
     * @return 递增后字符串
     * @Author: zhiqiang.lu
     * @Date: 2022/8/2 10:25
     */
    public static String stringNumberAdd(String oldNumber, int addNumber) {
        if (StringUtils.isBlank(oldNumber)) {
            throw new RuntimeException("该编号不符合规则，该数据为空：");
        }
        int lastCharIndex = -1;
        for (int index = 0; index < oldNumber.length(); index++) {
            char c = oldNumber.charAt(index);
            if (c < '0' || c > '9') {
                lastCharIndex = index;
            }
        }
        if (lastCharIndex == oldNumber.length() - 1) {
            throw new RuntimeException("该编号不符合规则，后面应该以数字结尾：" + oldNumber);
        }
        String numberStr = lastCharIndex == -1 ? oldNumber : oldNumber.substring(lastCharIndex + 1);
        String charStr = oldNumber.substring(0, lastCharIndex + 1);
        long longNumber = Long.parseLong(numberStr) + addNumber;
        String codeAfter = String.valueOf(longNumber);
        if (numberStr.length() > codeAfter.length()) {
            int minLength = numberStr.length() - codeAfter.length();
            StringBuilder beforeChar = new StringBuilder();
            for (int lengthIndex = 0; lengthIndex < minLength; lengthIndex++) {
                beforeChar.append("0");
            }
            codeAfter = beforeChar + codeAfter;
        }
        return charStr + codeAfter;
    }
}
