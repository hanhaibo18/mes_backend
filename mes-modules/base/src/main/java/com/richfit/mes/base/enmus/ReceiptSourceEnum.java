package com.richfit.mes.base.enmus;

/**
 * 单据状态
 * @author wcy
 * @date 2022/10/31
 * @apiNote
 */
public enum ReceiptSourceEnum {



    BOM("0", "原BOM"),
    NEW("1", "新数据"),


    ;

    private final String code;

    private final String name;

    private ReceiptSourceEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ReceiptSourceEnum getSenderEnum(String stateId) {
        for (ReceiptSourceEnum recipientsEnum : ReceiptSourceEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getName(String stateId) {
        for (ReceiptSourceEnum recipientsEnum : ReceiptSourceEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getCode(String name) {
        for (ReceiptSourceEnum recipientsEnum : ReceiptSourceEnum.values()) {
            if (recipientsEnum.name.equals(name)) {
                return recipientsEnum.getCode();
            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }

}