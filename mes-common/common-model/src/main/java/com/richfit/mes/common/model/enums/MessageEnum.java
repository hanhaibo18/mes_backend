package com.richfit.mes.common.model.enums;

/**
 * @ClassName: RecipientsEnum.java
 * @Author: Hou XinYu
 * @Description: 枚举判断
 * @CreateTime: 2022年02月16日 13:58:00
 */
public enum MessageEnum {
    //否
    NO("0", "否"),
    //是
    YES("1", "是"),
    ;
    /**
     * ID
     */
    private final String code;

    /**
     * 名称
     */
    private final String message;

    private MessageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessage(String stateId) {
        for (MessageEnum recipientsEnum : MessageEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getMessage();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getCode(String name) {
        for (MessageEnum recipientsEnum : MessageEnum.values()) {
            if (recipientsEnum.message.equals(name)) {
                return recipientsEnum.getCode();

            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }
}
