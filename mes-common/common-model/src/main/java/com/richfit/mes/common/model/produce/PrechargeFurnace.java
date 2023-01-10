package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zhiqiang.lu
 * @Date: 2023.1.4
 */
@Data
public class PrechargeFurnace {

    private static final long serialVersionUID = -5801273490970600632L;

    public static final String STATE_WKG = "0";
    public static final String NO_START_WORK = "0";
    public static final String YES_START_WORK = "1";
    public static final String END_START_WORK = "2";

    @TableId(type = IdType.AUTO)
    protected Long id;

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

    @ApiModelProperty(value = "预装温度", dataType = "String")
    private String tempWork;
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String optName;
    @ApiModelProperty(value = "设备类型", dataType = "String")
    private String typeCode;
    @ApiModelProperty(value = "状态 0 初始  1 开工  2 完工", dataType = "String")
    private String status;
    @ApiModelProperty(value = "状态 0 未开工  1 已开工", dataType = "String")
    private String stepStatus;
    @ApiModelProperty(value = "开工人", dataType = "String")
    private String startWorkBy;
    @ApiModelProperty(value = "是否分批  0否  1是", dataType = "String")
    private String isBatch;
    @ApiModelProperty(value = "车间班组组织代码", dataType = "String")
    private String siteId;
    @ApiModelProperty(value = "炉批号", dataType = "String")
    private String furnaceNo;
    @ApiModelProperty(value = "处理炉", dataType = "String")
    private String dealFurnace;
    @ApiModelProperty(value = "上步骤", dataType = "String")
    private String upStep;
    @ApiModelProperty(value = "当前步骤", dataType = "String")
    private String currStep;
    @ApiModelProperty(value = "次数", dataType = "String")
    private String number;

}
