package com.richfit.mes.produce.enmus;

/**
 * @ClassName: IdEnum.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年07月13日 11:07:00
 */
public enum IdEnum {
    //派工
    TRACK_HEAD_ID(1, "trackHeadId"),
    //报工
    TRACK_ITEM_ID(2, "trackItemId"),
    //质检
    ASSIGN_ID(3, "assignId"),
    ;
    /**
     * ID
     */
    private final int code;

    /**
     * 名称
     */
    private final String message;

    IdEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static IdEnum getEnum(int stateId) {
        for (IdEnum idEnum : IdEnum.values()) {
            if (idEnum.code == stateId) {
                return idEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getMessage(int stateId) {
        for (IdEnum idEnum : IdEnum.values()) {
            if (idEnum.code == stateId) {
                return idEnum.getMessage();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }
}
