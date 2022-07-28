package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 合格证表
 */
@Data
public class Certificate extends BaseEntity<Certificate> {

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "分公司")
    private String branchCode;

    @ApiModelProperty(value = "合格证编号")
    private String certificateNo;

    @ApiModelProperty(value = "类型 0工序合格证 1完工合格证")
    private String type;

    @ApiModelProperty(value = "下道工序车间")
    private String nextOptWork;

    @ApiModelProperty(value = "下工序")
    private String nextOpt;

    @ApiModelProperty(value = "检测员")
    private String checkName;

    @ApiModelProperty(value = "检测日期")
    private Date checkTime;

    @ApiModelProperty(value = "图号 from跟单")
    @TableField(exist = false)
    private String drawingNo;

    @ApiModelProperty(value = "产品编号 from跟单")
    @TableField(exist = false)
    private String productNo;

    @ApiModelProperty(value = "产品名称 from跟单")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty(value = "物料号 from跟单")
    @TableField(exist = false)
    private String materialNo;

    @ApiModelProperty(value = "数量")
    @TableField(exist = false)
    private Integer number;

    @ApiModelProperty(value = "工序Id from跟单工序")
    @TableField(exist = false)
    private String optId;

    @ApiModelProperty(value = "工序名称 from跟单工序")
    @TableField(exist = false)
    private String optName;

    @ApiModelProperty(value = "工序版本 from跟单工序")
    @TableField(exist = false)
    private String optVer;

    @ApiModelProperty(value = "工序序号 from跟单工序")
    @TableField(exist = false)
    private String sequenceOrderBy;

    @ApiModelProperty(value = "工序列表 ")
    @TableField(exist = false)
    private List<TrackCertificate> trackCertificates;

    //以下字段2022-06-23 gaol 添加
    @ApiModelProperty(value = "合格证来源 0：开出合格证 1：接收合格证")
    private String certOrigin;

    @ApiModelProperty(value = "是否下车间已接收 0：未接收  1：已接收")
    private String isPush;

    //2022-07-26 gaol 添加
    @ApiModelProperty(value = "是否推送工时给ERP  0：未推送  1：已推送")
    private String isSendWorkHour;

    @ApiModelProperty(value = "试棒数量 from跟单")
    @TableField(exist = false)
    private Integer testBarNumber;

    @ApiModelProperty(value = "试棒类型 from跟单")
    @TableField(exist = false)
    private String testBarType;

    @ApiModelProperty(value = "炉批号 from跟单")
    @TableField(exist = false)
    private String batchNo;

    @ApiModelProperty(value = "材质 from跟单")
    @TableField(exist = false)
    private String texture;

    @ApiModelProperty(value = "单重 from跟单")
    @TableField(exist = false)
    private Float weight;

    @ApiModelProperty(value = "零件名称 from跟单")
    @TableField(exist = false)
    private String materialName;
}
