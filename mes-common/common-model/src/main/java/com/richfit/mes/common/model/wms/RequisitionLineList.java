package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * requisition_line_list  明细列表
 */
@Data
public class RequisitionLineList implements Serializable {
    /**
     * MES领料单行id
     */
    private String applyId;

    /**
     * MES领料单ID
     */
    private String id;

    /**
     * MES领料单行项目
     */
    private String lineNum;

    /**
     * 产品名称  零部件名称
     */
    private String productName;

    /**
     *  库存地点
     */
    private String invCode;

    /**
     * 是否实物配送  是/否
     */
    private String deliveryFlag;

    /**
     * 物料编码
     */
    private String materialNum;

    /**
     * 物料名称
     */
    private String materialDesc;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 领用数量
     */
    private Double quantity;

    /**
     * 是否锁库库存 锁库/否 自动出库类型时只能为否
     */
    private String frozenFlag;

    /**
     * 关键件
     */
    private String crucialFlag;

    @TableField(exist = false)
    private List<RequisitionLineInfoList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}