package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 马峰
 * @Description 工序质检表
 */
@Data
public class TrackCheck extends BaseEntity<TrackCheck> {

    private static final long serialVersionUID = -822677447762645846L;
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
    @ApiModelProperty(value = "跟单工序项ID(trackNo)", dataType = "String")
    private String tiId;
    /**
     * 跟单ID
     */
    @ApiModelProperty(value = "跟单ID", dataType = "String")
    private String thId;
    /**
     * 质检类型 0-半检 1-全检 2-抽检
     */
    @ApiModelProperty(value = "质检类型 0-半检 1-全检 2-抽检", dataType = "int")
    private int type;
    /**
     * 质检结果 0-不合格 1-合格
     */
    @ApiModelProperty(value = "质检结果 0-不合格 1-合格", dataType = "int")
    private int result;
    /**
     * 不合格原因
     */
    @ApiModelProperty(value = "不合格原因", dataType = "String")
    private String reason;
    /**
     * 合格数量
     */
    @ApiModelProperty(value = "合格数量", dataType = "int")
    private int qualify;
    /**
     * 不合格数量
     */
    @ApiModelProperty(value = "不合格数量", dataType = "int")
    private int unqualify;
    /**
     * 让步数量
     */
    @ApiModelProperty(value = "让步数量", dataType = "int")
    private int stepQty;
    /**
     * 返修数量
     */
    @ApiModelProperty(value = "返修数量", dataType = "int")
    private int fixQty;
    /**
     * 废弃数量
     */
    @ApiModelProperty(value = "废弃数量", dataType = "int")
    private int discardQty;
    /**
     * 处理单号
     */
    @ApiModelProperty(value = "处理单号", dataType = "String")
    private String dealNo;

    /**
     * 处理人
     */
    @ApiModelProperty(value = "处理人", dataType = "String")
    private String dealBy;
    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间", dataType = "Date")
    private Date dealTime;

    /**
     * 处理意见
     */
    @ApiModelProperty(value = "处理意见", dataType = "String")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "质检人", dataType = "String")
    private String userName;


    @TableField(exist = false)
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "数量", dataType = "int")
    private int number;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String optName;
    @TableField(exist = false)
    @ApiModelProperty(value = "工序类型", dataType = "String")
    private String optType;
    @TableField(exist = false)
    @ApiModelProperty(value = "工序Id", dataType = "String")
    private String optId;

    @TableField(exist = false)
    @ApiModelProperty(value = "文件Id集合", dataType = "List<String>")
    private List<String> fileId;
    @TableField(exist = false)
    @ApiModelProperty(value = "质检详情集合", dataType = "List<TrackCheckDetail>")
    private List<TrackCheckDetail> checkDetailsList;

    @TableField(exist = false)
    @ApiModelProperty(value = "质检详情集合", dataType = "Integer")
    private Integer isCurrent;

    @TableField(exist = false)
    @ApiModelProperty(value = "跟单号", dataType = "String")
    private String trackNo;

    @TableField(exist = false)
    @ApiModelProperty(value = "下工序绑定", dataType = "NextProcess")
    private NextProcess nextProcess;
}
