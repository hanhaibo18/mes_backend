package com.richfit.mes.base.enmus;

/**
 * 单据状态
 * @author wcy
 * @date 2022/10/31
 * @apiNote
 */
public enum ReceiptStatusEnum {



    W("0", "待交接"),
    Y("1", "已交接"),
    N("2", "已拒接"),


    ;

    private final String code;

    private final String name;

    private ReceiptStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ReceiptStatusEnum getSenderEnum(String stateId) {
        for (ReceiptStatusEnum recipientsEnum : ReceiptStatusEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getName(String stateId) {
        for (ReceiptStatusEnum recipientsEnum : ReceiptStatusEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getCode(String name) {
        for (ReceiptStatusEnum recipientsEnum : ReceiptStatusEnum.values()) {
            if (recipientsEnum.name.equals(name)) {
                return recipientsEnum.getCode();
            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }

}