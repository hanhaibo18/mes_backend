package com.richfit.mes.base.enmus;

/**
 * @ClassName: RecipientsEnum.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年02月16日 13:58:00
 */
public enum RouterStatusEnum {
    //停用
    BLOCK_UP(0, "停用"),
    //正常
    NORMAL(1, "正常"),
    //历史
    HISTORY(2, "历史"),

    ;
    /**
     * ID
     */
    private int code;

    /**
     * 名称
     */
    private String message;

    private RouterStatusEnum(int stateId, String message) {
        this.code = code;
        this.message = message;
    }

    public int getStateId() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static RouterStatusEnum getSenderEnum(int stateId) {
        for (RouterStatusEnum recipientsEnum : RouterStatusEnum.values()) {
            if (recipientsEnum.code == stateId) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getMessage(int stateId) {
        for (RouterStatusEnum recipientsEnum : RouterStatusEnum.values()) {
            if (recipientsEnum.code == stateId) {
                return recipientsEnum.getMessage();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }
}
