package com.richfit.mes.produce.enmus;

/**
 * @author gaol
 * @date 2022/10/31
 * @apiNote
 */
public enum OptSequenceEnum {



    DP(1, "底盘"),
    ZM(2, "整模"),
    ZZ(3, "坐模"),
    LG(4, "炼钢"),
    JZ(5, "浇注"),

    ;

    private final Integer code;

    private final String name;

    private OptSequenceEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static OptSequenceEnum getSenderEnum(String stateId) {
        for (OptSequenceEnum recipientsEnum : OptSequenceEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getName(String stateId) {
        for (OptSequenceEnum recipientsEnum : OptSequenceEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static Integer getCode(String name) {
        for (OptSequenceEnum recipientsEnum : OptSequenceEnum.values()) {
            if (recipientsEnum.name.equals(name)) {
                return recipientsEnum.getCode();
            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }

}