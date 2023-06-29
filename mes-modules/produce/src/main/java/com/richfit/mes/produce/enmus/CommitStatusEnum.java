package com.richfit.mes.produce.enmus;

/**
 * 提报状态
 *
 * @author wcy
 * @date 2022/10/31
 * @apiNote
 */
public enum CommitStatusEnum {


    NOT("0", "未提报"),
    YES("1", "已提报");

    private final String code;

    private final String name;

    private CommitStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static CommitStatusEnum getSenderEnum(String stateId) {
        for (CommitStatusEnum recipientsEnum : CommitStatusEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getName(String stateId) {
        for (CommitStatusEnum recipientsEnum : CommitStatusEnum.values()) {
            if (recipientsEnum.code.equals(stateId)) {
                return recipientsEnum.getName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getCode(String name) {
        for (CommitStatusEnum recipientsEnum : CommitStatusEnum.values()) {
            if (recipientsEnum.name.equals(name)) {
                return recipientsEnum.getCode();
            }
        }
        throw new IllegalArgumentException("No element matches " + name);
    }

}