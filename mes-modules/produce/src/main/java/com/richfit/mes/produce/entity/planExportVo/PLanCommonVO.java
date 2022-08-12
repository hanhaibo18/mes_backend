package com.richfit.mes.produce.entity.planExportVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *计划管理导入第一个sheet页上半部分实体
 */
@Data
public class PLanCommonVO {
    private String objectKey;
    private String objectValue;

    public String getObjectValue(){
        if("计划类型：".equals(objectKey)){
            if("生产计划".equals(objectValue)){
                return "1";
            }else{
                return "0";
            }
        }
        return objectValue;
    }
}
