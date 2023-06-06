package com.richfit.mes.base.enmus;

/**
 * @ClassName: RecipientsEnum.java
 * @Author: Hou XinYu
 * @Description: 工序类型枚举
 * @CreateTime: 2022年02月16日 13:58:00
 */
public enum OptTypeEnum {
    //普通
    NORMAL_OPERATION(0, "普通"),
    //热处理
    HEAT_OPERATION(1, "热处理"),
    //装配
    ASSEMBLY_OPERATION(2, "装配"),
    //外协
    OUTSOURCE_OPERATION(3, "外协"),
    //虚拟工序
    VIRTUAL_OPERATION(4, "虚拟工序"),
    //检验工序
    QUALITY_CHECK_OPERATION(5, "检验工序"),
    //探伤工序
    DETECTION_OPERATION(6, "探伤工序"),
    //扣箱工序
    KX_OPERATION(20, "扣箱工序"),

    ;
    /**
     * ID
     */
    private int code;

    /**
     * 名称
     */
    private String message;

    private OptTypeEnum(int stateId, String message) {
        this.code = code;
        this.message = message;
    }

    public int getStateId() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static OptTypeEnum getSenderEnum(int stateId) {
        for (OptTypeEnum recipientsEnum : OptTypeEnum.values()) {
            if (recipientsEnum.ordinal() == stateId) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static int getCode(String name) {

        for (OptTypeEnum recipientsEnum : OptTypeEnum.values()) {
            if (name.contains(recipientsEnum.getMessage())) {
                return recipientsEnum.ordinal();
            }
        }
        return 0;
    }

    public static String getMessage(int stateId) {

        for (OptTypeEnum recipientsEnum : OptTypeEnum.values()) {
            if (recipientsEnum.ordinal() == stateId) {
                return recipientsEnum.getMessage();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }
}
