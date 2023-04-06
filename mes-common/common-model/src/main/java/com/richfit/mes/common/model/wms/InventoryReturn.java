package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * MES实时查询WMS库存 返回类型
 */
@Data
public class InventoryReturn {

    private String invType;


    private double quantity;


    private String materialDesc;


    private String productNum;


    private String unit;

    private String productionDate;

    private String workCode;

    private String materialGroup;

    private String materialNum;

    private String validityDate;

    private String jobNo;

    private String location;

    private String id;

    private String invCode;

    //图号
    @TableField(exist = false)
    private String drawingNo;

    //单重
    @TableField(exist = false)
    private String weight;

}
