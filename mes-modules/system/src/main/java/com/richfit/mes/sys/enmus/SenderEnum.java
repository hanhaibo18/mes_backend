package com.richfit.mes.sys.enmus;

/**
 * @ClassName: RecipientsEnum.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月16日 13:58:00
 */
public enum SenderEnum {
    //未读
    NOT_READ(0,"未读"),
    //已读
    READ(1,"已读"),
    //删除
    DELETE(2, "删除")
    ;
    /**ID*/
    private int stateId;

    /**名称*/
    private String stateName;

    private SenderEnum(int stateId, String stateName) {
        this.stateId = stateId;
        this.stateName = stateName;
    }

    public int getStateId(){
        return stateId;
    }

    public String getStateName(){
        return stateName;
    }

    public static SenderEnum getSenderEnum(int stateId){
        for (SenderEnum recipientsEnum : SenderEnum.values() ) {
            if (recipientsEnum.stateId == stateId) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }

    public static String getMessage(int stateId){
        for (SenderEnum recipientsEnum : SenderEnum.values() ) {
            if (recipientsEnum.stateId == stateId) {
                return recipientsEnum.getStateName();
            }
        }
        throw new IllegalArgumentException("No element matches " + stateId);
    }
}
