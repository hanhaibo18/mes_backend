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

    @ApiModelProperty(value = "图号")
    @TableField(exist = false)
    private String drawingNo;

    @ApiModelProperty(value = "产品编号")
    @TableField(exist = false)
    private String productNo;

    @ApiModelProperty(value = "物料号")
    @TableField(exist = false)
    private String materialNo;

    @ApiModelProperty(value = "数量")
    @TableField(exist = false)
    private Integer number;

    @ApiModelProperty(value = "工序Id")
    @TableField(exist = false)
    private String optId;

    @ApiModelProperty(value = "工序名称")
    @TableField(exist = false)
    private String optName;

    @ApiModelProperty(value = "工序版本")
    @TableField(exist = false)
    private String optVer;

    @ApiModelProperty(value = "工序列表")
    @TableField(exist = false)
    private List<TrackCertificate> trackCertificates;

}
