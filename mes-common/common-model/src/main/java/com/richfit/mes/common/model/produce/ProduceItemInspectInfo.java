package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
@TableName(value = "produce_item_inspect_info")
public class ProduceItemInspectInfo{
    @ApiModelProperty(value = "探伤委托单id")
    private String powerId;
    @ApiModelProperty(value = "探伤记录id")
    private String inspectRecordId;
    @ApiModelProperty(value = "探伤记录模板类型（1、mt 2、pt 3、rt 4、ut）")
    private String tempType;
    @ApiModelProperty(value = "探伤记录审核人")
    private String auditBy;
    @ApiModelProperty(value = "探伤记录审核人")
    private String checkBy;
    @ApiModelProperty(value = "探伤记录审核状态")
    private String isAudit;
    @ApiModelProperty(value = "探伤记录审核意见")
    private String auditRemark;
    @ApiModelProperty(value = "探伤记录里的核验结果（0、合格   1、不合格）")
    private String inspectionResults;
    @ApiModelProperty(value = "是否是最近的记录（0、否  1、是）")
    private String isNew;
    @ApiModelProperty(value = "记录号")
    private String recordNo;
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
