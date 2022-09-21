package com.richfit.mes.common.model.produce;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.base.RouterCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 质量检测卡内容列表信息类，并包含很多封装类的方法
 *
 * @author zhiqiang.lu
 * @date 2022.9.21
 */
@Data
@Accessors(chain = true)
@TableName("produce_inspection_record_card_content")
@ApiModel(value = "质量检验记录卡质检内容")
public class ProduceInspectionRecordCardContent extends BaseEntity<ProduceInspectionRecordCardContent> {

    private static final long serialVersionUID = -1044825101675722165L;

    /**
     * 质检类型（普通）
     */
    public static final String PRODUCEINSPECTIONRECORDCARDCONTENT_TYPE_PT = "1";

    /**
     * 跟单类型（工序合格证）
     */
    public static final String PRODUCEINSPECTIONRECORDCARDCONTENT_TYPE_GXHGZ = "2";

    /**
     * 跟单类型（探伤）
     */
    public static final String PRODUCEINSPECTIONRECORDCARDCONTENT_TYPE_TS = "3";

    /**
     * 跟单类型（炉号）
     */
    public static final String PRODUCEINSPECTIONRECORDCARDCONTENT_TYPE_CLZS = "4";

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "组织机构编号")
    private String branchCode;

    @ApiModelProperty(value = "生产线id")
    private String flowId;

    @ApiModelProperty(value = "序号")
    private String inspectionNo;

    @ApiModelProperty(value = "项目名称")
    private String inspectionName;

    @ApiModelProperty(value = "工序名称")
    private String inspectionItemNo;

    @ApiModelProperty(value = "工序名称")
    private String inspectionItemName;

    @ApiModelProperty(value = "检查内容")
    private String inspectionContent;

    @ApiModelProperty(value = "技术要求")
    private String inspectionRequirement;

    @ApiModelProperty(value = "量具或者检测方法")
    private String inspectionTesting;

    @ApiModelProperty(value = "检测结果")
    private String inspectionResult;

    @ApiModelProperty(value = "合格 0不合格、1合格")
    private String inspectionQualified;

    @TableField(exist = false)
    @ApiModelProperty(value = "合格状态文字描述")
    private String inspectionQualifiedDexc;

    @ApiModelProperty(value = "检验员名称")
    private String inspectionUserName;

    @ApiModelProperty(value = "检验日期")
    private String inspectionDate;

    @ApiModelProperty(value = "类型：1普通 2工序合格证 3探伤记录 4材料追溯（炉号）")
    private String type;

    /**
     * 合格状态文字处理
     *
     * @author zhiqiang.lu
     * @date 2022.9.21
     */
    public static String qualified(String inspectionQualified) {
        switch (inspectionQualified) {
            case "0":
                return "不合格";
            case "1":
                return "合格";
        }
        return inspectionQualified;
    }


    /**
     * 质检信息列表信息封装类
     *
     * @author zhiqiang.lu
     * @date 2022.9.21
     */
    public static ProduceInspectionRecordCardContent packageInfo(TrackItem trackItem, TrackCheckDetail trackCheckDetail) {
        ProduceInspectionRecordCardContent pt = new ProduceInspectionRecordCardContent();
        RouterCheck routerCheck = trackCheckDetail.getRouterCheck();
        String requirement = "";
        if (routerCheck != null) {
            if (!StrUtil.isBlank(routerCheck.getPropertyUplimit())) {
                requirement += "最大值" + routerCheck.getPropertyUplimit() + ";";
            }
            if (!StrUtil.isBlank(routerCheck.getPropertyLowerlimit())) {
                requirement += "最小值" + routerCheck.getPropertyLowerlimit() + ";";
            }
            if (!StrUtil.isBlank(routerCheck.getPropertyDefaultvalue())) {
                requirement += "默认值" + routerCheck.getPropertyDefaultvalue() + ";";
            }
        }
        pt.setId(trackCheckDetail.getId());
        pt.setTenantId(trackItem.getTenantId());
        pt.setBranchCode(trackItem.getBranchCode());
        pt.setFlowId(trackItem.getFlowId());
        pt.setInspectionItemNo(trackItem.getOptNo());
        pt.setInspectionItemName(trackItem.getOptName());
        pt.setInspectionContent(trackCheckDetail.getCheckName());
        pt.setInspectionRequirement(requirement);
        pt.setInspectionTesting(trackCheckDetail.getCheckMethod());
        pt.setInspectionResult(trackCheckDetail.getValue());
        pt.setInspectionQualified(trackCheckDetail.getResult() + "");
        pt.setInspectionQualifiedDexc(ProduceInspectionRecordCardContent.qualified(pt.getInspectionQualified()));
        pt.setInspectionDate(DateUtil.format(trackCheckDetail.getCreateTime(), "yyyy/MM/dd"));
        pt.setRemark(trackCheckDetail.getRemark());
        //处理人签字
        //pt.setInspectionUserName(trackCheck.getDealBy());
        //系统记录修改人
        pt.setInspectionUserName(trackCheckDetail.getModifyBy());
        pt.setType(PRODUCEINSPECTIONRECORDCARDCONTENT_TYPE_PT);
        return pt;
    }

    public static List<ProduceInspectionRecordCardContent> listByTrackItem(TrackItem trackItem, List<TrackCheck> trackCheckList) {
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = new ArrayList<>();
        for (TrackCheck trackCheck : trackCheckList) {
            for (TrackCheckDetail trackCheckDetail : trackCheck.getCheckDetailsList()) {
                //当工序的id等于质检数据中的工序id时
                if (trackItem.getId().equals(trackCheckDetail.getTiId())) {
                    produceInspectionRecordCardContentList.add(packageInfo(trackItem, trackCheckDetail));
                }
            }
        }

        //工序合格证
        if (!StrUtil.hasBlank(trackItem.getCertificateNo())) {
            ProduceInspectionRecordCardContent gxhgz = new ProduceInspectionRecordCardContent();
            gxhgz.setId(trackItem.getId());
            gxhgz.setTenantId(trackItem.getTenantId());
            gxhgz.setBranchCode(trackItem.getBranchCode());
            gxhgz.setFlowId(trackItem.getFlowId());
            gxhgz.setInspectionItemName(trackItem.getOptName());
            gxhgz.setInspectionContent("调质处理");
            gxhgz.setInspectionRequirement("记录工序合格证号");
            gxhgz.setInspectionTesting("/");
            gxhgz.setInspectionResult(trackItem.getCertificateNo());
            gxhgz.setType(PRODUCEINSPECTIONRECORDCARDCONTENT_TYPE_GXHGZ);
            gxhgz.setInspectionQualified(null);
            produceInspectionRecordCardContentList.add(gxhgz);
        }

        //探伤
        if (!StrUtil.hasBlank(trackItem.getInspectRecordNo())) {
            ProduceInspectionRecordCardContent ts = new ProduceInspectionRecordCardContent();
            ts.setId(trackItem.getId());
            ts.setTenantId(trackItem.getTenantId());
            ts.setBranchCode(trackItem.getBranchCode());
            ts.setFlowId(trackItem.getFlowId());
            ts.setInspectionItemName(trackItem.getOptName());
            ts.setInspectionContent("探伤");
            ts.setInspectionRequirement("探伤报告单号");
            ts.setInspectionTesting("/");
            ts.setInspectionResult(trackItem.getInspectRecordNo());
            ts.setType(PRODUCEINSPECTIONRECORDCARDCONTENT_TYPE_TS);
            ts.setInspectionQualified(null);
            produceInspectionRecordCardContentList.add(ts);
        }
        return produceInspectionRecordCardContentList;
    }

    public static List<ProduceInspectionRecordCardContent> listByTrackHead(ProduceInspectionRecordCard produceInspectionRecordCard) {
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = new ArrayList<>();
        //材料追溯（炉号）
        if (!StrUtil.hasBlank(produceInspectionRecordCard.getBatchNo())) {
            ProduceInspectionRecordCardContent clzs = new ProduceInspectionRecordCardContent();
            clzs.setId(produceInspectionRecordCard.getId());
            clzs.setTenantId(produceInspectionRecordCard.getTenantId());
            clzs.setBranchCode(produceInspectionRecordCard.getBranchCode());
            clzs.setFlowId(produceInspectionRecordCard.getId());
            clzs.setInspectionContent("材料追溯");
            clzs.setInspectionRequirement("记录材料炉号");
            clzs.setInspectionTesting("/");
            clzs.setInspectionResult(produceInspectionRecordCard.getBatchNo());
            clzs.setType(PRODUCEINSPECTIONRECORDCARDCONTENT_TYPE_CLZS);
            clzs.setInspectionQualified(null);
            produceInspectionRecordCardContentList.add(clzs);
        }
        return produceInspectionRecordCardContentList;
    }
}
