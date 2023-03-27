package com.richfit.mes.produce.entity.quality;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.produce.DisqualificationAttachment;
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

    private static final long serialVersionUID = 2911298524898169849L;
    /**
     * 跟单Id
     */
    @ApiModelProperty(value = "跟单Id")
    private String trackHeadId;

    /**
     * 跟单类型
     */
    @ApiModelProperty(value = "跟单类型")
    private Integer trackHeadType;

    /**
     * 炉号
     */
    @ApiModelProperty(value = "炉号")
    private String heatNumber;

    /**
     * 跟单Id
     */
    @ApiModelProperty(value = "跟单Id")
    private Integer number;

    /**
     * 跟单工序Id
     */
    @ApiModelProperty(value = "跟单工序ID")
    private String trackItemId;

    /**
     * 申请单编号
     */
    @ApiModelProperty(value = "申请单编号")
    private String processSheetNo;

    /**
     * 不合格类型
     */
    @ApiModelProperty(value = "不合格类型")
    private Integer type;

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
     * 责任单位内
     */
    @ApiModelProperty(value = "责任单位内")
    private String unitResponsibilityWithin;

    /**
     * 责任单位外
     */
    @ApiModelProperty(value = "责任单位外")
    private String unitResponsibilityOutside;

    /**
     * 处理单位1
     */
    @ApiModelProperty(value = "处理单位1")
    private String unitTreatmentOne;

    /**
     * 处理单位2
     */
    @ApiModelProperty(value = "处理单位2")
    private String unitTreatmentTwo;

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

//    /**
//     * 质控工程师
//     */
//    @ApiModelProperty(value = "质控工程师")
//    private String qualityCheckBy;

    /**
     * 开单时间
     */
    private Date orderTime;

    /**
     * 发现车间
     */
    @ApiModelProperty(value = "发现车间")
    private String discoverBranch;

    /**
     * 发现工序
     */
    @ApiModelProperty(value = "发现工序")
    private String discoverItem;

    /**
     * 总重量
     */
    @ApiModelProperty(value = "总重量")
    private Double totalWeight;

    /**
     * 废品损失
     */
    @ApiModelProperty(value = "废品损失")
    private String abandonmentLoss;

    /**
     * 废品工时
     */
    @ApiModelProperty(value = "废品工时")
    private Double discardTime;

    /**
     * 回用工时
     */
    @ApiModelProperty(value = "回用工时")
    private Double reuseTime;

    /**
     * 让步接收数量
     */
    @ApiModelProperty(value = "让步接收数量")
    private Integer acceptDeviation;

    /**
     * 让步接收损失
     */
    @ApiModelProperty(value = "让步接收损失")
    private String acceptDeviationLoss;

    /**
     * 让步接收产品编号
     */
    @ApiModelProperty(value = "让步接收产品编号")
    private List<String> acceptDeviationNoList;

    /**
     * 返修数量
     */
    @ApiModelProperty(value = "返修数量")
    private Integer repairQuantity;

    /**
     * 返修损失
     */
    @ApiModelProperty(value = "返修损失")
    private String repairLoss;

    /**
     * 返修后产品编号
     */
    @ApiModelProperty(value = "返修后产品编号")
    private List<String> repairNoList;

    /**
     * 返修结果
     */
    @ApiModelProperty(value = "返修结果")
    private String recapDemerits;

    /**
     * 返修描述
     */
    @ApiModelProperty(value = "返修描述")
    private String recapDescribe;

    /**
     * 返修检验员
     */
    @ApiModelProperty(value = "返修检验员")
    private String recapUser;

    /**
     * 返修时间
     */
    @ApiModelProperty(value = "返修时间")
    private Date recapTime;

    /**
     * 返修合格数量
     */
    @ApiModelProperty(value = "返修合格数量")
    private Integer repairQualified;

    /**
     * 返修合格损失
     */
    @ApiModelProperty(value = "返修合格损失")
    private String repairQualifiedLoss;

    /**
     * 返修合格产品编号
     */
    @ApiModelProperty(value = "返修合格产品编号")
    private List<String> repairQualifiedNoList;

    /**
     * 返修不合格数量
     */
    @ApiModelProperty(value = "返修不合格数量")
    private Integer repairNotQualified;

    /**
     * 返修不合格损失
     */
    @ApiModelProperty(value = "返修不合格损失")
    private String repairNotQualifiedLoss;

    /**
     * 返修不合格产品编号
     */
    @ApiModelProperty(value = "返修不合格产品编号")
    private List<String> repairNotQualifiedNoList;

    /**
     * 报废数量
     */
    @ApiModelProperty(value = "报废数量")
    private Integer scrap;

    /**
     * 报废损失
     */
    @ApiModelProperty(value = "报废损失")
    private String scrapLoss;

    /**
     * 报废后产品编号
     */
    @ApiModelProperty(value = "报废后产品编号")
    private List<String> scrapNoList;

    /**
     * 退货数量
     */
    @ApiModelProperty(value = "退货数量")
    private Integer salesReturn;

    /**
     * 退货损失
     */
    @ApiModelProperty(value = "退货损失")
    private String salesReturnLoss;

    /**
     * 退货产品编号
     */
    @ApiModelProperty(value = "退货产品编号")
    private List<String> salesReturnNoList;


    /**
     * 发现车间
     */
    @ApiModelProperty(value = "发现车间")
    private String discoverTenant;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
    private String tenantId;

    @ApiModelProperty(value = "是否责任裁决")
    private Integer isResponsibility = 0;
    @ApiModelProperty(value = "是否技术裁决")
    private Integer isTechnology = 0;

    @ApiModelProperty(value = "来源状态 1=有来源 0=无来源")
    private Integer sourceType;

    /**
     * 不合格情况
     */
    @ApiModelProperty(value = "不合格情况")
    private String disqualificationCondition;
    @ApiModelProperty(value = "质控意见")
    private String qualityControlOpinion;
    @ApiModelProperty(value = "处理单位1")
    private String unitTreatmentOneOpinion;
    @ApiModelProperty(value = "处理单位2")
    private String unitTreatmentTwoOpinion;
    @ApiModelProperty(value = "责任裁决")
    private String responsibilityOpinion;
    @ApiModelProperty(value = "技术裁决")
    private String technologyOpinion;

    @ApiModelProperty(value = "是否提交")
    private Integer isSubmit;


    @TableField(exist = false)
    @ApiModelProperty(value = "文件列表", dataType = "List<DisqualificationAttachment>")
    private List<DisqualificationAttachment> attachmentList;

    /**
     * 检验人员
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "检验人员", dataType = "List<String>")
    private List<String> userList;

    /**
     * 不合格类型
     */
    @ApiModelProperty(value = "不合格类型集合", dataType = "List<String>")
    private List<String> typeList;

}
