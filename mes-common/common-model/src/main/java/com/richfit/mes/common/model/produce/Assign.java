package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 马峰
 * @Description 派工表
 */
@Data
public class Assign extends BaseEntity<Assign> {

    @ApiModelProperty(value = "id", dataType = "String")
    private String id;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", dataType = "String")
    private String tenantId;
    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码", dataType = "String")
    private String branchCode;
    /**
     * 跟单工序项ID
     */
    @ApiModelProperty(value = "跟单工序项ID", dataType = "String")
    private String tiId;
    /**
     * 跟单ID
     */
    @ApiModelProperty(value = "跟单ID", dataType = "String")
    private String trackId;
    /**
     * 跟单编号
     */
    @ApiModelProperty(value = "跟单编号", dataType = "String")
    private String trackNo;
    /**
     * 派工用户ID
     */
    @ApiModelProperty(value = "派工用户ID", dataType = "String")
    private String userId;
    /**
     * 派工用户ID
     */
    @ApiModelProperty(value = "派工用户ID", dataType = "String")
    private String emplName;
    /**
     * 派工用户名称
     */
    @ApiModelProperty(value = "派工用户名称", dataType = "String")
    private String siteId;
    /**
     * 派工工位名称
     */
    @ApiModelProperty(value = "派工工位名称", dataType = "String")
    private String siteName;
    /**
     * 派工设备ID
     */
    @ApiModelProperty(value = "派工设备ID", dataType = "String")
    private String deviceId;
    /**
     * 派工设备名称
     */
    @ApiModelProperty(value = "派工设备名称", dataType = "String")
    private String deviceName;
    /**
     * 派工优先级  3=High、2=Medium、1=Normal、0=Low
     */
    @ApiModelProperty(value = "派工优先级  3=High、2=Medium、1=Normal、0=Low", dataType = "int")
    private int priority;
    /**
     * 派工数量
     */
    @ApiModelProperty(value = "派工数量", dataType = "int")
    private int qty;
    /**
     * 可报工数
     */
    @ApiModelProperty(value = "可报工数", dataType = "int")
    private int availQty;

    /**
     * 派工状态
     */
    @ApiModelProperty(value = "派工状态", dataType = "int")
    private int state;

    /**
     * 派工人
     */
    @ApiModelProperty(value = "派工人", dataType = "String")
    protected String assignBy;

    /**
     * 派工时间
     */
    @ApiModelProperty(value = "派工时间", dataType = "Date")
    protected Date assignTime;

    /**
     * 计划开始时间
     */
    @ApiModelProperty(value = "计划开始时间", dataType = "Date")
    protected Date startTime;

    /**
     * 计划结束时间
     */
    @ApiModelProperty(value = "计划结束时间", dataType = "Date")
    protected Date endTime;

    @TableField(exist = false)
    private String drawingNo;
    @TableField(exist = false)
    private String trackType;
    @TableField(exist = false)
    private String trackQty;
    @TableField(exist = false)
    private String trackNo2;
    @TableField(exist = false)
    private String productNo;
    @TableField(exist = false)
    private String optName;
    @TableField(exist = false)
    private Integer optType;
    @TableField(exist = false)
    private Integer optSequence;
    @TableField(exist = false)
    private Integer technologySequence;
    @TableField(exist = false)
    private Integer optParallelType;
    @TableField(exist = false)
    private Integer sequenceOrderBy;
    @TableField(exist = false)
    private Double prepareEndHours;
    @TableField(exist = false)
    private Double singlePieceHours;

    @TableField(exist = false)
    private List<AssignPerson> assignPersons;


    @TableField(exist = false)
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;
    @TableField(exist = false)
    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String partsName;
    @TableField(exist = false)
    @ApiModelProperty(value = "总数量", dataType = "String")
    private Integer totalQuantity;
    @TableField(exist = false)
    @ApiModelProperty(value = "可派工数量", dataType = "String")
    private Integer dispatchingNumber;
    @TableField(exist = false)
    @ApiModelProperty(value = "重量", dataType = "Float")
    private Float weight;
    @TableField(exist = false)
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "计划号", dataType = "String")
    private String workPlanNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "工艺Id", dataType = "String")
    private String routerId;
}
