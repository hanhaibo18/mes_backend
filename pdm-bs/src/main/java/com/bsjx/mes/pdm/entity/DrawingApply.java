package com.bsjx.mes.pdm.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "base_drawing_apply")
public class DrawingApply {
    @Id
    private String id;
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

    @Column(name="datagroup")
    private String dataGroup;

    private String needQuery;
    private String ver;
}
