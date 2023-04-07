package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * MES实时查询WMS库存 返回对象
 */
@Data
public class InventoryReturn {

    /**
     * 库存类型
     */
    private String invType;

    /**
     * 库存数量
     */
    private double quantity;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 产品编码
     */
    private String productNum;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 生产日期
     */
    private String productionDate;

    /**
     * 工厂
     */
    private String workCode;

    /**
     * 物料组
     */
    private String materialGroup;

    /**
     * 物料编码
     */
    private String materialNum;

    /**
     * 有效期
     */
    private String validityDate;

    /**
     * 工作号
     */
    private String jobNo;

    /**
     * 仓位
     */
    private String location;

    /**
     * 库存id
     */
    private String id;

    /**
     * 库房
     */
    private String invCode;

    //图号
    @TableField(exist = false)
    private String drawingNo;

    //单重
    @TableField(exist = false)
    private String weight;

    //物料名称
    @TableField(exist = false)
    private String materialName;

    //材质
    @TableField(exist = false)
    private String texture;

}
