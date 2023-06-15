package com.richfit.mes.common.model.enums;

/**
 * @author gaol
 * @date 2022/10/31
 * @apiNote
 */
public enum MaterialTypeEnum {

    /*
     *   宝石机械的物料类型
     *   成品、半成品（半）、下料件（X），锻件（D）、铸件（Z）、精铸件（JZ）、模型件（MX）这七种物料类型
     *   我们在同步物料信息时，对物料名称中的对应字母进行解析，转换为 0 1 2 等物料类型
     */

    D("0", "锻件"),
    Z("1", "铸件"),
    JZ("2", "精铸件"),
    C("3", "成品"),
    X("4", "下料件"),
    MX("5", "模型件"),
    B("6", "半成品"),

    ;

    private final String code;

    private final String name;

    private MaterialTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static MaterialTypeEnum getSenderEnum(String stateId) {
        for (MaterialTypeEnum recipientsEnum : MaterialTypeEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getName(String stateId) {
        for (MaterialTypeEnum recipientsEnum : MaterialTypeEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getCode(String name) {
        for (MaterialTypeEnum recipientsEnum : MaterialTypeEnum.values()) {
            if (recipientsEnum.name.equals(name)) {
                return recipientsEnum.getCode();
            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }

    public static String getMessage(String stateId) {
        for (MaterialTypeEnum recipientsEnum : MaterialTypeEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }
}