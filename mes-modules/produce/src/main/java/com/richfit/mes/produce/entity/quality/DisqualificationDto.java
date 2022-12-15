package com.richfit.mes.produce.entity.quality;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.produce.DisqualificationAttachment;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * produce_disqualification
 *
 * @author hou XinYu
 */
@Data
public class DisqualificationDto extends BaseEntity<DisqualificationDto> {

    /**
     * 跟单Id
     */
    private String trackHeadId;

    /**
     * 跟单工序Id
     */
    @ApiModelProperty(value = "跟单工序Id", dataType = "String")
    private String trackItemId;

    /**
     * 申请单编号
     */
    @ApiModelProperty(value = "申请单编号", dataType = "String")
    private String processSheetNo;

    /**
     * 不合格类型
     */
    private String type;

    /**
     * 0 = 未发布 1= 已发布 2 = 已关闭
     */
    @ApiModelProperty(value = "是否发布", dataType = "String")
    private Integer isIssue;

    /**
     * 工作号
     */
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;

    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;


    /**
     * 跟单编号
     */
    private String trackNo;

    /**
     * 零部件名称
     */
    @ApiModelProperty(value = "零部件名称", dataType = "String")
    private String partName;

    /**
     * 零部件材料
     */
    @ApiModelProperty(value = "零部件材料", dataType = "String")
    private String partMaterials;

    /**
     * 零部件图号
     */
    @ApiModelProperty(value = "零部件图号", dataType = "String")
    private String partDrawingNo;

    /**
     * 送出的车间
     */
    @ApiModelProperty(value = "送出的车间", dataType = "String")
    private String missiveBranch;


    /**
     * 责任单位内
     */
    @ApiModelProperty(value = "责任单位内", dataType = "String")
    private String unitResponsibilityWithin;

    /**
     * 责任单位外
     */
    @ApiModelProperty(value = "责任单位外", dataType = "String")
    private String unitResponsibilityOutside;

    /**
     * 处理单位1
     */
    @ApiModelProperty(value = "处理单位1", dataType = "String")
    private String unitTreatmentOne;

    /**
     * 处理单位2
     */
    @ApiModelProperty(value = "处理单位2", dataType = "String")
    private String unitTreatmentTwo;

    /**
     * 工序名称
     */
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String itemName;

    /**
     * 工序类型
     */
    @ApiModelProperty(value = "工序类型", dataType = "String")
    private String itemType;

    /**
     * 不合格数量
     */
    @ApiModelProperty(value = "不合格数量", dataType = "String")
    private Integer disqualificationNum;

    /**
     * 质检完成时间
     */
    @ApiModelProperty(value = "质检完成时间", dataType = "String")
    private Date qualityCompleteTime;

    /**
     * 质控工程师姓名
     */
    @ApiModelProperty(value = "质量检查人name", dataType = "String")
    private String qualityCheckName;

    /**
     * 质控工程师
     */
    @ApiModelProperty(value = "质量检查userId", dataType = "String")
    private String qualityCheckBy;

    /**
     * 不合格情况
     */
    @ApiModelProperty(value = "不合格情况", dataType = "String")
    private String disqualificationCondition;

    /**
     * 开单时间
     */
    @ApiModelProperty(value = "开单时间", dataType = "Date")
    private Date orderTime;

    /**
     * 所属机构
     */
    @ApiModelProperty(value = "所属机构", dataType = "String")
    private String branchCode;

    /**
     * 所属租户
     */
    @ApiModelProperty(value = "所属租户", dataType = "Date")
    private String tenantId;

    @TableField(exist = false)
    @ApiModelProperty(value = "文件列表", dataType = "List<DisqualificationAttachment>")
    private List<DisqualificationAttachment> attachmentList;

    /**
     * 检验人员
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "检验人员", dataType = "List<DisqualificationUserOpinion>")
    private List<TenantUserVo> userList;

    /**
     * 不合格类型
     */
    @ApiModelProperty(value = "不合格类型集合", dataType = "List<String>")
    private List<String> typeList;

}
