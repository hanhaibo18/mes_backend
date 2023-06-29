package com.richfit.mes.produce.enmus;

/**
 * 整改单据状态
 *
 * @author wcy
 * @date 2022/10/31
 * @apiNote
 */
public enum RectificationStatusEnum {


    MANAGE_NOT("9", "开具人未提报"),
    MANAGE_HAVE("10", "开具人已提报"),
    Y("1", "办理单位已提报"),
    N("2", "开具人已关闭"),
    RECTIFICATION_UNIT_DONE("3", "整改单位提交，待整改检验"),
    HAVE_CHECK("4", "整改检验已提交");

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