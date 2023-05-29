package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhiqiang.lu
 * @Description 跟单
 */
@Data
@ApiModel(value = "跟单管理")
public class TrackHead extends BaseEntity<TrackHead> {

    private static final long serialVersionUID = 6336423092552908350L;

    /**
     * 跟单类型（机加）
     */
    public static final String TRACKHEAD_CLASSES_JJ = "1";

    /**
     * 跟单类型（装配）
     */
    public static final String TRACKHEAD_CLASSES_ZP = "2";

    /**
     * 跟单类型（热处理）
     */
    public static final String TRACKHEAD_CLASSES_RCL = "3";

    /**
     * 跟单类型（钢结构）
     */
    public static final String TRACKHEAD_CLASSES_GJG = "4";

    /**
     * 单件批量
     */
    public static final String TRACKHEAD_BATCH_YES = "Y";

    /**
     * 非单件批量
     */
    public static final String TRACKHEAD_BATCH_NO = "N";


    /**
     * 初始跟单
     */
    public static final String STATUS_0 = "0";

    /**
     * 跟单在制
     */
    public static final String STATUS_1 = "1";


    /**
     * 跟单完工
     */
    public static final String STATUS_2 = "2";

    /**
     * 打印跟单
     */
    public static final String STATUS_4 = "4";


    /**
     * 作废跟单
     */
    public static final String STATUS_5 = "5";


    /**
     * 生产完工资料
     */
    public static final String STATUS_8 = "8";


    /**
     * 已交库
     */
    public static final String STATUS_9 = "9";


    /**
     * 单价
     */
    public static final String TRACK_TYPE_0 = "0";

    /**
     * 批次
     */
    public static final String TRACK_TYPE_1 = "1";

    /**
     * 非试棒/实验跟单
     */
    public static final String IS_TEST_BAR_0 = "0";

    /**
     * 试棒/实验跟单
     */
    public static final String IS_TEST_BAR_1 = "1";

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

    @ApiModelProperty(value = "产品编号 连续的")
    private String productNoContinuous;

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

    @ApiModelProperty(value = "合格证号")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String certificateNo;

    @ApiModelProperty(value = "合同编号")
    private String contractNo;


    @ApiModelProperty(value = "组织机构编号")
    private String branchCode;

    @ApiModelProperty(value = "工序版本号")
    private String routerVer;

    @ApiModelProperty(value = "跟单完工时间")
    private Date completeTime;

    @ApiModelProperty(value = "跟单工序")
    @TableField(exist = false)
    private List<TrackItem> trackItems;

    @ApiModelProperty(value = "物料产品信息列表，用于根据物料信息生产跟单")
    @TableField(exist = false)
    private List<Map> storeList;


    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String materialName;

    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;

    @ApiModelProperty(value = "代用材料", dataType = "String")
    private String replaceMaterial;

    @ApiModelProperty(value = "重量", dataType = "Float")
    private float weight;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "项目名称")
    private String projectName;

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


    @ApiModelProperty(value = "试棒编号", dataType = "String")
    private String testBarNo;

    @ApiModelProperty(value = "毛坯类型", dataType = "String")
    private String workblankType;
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
    @ApiModelProperty(value = "工序id")
    private String tiId;

    @TableField(exist = false)
    @ApiModelProperty(value = "材质", dataType = "String")
    private String finalProductNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "原工序顺序")
    private String originalOptSequence;

    @TableField(exist = false)
    @ApiModelProperty(value = "下工序")
    private String nextOptSequence;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序顺序")
    private Integer optSequence;
    @TableField(exist = false)
    @ApiModelProperty(value = "是当前的")
    private int isCurrent;
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


    /**
     * 无用属性
     */

//    private Integer startNo;
//    private Integer endNo;
//    private String suffixNo;
//    private String userMaterialNo;


    @TableField(exist = false)
    @ApiModelProperty(value = "委托单信息")
    private PhysChemOrder physChemOrder;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序号")
    private String optNo;

}
