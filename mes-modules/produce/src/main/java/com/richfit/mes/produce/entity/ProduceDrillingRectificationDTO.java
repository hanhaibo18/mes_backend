package com.richfit.mes.produce.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.model.util.PageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * @author wcy
 *
 * 钻机整改单据
 */
@Data
public class ProduceDrillingRectificationDTO extends PageDto implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
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
     * 整改工序id
     */
    @ApiModelProperty(value = "整改工序id")
    private String optId;

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

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endTime;

    /**
     * 区分是新增还是提交：新增状态为：未提报；提交状态为：已提报
     */
    @ApiModelProperty(value = "区分是新增还是提交：新增状态为：未提报；提交状态为：已提报")
    private String status;

    /**
     * 操作菜单
     */
    @ApiModelProperty(value = "操作菜单:1:钻机；2：责任单位；3：整改单位；4：质检")
    private String menuType;

    /**
     * 附件集合
     */
    @ApiModelProperty(value = "附件集合")
    private List<ProduceDrillingRectificationFileDTO> produceDrillingRectificationFileDTOList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}