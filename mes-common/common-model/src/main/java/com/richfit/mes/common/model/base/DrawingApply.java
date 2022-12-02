package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author 王瑞
 * @Description 图纸申请
 */

@Data
public class DrawingApply extends BaseEntity<DrawingApply> {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 图号
     */
    private String drawingNo;

    /**
     * PDM图号
     */
    private String pdmDrawingNo;

    /**
     * 状态 0待审核 1通过 2驳回
     */
    private String status;

    /**
     * 描述
     */
    private String drawingDesc;

    /**
     * 工艺数量
     */
    private Integer routerNum;

    /**
     * 图纸数量
     */
    private Integer drawingNum;

    /**
     * BOM数量
     */
    private Integer bomNum;

    /**
     * 组织结构编码
     */
    private String branchCode;

    /**
     * 工艺
     */
    private String router;

    /**
     * 驳回原因
     */
    private String reason;

    /**
     * 审核人
     */
    protected String reviewBy;

    /**
     * 审核日期
     */
    protected Date reviewTime;

    @TableField(value = "datagroup")
    private String dataGroup;

    private String needQuery;


    /**
     * 工艺数量
     */
    @TableField(exist = false)
    private Integer routerNumber;

    /**
     * 图纸数量
     */
    @TableField(exist = false)
    private Integer drawingNumber;

    /**
     * BOM数量
     */
    @TableField(exist = false)
    private Integer bomNumber;
}
