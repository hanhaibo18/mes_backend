package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 马峰
 * @Description 报工实体类
 * @CreateTime: 2022年02月02日 08:08:00
 * @ModifyTime: 2022年08月15日 12:08:00
 */
@Data
public class TrackComplete extends BaseEntity<TrackComplete> {

    private static final long serialVersionUID = 2731220432204410300L;
    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;
    /**
     * 跟单工序项ID
     */
    protected String tiId;
    /**
     * 跟单工序项ID
     */
    protected String assignId;
    /**
     * 完工用户ID
     */
    protected String userId;
    /**
     * 完工用户ID
     */
    protected String userName;
    /**
     * 跟单ID
     */
    protected String trackId;
    /**
     * 跟单ID
     */
    protected String trackNo;
    /**
     * 产品编号
     */
    protected String prodNo;
    /**
     * 外协单号
     */
    protected String outNo;
    /**
     * 完工设备ID
     */
    protected String deviceId;
    /**
     * 完工设备ID
     */
    protected String deviceName;
    /**
     * 完工数量
     */
    protected Double completedQty;
    /**
     * 拒绝数量
     */
    protected Double rejectQty;

    /**
     * 完工工时
     */
    protected Double completedHours;
    /**
     * 实际工时
     */
    protected Double actualHours;
    /**
     * 报告工时
     */
    protected Double reportHours;
    /**
     * 静态工时
     */
    protected Double staticHours;
    /**
     * 报工人
     */
    protected String completeBy;

    /**
     * 报工时间
     */
    protected Date completeTime;
    /**
     * 质检人
     */
    protected String qualityCheckBy;
    /**
     * 质检车间?
     */
    protected String qualityCheckBranch;
    /**
     * 备注
     */
    protected String remarks;


    //北石新增报工字段
    /**
     * 实用固定机时
     */
    protected Double actualFixHours;
    /**
     * 实用变动机时（正常班）
     */
    protected Double actualNomalHours;

    /**
     * 实用变动机时（加班）
     */
    protected Double actualOverHours;
    /**
     * 完成固定机时
     */
    protected Double completedFixHours;
    /**
     * 完成变动机时
     */
    protected Double completedChangeHours;
    /**
     * 单件补付机时
     */
    protected Double singleAddHours;
    /**
     * 辅助工时
     */
    protected Double auxiliaryHours;

    /**
     * 探伤结果
     */
    protected String detectionResult;
    @TableField(exist = false)
    private String workNo;
    @TableField(exist = false)
    private String drawingNo;
    @TableField(exist = false)
    private Integer trackType;
    @TableField(exist = false)
    private Integer trackQty;
    @TableField(exist = false)
    private String trackNo2;
    @TableField(exist = false)
    private String productNo;


    @TableField(exist = false)
    private String optCode;
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

    @ApiModelProperty(value = "是否修改", dataType = "Integer")
    @TableField(exist = false)
    private Integer isUpdate;

    @ApiModelProperty(value = "产品名称", dataType = "String")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty(value = "是否给予准结工时", dataType = "int")
    private int isPrepare;

    @ApiModelProperty(value = "质检结果", dataType = "String")
    @TableField(exist = false)
    private String qualityResult;

    @ApiModelProperty(value = "订单编号", dataType = "String")
    @TableField(exist = false)
    private String productionOrder;

    @ApiModelProperty(value = "optId", dataType = "String")
    @TableField(exist = false)
    private String optId;


    /**
     * 工时查询总工时
     */
    @ApiModelProperty(value = "总工时", dataType = "Double")
    @TableField(exist = false)
    protected Double totalHours;

    @TableField(exist = false)
    private List<TrackComplete> trackCompleteList;
}
