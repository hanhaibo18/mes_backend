package com.richfit.mes.produce.entity.quality;

import com.richfit.mes.common.model.produce.DisqualificationAttachment;
import com.richfit.mes.common.model.produce.DisqualificationFinalResult;
import com.richfit.mes.common.model.produce.DisqualificationUserOpinion;
import com.richfit.mes.common.model.produce.TrackHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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
     * 发现车间
     */
    @ApiModelProperty(value = "发现车间")
    private String discoverTenant;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private int number;


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
    private String type = "1";

    /**
     * 不合格类型
     */
    @ApiModelProperty(value = "不合格类型")
    private String disqualificationType;

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
    private List<String> acceptDeviationNoList;

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
    @ApiModelProperty(value = "返修描述")
    private String recapUser;

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

    private String branchCode;

    private String tenantId;

    @ApiModelProperty(value = "不合格意见意见")
    private String unqualifiedOpinion;
    @ApiModelProperty(value = "质控意见")
    private String qualityControlOpinion;
    @ApiModelProperty(value = "质控姓名")
    private String qualityName;
    @ApiModelProperty(value = "质控时间")
    private Date qualityTime;

    @ApiModelProperty(value = "处理单位1意见")
    private String unitTreatmentOneOpinion;
    @ApiModelProperty(value = "处理单位1姓名")
    private String treatmentOneName;
    @ApiModelProperty(value = "处理单位1时间")
    private Date treatmentOneTime;

    @ApiModelProperty(value = "处理单位2意见")
    private String unitTreatmentTwoOpinion;
    @ApiModelProperty(value = "处理单位2姓名")
    private String treatmentTwoName;
    @ApiModelProperty(value = "处理单位2时间")
    private Date treatmentTwoTime;

    @ApiModelProperty(value = "责任裁决意见")
    private String responsibilityOpinion;
    @ApiModelProperty(value = "责任裁决姓名")
    private String responsibilityName;
    @ApiModelProperty(value = "责任裁决时间")
    private Date responsibilityTime;

    @ApiModelProperty(value = "技术裁决意见")
    private String technologyOpinion;
    @ApiModelProperty(value = "技术裁决姓名")
    private String technologyName;
    @ApiModelProperty(value = "技术裁决时间")
    private Date technologyTime;

    @ApiModelProperty(value = "不合格情况")
    private String disqualificationCondition;
    @ApiModelProperty(value = "不合格姓名")
    private String disqualificationName;
    @ApiModelProperty(value = "不合格时间")
    private Date disqualificationTime;
    /**
     * 质控工程师显示
     */
    @ApiModelProperty(value = "质控工程师显示")
    private String checkShow;

    @ApiModelProperty(value = "来源状态 1=有来源 0=无来源")
    private Integer sourceType;

    /**
     * 退货产品编号
     */
    @ApiModelProperty(value = "退货产品编号")
    private List<String> salesReturnNoList;

    @ApiModelProperty(value = "审核意见列表", dataType = "List<DisqualificationUserOpinion>")
    private List<DisqualificationUserOpinion> userOpinionsList;

    @ApiModelProperty(value = "文件列表")
    private List<DisqualificationAttachment> attachmentList;

    /**
     * 不合格类型
     */
    @ApiModelProperty(value = "不合格类型集合", dataType = "List<String>")
    private List<String> typeList = new ArrayList<>();


    @ApiModelProperty(value = "用户列表")
    private List<String> userList;

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
        //数量
        this.setNumber(trackHead.getNumber());
        //不合格品数量
//        this.setDisqualificationNum(trackItem.getQualityUnqty());
        //车间类型
