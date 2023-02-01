package com.richfit.mes.produce.enmus;

/**
 * @ClassName: UnitEnum.java
 * @Author: Hou XinYu
 * @Description: 不合格状态对应单位
 * @CreateTime: 2023年01月30日 10:19:00
 */
public enum UnitEnum {
    //申请人
    APPLICANT(1, "申请人"),
    //质控工程师
    QUALITY_CONTROL(2, "质控工程师"),
    //处理单位1
    UNIT_TREATMENT_ONE(3, "处理单位1"),
    //处理单位2
    UNIT_TREATMENT_TWO(4, "处理单位2"),
    //责任
    RESPONSIBILITY(5, "责任裁决"),
    //技术
    TECHNOLOGY(6, "技术裁决");

    /**
     * ID
     */
    private final int code;

    /**
     * 名称
     */
    private final String message;

    UnitEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static UnitEnum getEnum(int stateId) {
        for (UnitEnum publicCodeEnum : UnitEnum.values()) {
            if (publicCodeEnum.code == stateId) {
                return publicCodeEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getMessage(int stateId) {
        for (UnitEnum publicCodeEnum : UnitEnum.values()) {
            if (publicCodeEnum.code == stateId) {
                return publicCodeEnum.getMessage();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }
}
