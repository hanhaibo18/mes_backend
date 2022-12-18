package com.richfit.mes.produce.entity.quality;

import com.richfit.mes.common.model.produce.DisqualificationAttachment;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: DisqualificationItemVO.java
 * @Author: Hou XinYu
 * @Description: 不合格品工序参数
 * @CreateTime: 2022年10月13日 14:48:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisqualificationItemVo {

    @ApiModelProperty(value = "不合格品主键Id")
    private String id;
    /**
     * 跟单Id
     */
    @ApiModelProperty(value = "发现车间")
    private String trackHeadId;

    /**
     * 跟单工序Id
     */
    @ApiModelProperty(value = "发现车间")
    private String trackItemId;

    /**
     * 申请单编号
     */
    @ApiModelProperty(value = "发现车间")
    private String processSheetNo;

    /**
     * 不合格类型 1=开具处理单 2=质控评审 3=处理单位1评审 4=处理单位2评审 5=责任裁决 6=技术裁决
     */
    @ApiModelProperty(value = "不合格类型")
    private String type;

    /**
     * 0 = 未发布 1= 已发布 2 = 已关闭
     */
    @ApiModelProperty(value = "是否发布")
    private Integer isIssue;

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
    @ApiModelProperty(value = "送出的车间")
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
     * 不合格情况
     */
    @ApiModelProperty(value = "不合格情况")
    private String disqualificationCondition;

    /**
     * 开单时间
     */
    @ApiModelProperty(value = "开单时间")
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
    private String acceptDeviationNo;

    /**
     * 返修合格数量
     */
    @ApiModelProperty(value = "返修合格数量")
    private Integer repairQualified;

    /**
     * 返修损失
     */
    @ApiModelProperty(value = "返修损失")
    private String repairLoss;

    /**
     * 返修后产品编号
     */
    @ApiModelProperty(value = "返修后产品编号")
    private String repairNo;

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
     * 返修时间
     */
    @ApiModelProperty(value = "返修时间")
    private Date recapTime;

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
    private String scrapNo;

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

    private String branchCode;

    private String tenantId;

    @ApiModelProperty(value = "不合格意见JSON")
    private String unqualifiedOpinion;
    @ApiModelProperty(value = "质控意见JSON")
    private String qualityControlOpinion;
    @ApiModelProperty(value = "处理单位1JSON")
    private String unitTreatmentOneOpinion;
    @ApiModelProperty(value = "处理单位2JSON")
    private String unitTreatmentTwoOpinion;
    @ApiModelProperty(value = "责任裁决JSON")
    private String responsibilityOpinion;
    @ApiModelProperty(value = "技术JSON")
    private String technologyOpinion;

    /**
     * 退货产品编号
     */
    @ApiModelProperty(value = "退货产品编号")
    private String salesReturnNo;

    @ApiModelProperty(value = "审核意见列表", dataType = "List<SignedRecordsVo>")
    private List<SignedRecordsVo> signedRecordsList;

    @ApiModelProperty(value = "文件列表")
    private List<DisqualificationAttachment> attachmentList;

    /**
     * 不合格类型
     */
    @ApiModelProperty(value = "不合格类型集合", dataType = "List<String>")
    private List<String> typeList;


    @ApiModelProperty(value = "用户列表")
    private List<TenantUserVo> userList;

    public void trackHead(TrackHead trackHead) {
        //跟单号
        this.setTrackNo(trackHead.getTrackNo());
        //产品名称
        this.setProductName(trackHead.getProductName());
        //产品编号
        this.setProductNo(trackHead.getProductNo());
        //零部件名称
        this.setPartName(trackHead.getMaterialName());
        //零部件材料
        this.setPartMaterials(trackHead.getTexture());
        //零部件图号
        this.setPartDrawingNo(trackHead.getDrawingNo());
        //不合格品数量
//        this.setDisqualificationNum(trackItem.getQualityUnqty());
        //车间类型
//        this.setClasses(trackHead.getClasses());
        //工作号
        this.setWorkNo(trackHead.getWorkNo());
        //跟单Id
        this.setTrackHeadId(trackHead.getId());
    }
}
