package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author wcy
 * 钻机整改单据
 * @TableName produce_drilling_rectification
 */
@TableName(value = "produce_drilling_rectification")
@Data
public class ProduceDrillingRectification implements Serializable {
    /**
     * id
     */
    @Id
    private String id;

    /**
     * 整改单编号
     */
    @ApiModelProperty(value = "整改单编号")
    private String orderNo;

    /**
     * 工作号
     */
    @ApiModelProperty(value = "工作号")
    private String workNo;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /**
     * 零部件名称
     */
    @ApiModelProperty(value = "零部件名称")
    private String partName;

    /**
     * 整改来源
     */
    @ApiModelProperty(value = "整改来源")
    private String source;

    /**
     * 类别
     */
    @ApiModelProperty(value = "类别")
    private String type;

    /**
     * 责任单位
     */
    @ApiModelProperty(value = "责任单位")
    private String dutyUnit;

    /**
     * 整改单位
     */
    @ApiModelProperty(value = "整改单位")
    private String rectificationUnit;

    /**
     * 整改工序名称
     */
    @ApiModelProperty(value = "整改工序名称")
    private String optName;

    /**
     * 整改措施
     */
    @ApiModelProperty(value = "整改措施")
    private String measure;

    /**
     * 返修人
     */
    @ApiModelProperty(value = "返修人")
    private String rebackUser;

    /**
     * 检验人
     */
    @ApiModelProperty(value = "检验人")
    private String checkUser;

    /**
     * 检验结果
     */
    @ApiModelProperty(value = "检验结果")
    private String result;

    /**
     * 问题描述
     */
    @ApiModelProperty(value = "问题描述")
    private String detail;

    /**
     * branchCode
     */
    @ApiModelProperty(value = "branchCode")
    private String branchCode;

    /**
     * tenantId
     */
    @ApiModelProperty(value = "tenantId")
    private String tenantId;

    /**
     * 提交状态：0：未提报；1：已提报；2：已关闭
     */
    @ApiModelProperty(value = "提交状态：0：未提报；1：已提报；2：已关闭")
    private String status;

    /**
     * 办理单位
     */
    @ApiModelProperty(value = "办理单位")
    private String manageUnit;

    /**
     * 开具人是否提报
     */
    @ApiModelProperty(value = "开具人是否提报")
    private String isSendCommit;

    /**
     * 办理单位是否提报
     */
    @ApiModelProperty(value = "办理单位是否提报")
    private String isUnitCommit;

    /**
     * 整改单位是否提报
     */
    @ApiModelProperty(value = "整改单位是否提报")
    private String isUpdateCommit;

    /**
     * 整改检验是否提报
     */
    @ApiModelProperty(value = "整改检验是否提报")
    private String isCheckCommit;

    /**
     * 提报日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提报日期")
    private Date createDate;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 责任单位
     */
    @ApiModelProperty(value = "责任单位")
    @TableField(exist = false)
    private List<String> dutyUnitList;

    /**
     * 整改单位
     */
    @ApiModelProperty(value = "整改单位")
    @TableField(exist = false)
    private List<String> rectificationUnitList;

    /**
     * 整改来源
     */
    @ApiModelProperty(value = "整改来源")
    @TableField(exist = false)
    private List<String> sourceList;

    /**
     * 类别
     */
    @ApiModelProperty(value = "类别")
    @TableField(exist = false)
    private List<String> typeList;

    /**
     * 整改工序
     */
    @ApiModelProperty(value = "整改工序")
    @TableField(exist = false)
    private List<String> optList;
}