//        this.setClasses(trackHead.getClasses());
        //工作号
        this.setWorkNo(trackHead.getWorkNo());
        //跟单Id
        this.setTrackHeadId(trackHead.getId());
    }

    public void DisqualificationFinalResult(DisqualificationFinalResult finalResult) {
        //发现车间
        this.discoverBranch = finalResult.getDiscoverBranch();
        //发现工序
        this.discoverItem = finalResult.getDiscoverItem();
        //总重量
        this.totalWeight = finalResult.getTotalWeight();
        //废品损失
        this.abandonmentLoss = finalResult.getAbandonmentLoss();
        //废品工时
        this.discardTime = finalResult.getDiscardTime();
        //回用工时
        this.reuseTime = finalResult.getReuseTime();
        //让步接收数量
        this.acceptDeviation = finalResult.getAcceptDeviation();
        //让步接收损失
        this.acceptDeviationLoss = finalResult.getAcceptDeviationLoss();
        //让步接收损失编号
        if (finalResult.getAcceptDeviationNo().contains(",")) {
            this.acceptDeviationNoList = Arrays.asList(finalResult.getAcceptDeviationNo().split(","));
        } else if (StringUtils.isNotBlank(finalResult.getAcceptDeviationNo())) {
            this.acceptDeviationNoList = new ArrayList<>(Collections.singletonList(finalResult.getAcceptDeviationNo()));
        }
        //返修合格数量
        this.repairQualified = finalResult.getRepairQualified();
        //返修损失
        this.repairLoss = finalResult.getRepairLoss();
        //返修后产品编号
        if (finalResult.getRepairNo().contains(",")) {
            this.repairNoList = Arrays.asList(finalResult.getRepairNo().split(","));
        } else if (StringUtils.isNotBlank(finalResult.getRepairNo())) {
            this.repairNoList = new ArrayList<>(Collections.singletonList(finalResult.getRepairNo()));
        }
        //返修后结果
        this.recapDemerits = finalResult.getRecapDemerits();
        //返修描述
        this.recapDescribe = finalResult.getRecapDescribe();
        //返修检验员
        this.recapUser = finalResult.getRecapUser();
        //返修时间
        this.recapTime = finalResult.getRecapTime();
        //报废数量
        this.scrap = finalResult.getScrap();
        //报废损失
        this.scrapLoss = finalResult.getScrapLoss();
        //报废后产品编号
        if (finalResult.getScrapNo().contains(",")) {
            this.scrapNoList = Arrays.asList(finalResult.getScrapNo().split(","));
        } else if (StringUtils.isNotBlank(finalResult.getScrapNo())) {
            this.scrapNoList = new ArrayList<>(Collections.singletonList(finalResult.getScrapNo()));
        }
        //退货数量
        this.salesReturn = finalResult.getSalesReturn();
        //退货损失
        this.salesReturnLoss = finalResult.getSalesReturnLoss();
        //退货产品编号
        if (finalResult.getSalesReturnNo().contains(",")) {
            this.salesReturnNoList = Arrays.asList(finalResult.getSalesReturnNo().split(","));
        } else if (StringUtils.isNotBlank(finalResult.getSalesReturnNo())) {
            this.salesReturnNoList = new ArrayList<>(Collections.singletonList(finalResult.getSalesReturnNo()));
        }
        //责任单位内
        this.unitResponsibilityWithin = finalResult.getUnitResponsibilityWithin();
        //责任单位外
        this.unitResponsibilityOutside = finalResult.getUnitResponsibilityOutside();
        //发现车间
        this.discoverTenant = finalResult.getDiscoverTenant();

        //质控意见
        this.qualityControlOpinion = finalResult.getQualityControlOpinion();
        //质控姓名
        this.qualityName = finalResult.getQualityName();
        //质控时间
        this.qualityTime = finalResult.getQualityTime();

        //处理单位1
        this.unitTreatmentOne = finalResult.getUnitTreatmentOne();
        //处理单位1意见
        this.unitTreatmentOneOpinion = finalResult.getUnitTreatmentOneOpinion();
        //处理单位1姓名
        this.treatmentOneName = finalResult.getTreatmentOneName();
        //处理单位1时间
        this.treatmentOneTime = finalResult.getTreatmentOneTime();

        //处理单位2
        this.unitTreatmentTwo = finalResult.getUnitTreatmentTwo();
        //处理单位2意见
        this.unitTreatmentTwoOpinion = finalResult.getUnitTreatmentTwoOpinion();
        //处理单位2姓名
        this.treatmentTwoName = finalResult.getTreatmentTwoName();
        //处理单位2时间
        this.treatmentTwoTime = finalResult.getTreatmentTwoTime();

        //责任裁决意见
        this.responsibilityOpinion = finalResult.getResponsibilityOpinion();
        //责任裁决姓名
        this.responsibilityName = finalResult.getResponsibilityName();
        //责任裁决时间
        this.responsibilityTime = finalResult.getResponsibilityTime();

        //技术裁决意见
        this.technologyOpinion = finalResult.getTechnologyOpinion();
        //技术裁决姓名
        this.technologyName = finalResult.getTechnologyName();
        //技术裁决时间
        this.technologyTime = finalResult.getTechnologyTime();

        //不合格情况(意见)
        this.disqualificationCondition = finalResult.getDisqualificationCondition();
        //不合格姓名
        this.disqualificationName = finalResult.getDisqualificationName();
        //不合格时间
        this.disqualificationTime = finalResult.getDisqualificationTime();
    }
}
