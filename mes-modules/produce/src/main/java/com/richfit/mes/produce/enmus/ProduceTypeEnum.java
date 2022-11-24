package com.richfit.mes.produce.enmus;

/**
 * @ClassName: InspectionRecordTypeEnum.java
 * @Author: renzewen
 * @Description: 探伤委托单产品类型
 * @CreateTime: 2022年11月24日 16:35:00
 */
public enum ProduceTypeEnum {
    WELD("焊接"),
    CAST("铸造"),
    FOERG("锻压"),
    FLUORESCENT( "荧光");
       /**
     * 类型
     */
    private final String type;

    ProduceTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
