package com.richfit.mes.common.model.produce;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
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
 * @author zhiqiang.lu
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
    private String inspectionItemName;

    @ApiModelProperty(value = "检查内容")
    private String inspectionContent;

    @ApiModelProperty(value = "技术要求")
    private String inspectionRequirement;

    @ApiModelProperty(value = "量具或者检测方法")
    private String inspectionTesting;

    @ApiModelProperty(value = "检测结果")
    private String inspectionResult;

    @ApiModelProperty(value = "合格 N不合格、Y合格")
    private String inspectionQualified;

    @ApiModelProperty(value = "检验员名称")
    private String inspectionUserName;

    @ApiModelProperty(value = "检验日期")
    private String inspectionDate;

    @ApiModelProperty(value = "类型：1普通 2工序合格证 3探伤记录 4材料追溯（炉号）")
    private String type;

    public static List<ProduceInspectionRecordCardContent> listByTrackItem(TrackItem trackItem, List<TrackCheck> trackCheckList) {
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = new ArrayList<>();

        for (TrackCheck trackCheck : trackCheckList) {
            for (TrackCheckDetail trackCheckDetail : trackCheck.getCheckDetailsList()) {
                if (trackItem.getId().equals(trackCheckDetail.getTiId())) {
                    RouterCheck routerCheck = trackCheckDetail.getRouterCheck();
                    String jsyq = "";
                    if (!StrUtil.isBlank(routerCheck.getPropertyUplimit())) {
                        jsyq += "最大值" + routerCheck.getPropertyUplimit() + ";";
                    }
                    if (!StrUtil.isBlank(routerCheck.getPropertyLowerlimit())) {
                        jsyq += "最小值" + routerCheck.getPropertyLowerlimit() + ";";
                    }
                    if (!StrUtil.isBlank(routerCheck.getPropertyDefaultvalue())) {
                        jsyq += "默认值" + routerCheck.getPropertyDefaultvalue() + ";";
                    }
                    ProduceInspectionRecordCardContent pt = new ProduceInspectionRecordCardContent();
                    pt.setId(trackCheckDetail.getId());
                    pt.setTenantId(trackItem.getTenantId());
                    pt.setBranchCode(trackItem.getBranchCode());
                    pt.setFlowId(trackItem.getFlowId());
                    pt.setInspectionItemName(trackItem.getOptName());
                    pt.setInspectionContent(trackCheckDetail.getCheckName());
                    pt.setInspectionRequirement(jsyq);
                    pt.setInspectionTesting(trackCheckDetail.getCheckMethod());
                    pt.setInspectionResult(trackCheckDetail.getValue());
                    pt.setInspectionQualified(trackCheckDetail.getResult() + "");
                    pt.setInspectionDate(DateUtil.format(trackCheckDetail.getCreateTime(), "yyyy/MM/dd"));
                    pt.setRemark(trackCheckDetail.getRemark());
                    pt.setInspectionUserName(trackCheck.getDealBy());
                    pt.setType(PRODUCEINSPECTIONRECORDCARDCONTENT_TYPE_PT);
                    produceInspectionRecordCardContentList.add(pt);
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
            produceInspectionRecordCardContentList.add(clzs);
        }
        return produceInspectionRecordCardContentList;
    }
}
