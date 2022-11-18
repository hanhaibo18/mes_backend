package com.richfit.mes.sys.enmus;


/**
 * @ClassName: SenderEnum.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年02月16日 10:58:00
 */

public enum RecipientsEnum {
    //未读
    NOT_READ(0, "对方未读[发送成功]"),
    //已读
    READ(1, "对方已读"),
    //撤回
    REVOCATION(2, "已经撤回"),
    //删除
    DELETE(3, "删除");
    /**
     * ID
     */
    private int stateId;

    /**
     * 名称
     */
    private String stateName;

    private RecipientsEnum(int stateId, String stateName) {
        this.stateId = stateId;
        this.stateName = stateName;
    }

    public int getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public static RecipientsEnum getSenderEnum(int stateId) {
        for (RecipientsEnum senderEnum : RecipientsEnum.values()) {
            if (senderEnum.stateId == stateId) {
                return senderEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getMessage(int stateId) {
        for (RecipientsEnum senderEnum : RecipientsEnum.values()) {
            if (senderEnum.stateId == stateId) {
                return senderEnum.getStateName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }
}
