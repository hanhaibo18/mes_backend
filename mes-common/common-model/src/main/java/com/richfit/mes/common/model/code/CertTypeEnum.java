package com.richfit.mes.common.model.code;

/**
 * @Author: GaoLiang
 * @Date: 2022/6/13 15:48
 */
public enum CertTypeEnum {

    //0工序合格证 1完工合格证
    ITEM_CERT("0"), FINISH_CERT("1");

    private final String code;

    public String getCode() {
        return code;
    }

    private CertTypeEnum(String s) {
        this.code = s;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
