package com.richfit.mes.base.enmus;

/**
 * 制造类型
 */
public enum ObjectTypeEnum {

    /**
     * 自制
     */
    Z("0", "自制"),
    /**
     * 外购
     */
    WG("1", "外购"),
    /**
     * 外协
     */
    WX("2", "外协")
    ;
    /**
     * ID
     */
    private final String code;

    /**
     * 名称
     */
    private final String message;

    private ObjectTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ObjectTypeEnum getSenderEnum(int stateId) {
        for (ObjectTypeEnum recipientsEnum : ObjectTypeEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getMessage(String stateId) {
        for (ObjectTypeEnum recipientsEnum : ObjectTypeEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getMessage();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getCode(String name) {
        for (ObjectTypeEnum recipientsEnum : ObjectTypeEnum.values()) {
            if (recipientsEnum.message.equals(name)) {
                return recipientsEnum.getCode();

            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }

}
