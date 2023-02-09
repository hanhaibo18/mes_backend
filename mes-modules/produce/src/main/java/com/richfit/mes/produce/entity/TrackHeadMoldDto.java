package com.richfit.mes.produce.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: TrackHeadMoldDto.java
 * @Author: Hou XinYu
 * @Description: Head和Mold 视图
 * @CreateTime: 2023年02月09日 17:03:00
 */
@Data
public class TrackHeadMoldDto {
    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "跟单号")
    private String trackNo;

    @ApiModelProperty(value = "跟单类型 0单件 1批次")
    private String trackType;

    @ApiModelProperty(value = "工作号")
    private String workNo;

    @ApiModelProperty(value = "产品编号")
    private String productNo;

    @ApiModelProperty(value = "产品编号描述")
    private String productNoDesc;

    @ApiModelProperty(value = "物料编号")
    private String materialNo;

    @ApiModelProperty(value = "图号")
    private String drawingNo;

    @ApiModelProperty(value = "生产订单编号")
    private String productionOrder;

    @ApiModelProperty(value = "审批人")
    private String approvalBy;

    @ApiModelProperty(value = "审批时间")
    private Date approvalTime;

    @ApiModelProperty(value = "审批状态 0待审批 1通过 2打回")
    private String approvalStatus;

    @ApiModelProperty(value = "状态 0已生成待派工 1在制 2完工 3作废 4删除")
    private String status;

    @ApiModelProperty(value = "试棒数量")
    private Integer testBarNumber;

    @ApiModelProperty(value = "试棒类型")
    private String testBarType;

    @ApiModelProperty(value = "炉批号")
    private String batchNo;

    @ApiModelProperty(value = "准备工时 0首件 1全部")
    private String isFirst;

    @ApiModelProperty(value = "优先级")
    private String priority;

    @ApiModelProperty(value = "使用的料单产品编码", dataType = "String")
    private String userProductNo;

    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "签发人")
    private String issueBy;

    @ApiModelProperty(value = "签发时间")
    private Date issueTime;

    @ApiModelProperty(value = "来料合格证号")
    private String materialCertificateNo;

    @ApiModelProperty(value = "试棒跟单编号")
    private String testBarTrackNo;

    @ApiModelProperty(value = "合同编号")
    private String contractNo;

    @ApiModelProperty(value = "组织机构编号")
    private String branchCode;

    @ApiModelProperty(value = "工序版本号")
    private String routerVer;

    @ApiModelProperty(value = "跟单完工时间")
    private Date completeTime;

    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String materialName;

    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;

    @ApiModelProperty(value = "代用材料", dataType = "String")
    private String replaceMaterial;

    @ApiModelProperty(value = "重量", dataType = "Float")
    private Float weight;

    @ApiModelProperty(value = "材质", dataType = "String")
    private String texture;

    @ApiModelProperty(value = "材质", dataType = "String")
    private String templateCode;

    @ApiModelProperty(value = "计划Id", dataType = "String")
    private String workPlanId;

    @ApiModelProperty(value = "计划号")
    private String workPlanNo;

    @ApiModelProperty(value = "计划项目号")
    private String workPlanProjectNo;

    @ApiModelProperty(value = "计划结束时间")
    private Date workPlanEndTime;

    @ApiModelProperty(value = "工艺id", dataType = "String")
    private String routerId;

    @ApiModelProperty(value = "跟单生成类型 0课程生成  1物料生成  2计划生成", dataType = "String")
    private String type;

    @ApiModelProperty(value = "是否试棒跟单  0否  1是", dataType = "String")
    private String isTestBar;

    @ApiModelProperty(value = "项目bomID", dataType = "String")
    private String projectBomId;

    @ApiModelProperty(value = "关联项目BOM的workno", dataType = "String")
    private String projectBomWork;

    @ApiModelProperty(value = "联项目BOM的名称", dataType = "String")
    private String projectBomName;

    @ApiModelProperty(value = "bom分组选择", dataType = "String")
    private String projectBomGroup;

    @ApiModelProperty(value = "跟单分类：1机加  2装配 3热处理 4钢结构", dataType = "String")
    private String classes;

    @ApiModelProperty(value = "完工资料：Y是", dataType = "String")
    private String isCompletionData;

    @ApiModelProperty(value = "是否批次：Y是 N否", dataType = "String")
    private String isBatch;

    @ApiModelProperty(value = "完成数量", dataType = "Integer")
    private Integer numberComplete;

    @ApiModelProperty(value = "原跟单id", dataType = "String")
    private String originalTrackId;

    @ApiModelProperty(value = "原跟单编号", dataType = "String")
    private String originalTrackNo;

    @ApiModelProperty(value = "分流（生产线数量）", dataType = "Integer")
    private Integer flowNumber;

    @ApiModelProperty(value = "订单id", dataType = "String")
    private String productionOrderId;

    @TableField(exist = false)
    @ApiModelProperty(value = "选择的毛胚物料号码", dataType = "String")
    private String selectedMaterialNo;

    @ApiModelProperty(value = "跟单附件id，多个用，隔开", dataType = "String")
    private String filesId;

    /**
     * 模具类别(0=制新,1=返修)
     */
    @ApiModelProperty(value = "模具类别")
    private Integer moldType;

    /**
     * 模具材质(0=木质,1=气化模)
     */
    @ApiModelProperty(value = "模具材质")
    private Integer dieMaterial;

    /**
     * 实样数量
     */
    @ApiModelProperty(value = "实样数量")
    private Integer realSamplesQty;

    /**
     * 芯盒数量
     */
    @ApiModelProperty(value = "芯盒数量")
    private Integer coreBoxQty;

    /**
     * 浇道数量
     */
    @ApiModelProperty(value = "浇道数量")
    private Integer pouringGateQty;

    /**
     * 冒口数量
     */
    @ApiModelProperty(value = "冒口数量")
    private Integer feedHeadQty;

    /**
     * 型板数量
     */
    @ApiModelProperty(value = "型板数量")
    private Integer templateQty;

    /**
     * 胎垫数量
     */
    @ApiModelProperty(value = "胎垫数量")
    private Integer tiresQty;

    /**
     * 活块数量
     */
    @ApiModelProperty(value = "活块数量")
    private Integer dieInsertQty;

    /**
     * 冷铁数量
     */
    @ApiModelProperty(value = "冷铁数量")
    private Integer chillingBlockQty;

    /**
     * 刮板数量
     */
    @ApiModelProperty(value = "刮板数量")
    private Integer scraperQty;

    /**
     * 完工日期
     */
    @ApiModelProperty(value = "完工日期")
    private Date completionDate;
}
