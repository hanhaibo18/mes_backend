package com.richfit.mes.base.enmus;

/**
 * @Author: longlinhui
 * @CreateTime: 2023/4/10
 */
public enum TrackTypeEnum {
    //单件
    D("0", "单件"),
    //批次
    P("1", "批次"),
    ;
    /**
     * ID
     */
    private final String code;

    /**
     * 名称
     */
    private final String message;

    private TrackTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static TrackTypeEnum getSenderEnum(int stateId) {
        for (TrackTypeEnum recipientsEnum : TrackTypeEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getMessage(int stateId) {
        for (TrackTypeEnum recipientsEnum : TrackTypeEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getMessage();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getCode(String name) {
        for (TrackTypeEnum recipientsEnum : TrackTypeEnum.values()) {
            if (recipientsEnum.message.equals(name)) {
                return recipientsEnum.getCode();

            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }
}
