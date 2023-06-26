package com.richfit.mes.produce.enmus;

/**
 * 整改单据状态
 *
 * @author wcy
 * @date 2022/10/31
 * @apiNote
 */
public enum RectificationUnitEnum {


    A("1", "钻机"),
    B("2", "责任单位"),
    C("3", "整改单位"),
    D("4", "质检");

    private final String code;

    private final String name;

    private RectificationUnitEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RectificationUnitEnum getSenderEnum(String stateId) {
        for (RectificationUnitEnum recipientsEnum : RectificationUnitEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getName(String stateId) {
        for (RectificationUnitEnum recipientsEnum : RectificationUnitEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getCode(String name) {
        for (RectificationUnitEnum recipientsEnum : RectificationUnitEnum.values()) {
            if (recipientsEnum.name.equals(name)) {
                return recipientsEnum.getCode();
            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }

}