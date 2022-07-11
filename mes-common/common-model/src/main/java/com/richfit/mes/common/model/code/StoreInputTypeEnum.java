package com.richfit.mes.common.model.code;

/**
 * 线边库 料单录入方式
 *
 * @Author: GaoLiang
 * @Date: 2022/6/29 9:07
 */
public enum StoreInputTypeEnum {

//    录入类型 0 手动录入 1 合格证来料接收  2 系统自动生成  3 配送接收

    USER_INPUT("0"),
    CERT_ACCEPT("1"),
    SYS_AUTO("2"),
    WMS_SEND("3");

    private final String code;

    public String getCode() {
        return code;
    }

    private StoreInputTypeEnum(String s) {
        this.code = s;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
