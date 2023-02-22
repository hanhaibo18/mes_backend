package com.richfit.mes.produce.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: TrackHeadMoldDto.java
 * @Author: Hou XinYu
 * @Description: Head视图
 * @CreateTime: 2023年2月22日 16:22:33
 */
@Data
public class TrackHeadPublicVo {
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


    @ApiModelProperty(value = "跟单工序")
    @TableField(exist = false)
    private List<TrackItem> trackItems;

    @ApiModelProperty(value = "物料产品信息列表，用于根据物料信息生产跟单")
    @TableField(exist = false)
    private List<Map> storeList;


    /**
     * 锻造开始
     **/

    /**
     * 下料规格
     */
    @ApiModelProperty(value = "下料规格")
    private String blankingSpecification;

    /**
     * 代用材质
     */
    @ApiModelProperty(value = "代用材质")
    private String substituteMaterial;

    /**
     * 单号
     */
    @ApiModelProperty(value = "单号")
    private String forgeNumber;
    /**
     * 锻造结束
     **/


    /**
     * 铸造开始
     **/

    /**
     * 钢水重量
     */
    @ApiModelProperty(value = "钢水重量")
    private Double moltenSteel;

    /**
     * 铸件编号
     */
    @ApiModelProperty(value = "铸件编号")
    private String castingPartsNumber;

    /**
     * 工艺保温时间
     */
    @ApiModelProperty(value = "工艺保温时间")
    private Date processHoldingTime;

    /**
     * 浇铸温度
     */
    @ApiModelProperty(value = "浇铸温度")
    private String pouringTemperature;

    /**
     * 浇铸速度
     */
    @ApiModelProperty(value = "浇铸速度")
    private String pouringRate;

    /**
     * 毛坯调制 1=有 0=无
     */
    @ApiModelProperty(value = "毛坯调制 1=有 0=无")
    private boolean blankConditioning;

    /**
     * 毛坯探伤 1=有 0=无
     */
    @ApiModelProperty(value = "毛坯探伤 1=有 0=无")
    private boolean blankInspection;
    /**
     * 铸造结束
     **/


    /**
     * 跟单分流表开始
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "产品来源")
    private String productSource;

    @TableField(exist = false)
    @ApiModelProperty(value = "产品来源名称")
    private String productSourceName;

    @TableField(exist = false)
    @ApiModelProperty(value = "分流id", dataType = "String")
    private String flowId;

    @TableField(exist = false)
    @ApiModelProperty(value = "完工资料附件")
    private String completionData;

    @TableField(exist = false)
    @ApiModelProperty(value = "检验记录卡审核状态  Y已审核 N审核未通过")
    private String isExamineCardData;

    @TableField(exist = false)
    @ApiModelProperty(value = "检验记录卡生成状态  Y已生成")
    private String isCardData;

    @TableField(exist = false)
    @ApiModelProperty(value = "检验记录卡文件")
    private String cardData;

    @TableField(exist = false)
    @ApiModelProperty(value = "跟单id")
    private String trackHeadId;
    /**
     * 跟单分流表结束
     */
    /**
     * 跟单合格证
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工序产品编码")
    private String itemProductNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序产品数量")
    private String itemNumber;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序顺序")
    private String sequenceOrderBy;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序名称")
    private String optName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序id")
    private String optId;

    @TableField(exist = false)
    @ApiModelProperty(value = "材质", dataType = "String")
    private String finalProductNo;
    /**
     * 跟单合格证结束
     */

    /**
     * 跟单台账料单信息
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "料单创建日期", dataType = "Date")
    private Date storeCreateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "料单创建人", dataType = "String")
    private String storeCreateBy;

    @TableField(exist = false)
    @ApiModelProperty(value = "交来日期,入库时间", dataType = "Date")
    private Date storeInTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "交来日期，出库时间", dataType = "Date")
    private Date storeOutTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "上工序，物料来源", dataType = "String")
    private String storeMaterialSource;

    @TableField(exist = false)
    @ApiModelProperty(value = "单重", dataType = "String")
    private String storeWeight;

    @TableField(exist = false)
    @ApiModelProperty(value = "材质", dataType = "String")
    private String storeTexture;

    @TableField(exist = false)
    @ApiModelProperty(value = "零件编号", dataType = "String")
    private String storeWorkblankNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "炉号", dataType = "String")
    private String storeBatchNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "下工序车间", dataType = "String")
    private String certificateNextOptWork;

    @TableField(exist = false)
    @ApiModelProperty(value = "交出日期", dataType = "String")
    private String certificateCreateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "交出人", dataType = "String")
    private String certificateCreateBy;


    @TableField(exist = false)
    @ApiModelProperty(value = "委托单信息")
    private PhysChemOrder physChemOrder;


    @TableId(type = IdType.ASSIGN_UUID)
    protected String id;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    protected String createBy;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    protected Date createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected String modifyBy;

    /**
     * 更新日期
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Date modifyTime;

    /**
     * 备注字段
     */
    protected String remark;

}
