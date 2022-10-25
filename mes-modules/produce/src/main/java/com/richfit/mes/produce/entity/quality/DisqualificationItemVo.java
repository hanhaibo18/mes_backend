package com.richfit.mes.produce.entity.quality;

import com.richfit.mes.common.model.produce.DisqualificationAttachment;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 工作号
     */
    @ApiModelProperty(value = "工作号", dataType = "String")
    private String workNo;
    /**
     * 申请单编号
     */
    @ApiModelProperty(value = "申请单编号", dataType = "String")
    private String processSheetNo;
    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称", dataType = "String")
    private String productName;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;


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

    @ApiModelProperty(value = "送出的车间", dataType = "String")
    private String missiveBranch;

    /**
     * 跟单号
     */
    @ApiModelProperty(value = "跟单号", dataType = "String")
    private String trackNo;

    /**
     * 不合格数量
     */
    @ApiModelProperty(value = "不合格数量", dataType = "String")
    private Integer disqualificationNum;

    /**
     * 不合格情况
     */
    @ApiModelProperty(value = "不合格情况", dataType = "String")
    private String disqualificationCondition;

    private String classes;

    @ApiModelProperty(value = "处理单状态")
    private Integer isIssue;

    @ApiModelProperty(value = "工序名称")
    private String itemName;

    @ApiModelProperty(value = "工序类型")
    private String itemType;

    @ApiModelProperty(value = "车间")
    private String branchCode;
    @ApiModelProperty(value = "租户")
    private String tenantId;

    @ApiModelProperty(value = "审核意见列表")
    private List<SignedRecordsVo> signedRecordsList;
    @ApiModelProperty(value = "文件列表")
    private List<DisqualificationAttachment> attachmentList;
    @ApiModelProperty(value = "用户列表")
    private List<TenantUserVo> tenantUserList;

    public void trackHead(TrackHead trackHead) {
        //跟单号
        this.setTrackNo(trackHead.getTrackNo());
        //产品名称
//        item.setProductName(trackItem.getProductName());
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
        this.setClasses(trackHead.getClasses());
        //工作号
        this.setWorkNo(trackHead.getWorkNo());
    }
}
