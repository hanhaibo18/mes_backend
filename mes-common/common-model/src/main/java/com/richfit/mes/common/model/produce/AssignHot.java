package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
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
    @ApiModelProperty(value = "创建人", dataType = "String")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人", dataType = "String")
    private String modifyBy;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", dataType = "Date")
    private Date modifyTime;

    /**
     * 跟单工序ID
     */
    @ApiModelProperty(value = "跟单工序ID", dataType = "String")
    private String tiId;

    /**
     * 派工设备ID
     */
    @ApiModelProperty(value = "派工设备ID", dataType = "String")
    private String deviceId;

    /**
     * 派工用户ID
     */
    @ApiModelProperty(value = "派工用户ID", dataType = "String")
    private String userId;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级", dataType = "Integer")
    private Integer priority;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private Integer qty;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "所属机构", dataType = "String")
    private String branchCode;

    /**
     * 所属租户
     */
    @ApiModelProperty(value = "所属租户", dataType = "String")
    private String tenantId;

    @ApiModelProperty(value = "备注", dataType = "String")
    private String remark;

    /**
     * 派工状态(0=未开工,1=以开工,2=已完成)
     */
    @ApiModelProperty(value = "派工状态(0=未开工,1=以开工,2=已完成)", dataType = "Integer")
    private Integer state;

    /**
     * 跟单分类：1机加  2装配 3热处理 4钢结构
     */
    @ApiModelProperty(value = "跟单分类：1机加  2装配 3热处理 4钢结构", dataType = "String")
    private String classes;

    /**
     * 跟单Id
     */
    @ApiModelProperty(value = "跟单Id", dataType = "String")
    private String trackId;

    /**
     * 派工人
     */
    @ApiModelProperty(value = "派工人", dataType = "String")
    private String assignBy;

    /**
     * 派工时间
     */
    @ApiModelProperty(value = "派工时间", dataType = "Date")
    private Date assignTime;

    /**
     * 派工数量
     */
    @ApiModelProperty(value = "派工数量", dataType = "Integer")
    private Integer availQty;
    @ApiModelProperty(value = "", dataType = "Date")
    private Date startTime;
    @ApiModelProperty(value = "", dataType = "Date")
    private Date endTime;

    /**
     * 车间名称
     */
    @ApiModelProperty(value = "车间名称", dataType = "String")
    private String siteName;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称", dataType = "String")
    private String deviceName;
    @ApiModelProperty(value = "", dataType = "String")
    private String emplName;

    /**
     * 车间班组Id
     */
    @ApiModelProperty(value = "车间班组Id", dataType = "String")
    private String siteId;

    /**
     * 工序顺序序号
     */
    @ApiModelProperty(value = "工序顺序序号", dataType = "String")
    private Byte sequenceOrderBy;

    /**
     * 工序顺序
     */
    @ApiModelProperty(value = "工序顺序", dataType = "String")
    private Short optSequence;

    /**
     * 工艺名称
     */
    @ApiModelProperty(value = "工艺名称", dataType = "String")
    private String optName;

    /**
     * 工艺版本
     */
    @ApiModelProperty(value = "工艺版本", dataType = "String")
    private String optVer;

    /**
     * 是当前的
     */
    @ApiModelProperty(value = "是否当前工序 1是  0 否 ", dataType = "String")
    private Byte isCurrent;

    /**
     * 准结工时
     */
    @ApiModelProperty(value = "准结工时", dataType = "String")
    private BigDecimal prepareEndHours;

    /**
     * 单件工时
     */
    @ApiModelProperty(value = "单件工时", dataType = "String")
    private BigDecimal singlePieceHours;

    /**
     * 工序顺序序号
     */
    @ApiModelProperty(value = "工序顺序序号", dataType = "String")
    private Byte sequenceOrderBy2;

    /**
     * 是否并行（0否  1是）
     */
    @ApiModelProperty(value = "是否并行（0否  1是）", dataType = "String")
    private Byte optParallelType;

    /**
     * 可分配数量
     */
    @ApiModelProperty(value = "可分配数量", dataType = "Integer")
    private Integer assignableQty;

    /**
     * 工序类型
     */
    @ApiModelProperty(value = "工序类型", dataType = "String")
    private String optType;

    /**
     * 工序字典表id
     */
    @ApiModelProperty(value = "工序字典表id", dataType = "String")
    private String operatiponId;

    /**
     * 冷却方式（热工）
     */
    @ApiModelProperty(value = "冷却方式（热工）", dataType = "String")
    private String coolType;

    /**
     * 保温时间h（热工）
     */
    @ApiModelProperty(value = "保温时间h（热工）", dataType = "String")
    private String holdTime;

    /**
     * 实施温度℃（热工）
     */
    @ApiModelProperty(value = "实施温度℃（热工）", dataType = "String")
    private String tempWork;

    /**
     * 温度上限℃（热工）
     */
    @ApiModelProperty(value = "温度上限℃（热工）", dataType = "String")
    private String tempUp;

    /**
     * 温度下限℃（热工）
     */
    @ApiModelProperty(value = "温度下限℃（热工）", dataType = "String")
    private String tempDown;

    /**
     * 设备分类code（热工）
     */
    @ApiModelProperty(value = "设备分类code（热工）", dataType = "String")
    private String typeCode;

    /**
     * 设备分类名称（热工）
     */
    @ApiModelProperty(value = "设备分类名称（热工）", dataType = "String")
    private String typeName;

    /**
     * 预装炉id
     */
    @ApiModelProperty(value = "预装炉id", dataType = "String")
    private Long prechargeFurnaceId;


    /**
     * 工序号
     */
    @ApiModelProperty(value = "工序号", dataType = "String")
    private String optNo;

    /**
     * 工艺信息
     */
    @ApiModelProperty(value = "工艺信息", dataType = "String")
    private String routerInfo;

    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;

    /**
     * 跟单号
     */
    @ApiModelProperty(value = "跟单号", dataType = "String")
    private String trackNo;

    /**
     * 工作号
     */
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;

    /**
     * 图号
     */
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;

    /**
     * 重量
     */
    @ApiModelProperty(value = "重量", dataType = "String")
    private Double weight;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private Integer number;

    /**
     * 跟单类型（0单件  1批次）
     */
    @ApiModelProperty(value = "跟单类型（0单件  1批次）", dataType = "String")
    private String trackType;

    /**
     * 材质
     */
    @ApiModelProperty(value = "材质", dataType = "String")
    private String texture;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date thCreateTime;

    /**
     * 工艺id
     */
    @ApiModelProperty(value = "工艺id", dataType = "String")
    private String routerId;
    /**
     * 是否为长周期 0 :否  1 是
     */
    @ApiModelProperty(value = "是否为长周期 0 :否  1 是", dataType = "String")
    private Byte isLongPeriod;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称", dataType = "String")
    private String projectName;


    /**
     * 项目名称
     */
    @ApiModelProperty(value = "ERP物料编码", dataType = "String")
    private String erpProductCode;


    /**
     * 原材料库存
     */
    @ApiModelProperty(value = "原材料库存", dataType = "String")
    private Integer storeNumber;
    /**
     * 计划编号
     */
    @ApiModelProperty(value = "计划编号", dataType = "String")
    private String projCode;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", dataType = "Date")
    private Date planEndTime;

    /**
     * 下料规格
     */
    @ApiModelProperty(value = "下料规格", dataType = "Date")
    private String blankSpecifi;
    @ApiModelProperty(value = "锻件重量", dataType = "String")
    private String forgWeight;
    @TableField(exist = false)
    @ApiModelProperty(value = "单重", dataType = "String")
    private String pieceWeight;
    @TableField(exist = false)
    @ApiModelProperty(value = "钢水重量", dataType = "String")
    private String weightMolten;
}
