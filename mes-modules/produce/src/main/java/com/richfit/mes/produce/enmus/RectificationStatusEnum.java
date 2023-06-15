package com.richfit.mes.produce.enmus;

/**
 * 整改单据状态
 *
 * @author wcy
 * @date 2022/10/31
 * @apiNote
 */
public enum RectificationStatusEnum {


    W("0", "未提报"),
    Y("1", "已提报"),
    N("2", "已关闭"),


    ;

    private final String code;

    private final String name;

    private RectificationStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RectificationStatusEnum getSenderEnum(String stateId) {
        for (RectificationStatusEnum recipientsEnum : RectificationStatusEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getName(String stateId) {
        for (RectificationStatusEnum recipientsEnum : RectificationStatusEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getCode(String name) {
        for (RectificationStatusEnum recipientsEnum : RectificationStatusEnum.values()) {
            if (recipientsEnum.name.equals(name)) {
                return recipientsEnum.getCode();
            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }

}