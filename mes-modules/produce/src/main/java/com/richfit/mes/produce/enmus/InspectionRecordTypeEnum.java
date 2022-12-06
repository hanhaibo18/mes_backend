package com.richfit.mes.produce.enmus;

/**
 * @ClassName: InspectionRecordTypeEnum.java
 * @Author: renzewen
 * @Description: 探伤模板类型
 * @CreateTime: 2022年08月22日 16:35:00
 */
public enum InspectionRecordTypeEnum {
    MT("mt"),
    PT("pt"),
    RT("rt"),
    UT("ut");
       /**
     * 类型
     */
    private final String type;

    InspectionRecordTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
