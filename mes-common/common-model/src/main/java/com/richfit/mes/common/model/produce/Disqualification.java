package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * produce_disqualification
 *
 * @author
 */
@Data
public class Disqualification extends BaseEntity<Disqualification> {

    /**
     * 跟单Id
     */
    
    private String trackHeadId;

    /**
     * 跟单工序Id
     */
    private String trackItemId;

    /**
     * 申请单编号
     */
    private String processSheetNo;

    /**
     * 不合格类型
     */
    private String type;

    /**
     * 0 = 未发布 1= 已发布 2 = 已关闭
     */
    private Integer isIssue;

    /**
     * 工作号
     */
    private String workNo;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品编号
     */
    private String productNo;

    /**
     * 跟单编号
     */
    private String trackNo;

    /**
     * 零部件名称
     */
    private String partName;

    /**
     * 零部件材料
     */
    private String partMaterials;

    /**
     * 零部件图号
     */
    private String partDrawingNo;

    /**
     * 送出的车间
     */
    private String missiveBranch;

    /**
     * 责任单位内
     */
    private String unitResponsibilityWithin;

    /**
     * 责任单位外
     */
    private String unitResponsibilityOutside;

    /**
     * 处理单位1
     */
    private String unitTreatmentOne;

    /**
     * 处理单位2
     */
    private String unitTreatmentTwo;

    /**
     * 工序名称
     */
    private String itemName;

    /**
     * 工序类型
     */
    private String itemType;

    /**
     * 不合格数量
     */
    private Integer disqualificationNum;

    /**
     * 质检完成时间
     */
    private Date qualityCompleteTime;

    /**
     * 质控工程师姓名
     */
    private String qualityCheckName;

    /**
     * 质控工程师
     */
    private String qualityCheckBy;

    /**
     * 不合格情况
     */
    private String disqualificationCondition;

    /**
     * 开单时间
     */
    private Date orderTime;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
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

}
