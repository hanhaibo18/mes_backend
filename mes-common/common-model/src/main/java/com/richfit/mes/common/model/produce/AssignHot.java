package com.richfit.mes.common.model.produce;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class AssignHot {
    /**
     * 关系ID
     */
    private String id;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Date createTime;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String modifyBy;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Date modifyTime;

    /**
     * 跟单工序ID
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String tiId;

    /**
     * 派工设备ID
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String deviceId;

    /**
     * 派工用户ID
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String userId;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Integer priority;

    /**
     * 数量
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Integer qty;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String branchCode;

    /**
     * 所属租户
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String tenantId;

    @ApiModelProperty(value = "id", dataType = "String")
    private String remark;

    /**
     * 派工状态(0=未开工,1=以开工,2=已完成)
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Integer state;

    /**
     * 跟单分类：1机加  2装配 3热处理 4钢结构
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String classes;

    /**
     * 跟单Id
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String trackId;

    /**
     * 派工人
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String assignBy;

    /**
     * 派工时间
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Date assignTime;

    /**
     * 派工数量
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Integer availQty;
    @ApiModelProperty(value = "id", dataType = "String")
    private Date startTime;
    @ApiModelProperty(value = "id", dataType = "String")
    private Date endTime;

    /**
     * 车间名称
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String siteName;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String deviceName;
    @ApiModelProperty(value = "id", dataType = "String")
    private String emplName;

    /**
     * 车间班组Id
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String siteId;

    /**
     * 工序顺序序号
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Byte sequenceOrderBy;

    /**
     * 工序顺序
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Short optSequence;

    /**
     * 工艺名称
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String optName;

    /**
     * 工艺版本
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String optVer;

    /**
     * 是当前的
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Byte isCurrent;

    /**
     * 准结工时
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private BigDecimal prepareEndHours;

    /**
     * 单件工时
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private BigDecimal singlePieceHours;

    /**
     * 工序顺序序号
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Byte sequenceOrderBy2;

    /**
     * 是否并行（0否  1是）
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Byte optParallelType;

    /**
     * 可分配数量
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Integer assignableQty;

    /**
     * 工序类型
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String optType;

    /**
     * 工序字典表id
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String operatiponId;

    /**
     * 冷却方式（热工）
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String coolType;

    /**
     * 保温时间h（热工）
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String holdTime;

    /**
     * 实施温度℃（热工）
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String tempWork;

    /**
     * 温度上限℃（热工）
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String tempUp;

    /**
     * 温度下限℃（热工）
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String tempDown;

    /**
     * 设备分类code（热工）
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String typeCode;

    /**
     * 设备分类名称（热工）
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String typeName;

    /**
     * 预装炉id
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Long prechargeFurnaceId;

    /**
     * 正火装炉id
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Long normalizingFurnaceId;

    /**
     * 去氢装炉id
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Long dehydroFurnaceId;

    /**
     * 工序号
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String optNo;

    /**
     * 工艺信息
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String routerInfo;

    /**
     * 产品编号
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String productNo;

    /**
     * 跟单号
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String trackNo;

    /**
     * 工作号
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String workNo;

    /**
     * 图号
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String drawingNo;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String productName;

    /**
     * 重量
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Double weight;

    /**
     * 数量
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Integer number;

    /**
     * 跟单类型（0单件  1批次）
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String trackType;

    /**
     * 材质
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String texture;

    /**
     * 记录时间
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Date thCreateTime;

    /**
     * 是否为长周期 0 :否  1 是
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Byte isLongPeriod;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String projectName;

    /**
     * 计划编号
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private String projCode;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "id", dataType = "String")
    private Date planEndTime;
}
