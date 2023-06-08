package com.richfit.mes.base.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wcy
 * @date 2023/6/5 10:07
 */

@Data
public class ReceiptDTO extends QueryPageDto implements Serializable {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 交接单号
     */
    @ApiModelProperty(value = "交接单号")
    private String connectNo;

    /**
     * 配套钻机
     */
    @ApiModelProperty(value = "配套钻机")
    private String driNo;

    /**
     * 工作号
     */
    @ApiModelProperty(value = "工作号")
    private String workNo;

    /**
     * 产品图号
     */
    @ApiModelProperty(value = "产品图号")
    private String drawNo;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String prodDesc;

    /**
     * 项目bom
     */
    @ApiModelProperty(value = "项目bom")
    private String bomId;

    /**
     * 项目bom名称
     */
    @ApiModelProperty(value = "项目bom名称")
    private String bomName;

    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号")
    private String productNo;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer number;

    /**
     * 校验员
     */
    @ApiModelProperty(value = "校验员")
    private String checkUser;

    /**
     * 校验日期
     */
    @ApiModelProperty(value = "校验日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkDate;

    /**
     * 提交人
     */
    @ApiModelProperty(value = "提交人")
    private String createBy;

    /**
     * 车间编码
     */
    @ApiModelProperty(value = "车间编码")
    private String branchCode;

    /**
     * 验收人
     */
    @ApiModelProperty(value = "验收人")
    private String receiveUser;

    /**
     * 验收单位：默认钻机分公司
     */
    @ApiModelProperty(value = "验收单位：默认钻机分公司")
    private String receiveUnit;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "交接状态0：待交接；1：已交接；2：已拒收")
    private String status;

    @ApiModelProperty(value = "是否选中:1:选中；2：未选中")
    private Integer ifCheck;

    /**
     * 子bom信息
     */
    @ApiModelProperty(value = "子bom信息")
    private List<ReceiptExtendDTO> receiptExtendDTOList;
}
