package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * produce_disqualification
 *
 * @author
 */
@Data
public class Disqualification extends BaseEntity<Disqualification> {

    private static final long serialVersionUID = -1482899816223711387L;
    /**
     * 跟单Id
     */
    @ApiModelProperty(value = "跟单Id")
    private String trackHeadId;

    /**
     * 跟单类型
     */
    @ApiModelProperty(value = "跟单类型")
    private String trackHeadType;


    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private int number;

    /**
     * 跟单工序Id
     */
    @ApiModelProperty(value = "跟单工序ID")
    private String trackItemId;

    /**
     * 炉号
     */
    @ApiModelProperty(value = "炉号")
    private String heatNumber;

    /**
     * 申请单编号
     */
    @ApiModelProperty(value = "申请单编号")
    private String processSheetNo;

    /**
     * 不合格流程类型
     */
    @ApiModelProperty(value = "不合格流程类型")
    private Integer type;

    /**
     * 不合格类型
     */
    @ApiModelProperty(value = "不合格类型")
    private String disqualificationType;

    /**
     * 工作号
     */
    @ApiModelProperty(value = "工作号")
    private String workNo;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号")
    private String productNo;

    /**
     * 跟单编号
     */
    @ApiModelProperty(value = "跟单编号")
    private String trackNo;

    /**
     * 零部件名称
     */
    @ApiModelProperty(value = "零部件名称")
    private String partName;

    /**
     * 零部件材料
     */
    @ApiModelProperty(value = "零部件材料")
    private String partMaterials;

    /**
     * 零部件图号
     */
    @ApiModelProperty(value = "零部件图号")
    private String partDrawingNo;

    /**
     * 送出的车间
     */
    @ApiModelProperty(value = "送出车间")
    private String missiveBranch;


    /**
     * 工序名称
     */
    @ApiModelProperty(value = "工序名称")
    private String itemName;

    /**
     * 工序类型
     */
    @ApiModelProperty(value = "工序类型")
    private String itemType;

    /**
     * 不合格数量
     */
    @ApiModelProperty(value = "不合格数量")
    private Integer disqualificationNum;

    /**
     * 质检完成时间
     */
    @ApiModelProperty(value = "质检完成时间")
    private Date qualityCompleteTime;

    /**
     * 质控工程师姓名
     */
    @ApiModelProperty(value = "质控工程师姓名")
    private String qualityCheckName;

    /**
     * 质控工程师
     */
    @ApiModelProperty(value = "质控工程师")
    private String qualityCheckBy;

    /**
     * 开单时间
     */
    private Date orderTime;

    @ApiModelProperty(value = "来源状态 1=有来源 0=无来源")
    private Integer sourceType;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
    private String tenantId;

    @ApiModelProperty(value = "关单日期")
    private Date closeTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "文件列表", dataType = "List<DisqualificationAttachment>")
    private List<DisqualificationAttachment> attachmentList = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "检验人员", dataType = "List<DisqualificationUserOpinion>")
    private List<TenantUserVo> userList;
}
