package com.richfit.mes.base.enmus;

/**
 * @ClassName: RecipientsEnum.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月16日 13:58:00
 */
public enum MessageEnum {
    //否
    NO(0, "否"),
    //是
    YES(1, "是"),
    ;
    /**
     * ID
     */
    private final int code;

    /**
     * 名称
     */
    private final String message;

    private MessageEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static MessageEnum getSenderEnum(int stateId) {
        for (MessageEnum recipientsEnum : MessageEnum.values()) {
            if (recipientsEnum.code == stateId) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getMessage(int stateId) {
        for (MessageEnum recipientsEnum : MessageEnum.values()) {
            if (recipientsEnum.code == stateId) {
                return recipientsEnum.getMessage();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }
}
