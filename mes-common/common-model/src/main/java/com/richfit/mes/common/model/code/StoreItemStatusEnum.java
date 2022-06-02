package com.richfit.mes.common.model.code;

/**
 * 线边库-料单状态
 *
 * @Author: GaoLiang
 * @Date: 2022/6/1 9:47
 */
public enum StoreItemStatusEnum {

    //状态 0完工初始入库  1在制，开始占用  2作废 3已消耗，料单物料已全部投用
    FINISH("0"),
    MAKING("1"),
    SCRAP("2"),
    USED_ALL("3");

    private final String code;

    public String getCode() {
        return code;
    }

    private StoreItemStatusEnum(String s) {
        this.code = s;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
