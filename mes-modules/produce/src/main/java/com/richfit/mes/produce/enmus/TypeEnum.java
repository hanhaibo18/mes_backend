package com.richfit.mes.produce.enmus;

/**
 * @author
 * @date 2023/6/26
 * @apiNote
 */
public enum TypeEnum {

    /*
     *   type
     *   不合格流程类型
     */

    ZERO(0, "未送签"),
    ONE(1, "未送签"),
    TWO(2, "质检审核"),
    THREE(3, "处理单位1"),
    FOUR(4, "处理单位2"),
    FIVE(5, "责任裁决"),
    SIX(6, "技术裁决"),
    SEVEN(7, "流转完成"),
    EIGHT(8, "已完成"),
    NINE(9, "已关单"),
    TEN(10, "流转中"),
    ;

    private final int code;

    private final String name;

    private TypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getMessage(int stateId) {
        for (TypeEnum recipientsEnum : TypeEnum.values()) {
            if (recipientsEnum.code == stateId) {
                return recipientsEnum.getName();
            }
        }
        return TEN.getName();
    }
}