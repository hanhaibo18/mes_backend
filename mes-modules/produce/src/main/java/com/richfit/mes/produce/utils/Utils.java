package com.richfit.mes.produce.utils;

import com.alibaba.fastjson.JSON;
import com.richfit.mes.common.model.produce.TrackFlow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @Author: zhiqiang.lu
 * @Date: 2022/8/2 10:25
 */
public class Utils {
//    public static void main(String[] args) {
//        String code = "afdasf00001";
//        String codeAfter = Utils.stringNumberAdd(code, 1);
//        System.out.println(codeAfter);
//
//        String code2 = "3233afdasf0000132das09";
//        String codeAfter2 = Utils.stringNumberAdd(code2, 1);
//        System.out.println(codeAfter2);
//
//        String code3 = "afdasf0000132das99";
//        String codeAfter3 = Utils.stringNumberAdd(code3, 2);
//        System.out.println(codeAfter3);
//
//        String test = ",33331-33332";
//        String test1 = "33332";
//        System.out.println(test.replaceAll("[-]" + test1, ""));
//        List<Map> storeList = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            Map m = new HashMap();
//            m.put("workblankNo", "asd" + i);
//            storeList.add(m);
//        }
//
//        Collections.sort(storeList, new Comparator<Map>() {
//            @Override
//            public int compare(Map o1, Map o2) {
//                return o1.get("workblankNo").toString().compareTo(o2.get("workblankNo").toString());
//            }
//        });
//        System.out.println(JSON.toJSONString(storeList));
//    }

    public static void main(String[] args) {
        String productNo = "afdasf00001(1),afdasf00004,123123123123,afdasf00002(1),afdasf00003(1)";
        System.out.println(productNoContinuous(productNo));
    }

    /**
     * 字符串去括号后面的内容
     *
     * @param productNo 连续产品编号
     * @return 拼接后的产品编号
     * @Author: zhiqiang.lu
     * @Date: 2023/4/24 10:25
     */
    public static String productNoContinuous(String productNo) {
        String productNoContinuous = "";
        String[] arr = productNo.split(",");
        Arrays.sort(arr);
        Map<String, List<String>> grouping = grouping(arr);
        for (String content : grouping.keySet()) {
            List<String> list = grouping.get(content);
            productNoContinuous += "," + concatenation(list, content);
        }
        return productNoContinuous.replaceFirst("[,]", "");
    }

    /**
     * 产品编号分组
     *
     * @param arr 统一的产品编号数组
     * @return 产品编号分组后的内容
     * @Author: zhiqiang.lu
     * @Date: 2023/4/24 10:25
     */
    public static Map<String, List<String>> grouping(String[] arr) {
        Map<String, List<String>> map = new HashMap<>();
        for (String a : arr) {
            Map<String, String> productNoMap = endingNonNumeric(a);
            if (CollectionUtils.isEmpty(map.get(productNoMap.get("ending")))) {
                List<String> list = new ArrayList<>();
                list.add(productNoMap.get("productNo"));
                map.put(productNoMap.get("ending"), list);
            } else {
                map.get(productNoMap.get("ending")).add(productNoMap.get("productNo"));
            }
        }
        return map;
    }


    /**
     * 字符串去括号后面的内容
     *
     * @param list    统一的产品编号数组
     * @param content 括号产品编号分组内容
     * @return 拼接产品编号
     * @Author: zhiqiang.lu
     * @Date: 2023/4/24 10:25
     */
    public static String concatenation(List<String> list, String content) {
        if ("0".equals(content)) {
            content = "";
        }
        String productsNoTemp = "0";
        String productsNoStr = "";
        for (String a : list) {
            String pn = a;
            String pnOld = Utils.stringNumberAdd(productsNoTemp, 1);
            if (pn.equals(pnOld)) {
                productsNoStr = productsNoStr.replace(("-" + productsNoTemp + content), "");
                productsNoStr += "-" + pn + content;
            } else {
                productsNoStr += "," + pn + content;
            }
            productsNoTemp = pn;
        }
        return productsNoStr.replaceFirst("[,]", "");
    }

    /**
     * 字符串去括号后面的内容
     *
     * @param str 字符串
     * @return 去掉括号后的内容
     * @Author: zhiqiang.lu
     * @Date: 2023/4/24 10:25
     */
    public static Map<String, String> endingNonNumeric(String str) {
        Map<String, String> map = new HashMap<>();
        String productNo = str;
        String ending = "";

        String[] strings = str.split("[(]");
        if (strings.length > 1) {
            map.put("productNo", strings[0]);
            map.put("ending", "(" + strings[1]);
            return map;
        }

        char[] arr = str.toCharArray();
        if (String.valueOf(arr[arr.length - 1]).matches("\\d+")) {
            map.put("productNo", str);
            map.put("ending", "0");
            return map;
        }
        for (int i = arr.length; i > 0; i--) {
            String s = String.valueOf(arr[i - 1]);
            boolean b = s.matches("\\d+");
            if (!b) {
                ending += s;
                productNo = productNo.substring(0, i - 1);
            } else {
                break;
            }
        }
        map.put("productNo", productNo);
        map.put("ending", ending);
        return map;
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

    /**
     * 将oldNumber字符串后面的数字加addNumber的数字
     *
     * @param oldNumber 字符串
     * @param addNumber 递增
     * @return 递增后字符串
     * @Author: zhiqiang.lu
     * @Date: 2022/8/2 10:25
     */
    public static int unit(String unitStr) {
        int unit = 1;
        if (unitStr.contains("百")) {
            unit = 100;
        }
        return unit;
    }
}
