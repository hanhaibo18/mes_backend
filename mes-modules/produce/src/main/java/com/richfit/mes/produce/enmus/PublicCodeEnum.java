package com.richfit.mes.produce.enmus;

/**
 * @ClassName: PublicCodeEnum.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年07月12日 16:35:00
 */
public enum PublicCodeEnum {
    //派工
    DISPATCHING(1, "派工"),
    //报工
    COMPLETE(2, "报工"),
    //质检
    QUALITY_TESTING(3, "质检"),
    //调度
    DISPATCH(4, "调度");
    /**
     * ID
     */
    private final int code;

    /**
     * 名称
     */
    private final String message;

    PublicCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static PublicCodeEnum getEnum(int stateId) {
        for (PublicCodeEnum publicCodeEnum : PublicCodeEnum.values()) {
            if (publicCodeEnum.code == stateId) {
                return publicCodeEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getMessage(int stateId) {
        for (PublicCodeEnum publicCodeEnum : PublicCodeEnum.values()) {
            if (publicCodeEnum.code == stateId) {
                return publicCodeEnum.getMessage();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }
}
