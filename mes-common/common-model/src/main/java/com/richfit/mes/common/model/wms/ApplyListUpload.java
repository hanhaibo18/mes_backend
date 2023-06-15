package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.enums.MaterialTypeEnum;
import com.richfit.mes.common.model.enums.MessageEnum;
import com.richfit.mes.common.model.enums.TrackTypeEnum;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackFlow;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * apply_list_upload MES申请单上传WMS（已上线）
 */
@Data
public class ApplyListUpload implements Serializable {

    /**
     * 热工
     */
    public static final String TENANT_ID = "12345678901234567890123456789001";
    /**
     * 单据类型：自动入库
     */
    public static final String AUTO_TYPE = "自动类型";
    /**
     * 单据类型：正常
     */
    public static final String NORMAL_TYPE = "正常";

    /**
     * MES申请单ID 唯一
     */
    private String id;

    /**
     * MES申请单号
     */
    private String applyNum;

    /**
     * 数据类型
     */
    private String transType;

    /**
     * 工厂
     */
    private String workCode;

    /**
     * 车间  通过车间指向成品仓库
     */
    private String workshop;

    /**
     * 库存地点
     */
    private String invCode;

    /**
     * 工作号
     */
    private String jobNo;

    /**
     * 生产订单
     */
    private String prodNum;

    /**
     * 合格证 结构化
     */
    private String certificate;

    /**
     * 炉批号
     */
    private String batchNo;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 行数据 明细列表
     */
    @TableField(exist = false)
    private List<ApplyLineList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public ApplyListUpload() {

    }

    public ApplyListUpload(Certificate certificate) {

    }

    /**
     * @param certificate   合格证
     * @param product       物料
     * @param trackFlows    跟单分流list
     * @param tenantErpCode 租户erp标识
     */
    public ApplyListUpload(Certificate certificate, Product product, List<TrackFlow> trackFlows, String tenantErpCode) {
        this.id = certificate.getId();
        // MES申请单号
        this.applyNum = UUID.randomUUID().toString().replaceAll("-", "");
        if (certificate.getTenantId().equals(TENANT_ID)) {
            // 单据类型
            this.transType = NORMAL_TYPE;
        } else {
            this.transType = AUTO_TYPE;
        }
        // 工厂
        this.workCode = tenantErpCode;
        // 车间
        this.workshop = certificate.getBranchCode();
        // 库存地点 待定
        this.invCode = null;
        // 工作号
        this.jobNo = certificate.getWorkNo();
        // 生产订单
        this.prodNum = certificate.getProductionOrder();
        // 合格证
        this.certificate = certificate.getCertificateNo();
        // 炉批号
        this.batchNo = certificate.getBatchNo();
        // 创建人
        this.createBy = certificate.getCreateBy();
        // 创建日期
        this.createTime = certificate.getCreateTime();
        // 明细列表
        this.lineList = new ArrayList<>(1);

        ApplyLineList applyLineList = new ApplyLineList();
        // 申请单id
        applyLineList.setApplyId(UUID.randomUUID().toString().replaceAll("-", ""));
        // id
        applyLineList.setId(this.id);
        // 行项目
        applyLineList.setLineNum(1);
        // 图号
        applyLineList.setDrawingNo(certificate.getDrawingNo());
        // 物料号
        applyLineList.setMaterialNum(certificate.getMaterialNo());
        // 物料名称
        applyLineList.setMaterialDesc(certificate.getMaterialName());
        // 单位
        applyLineList.setUnit(product.getUnit());
        // 产品编号
        applyLineList.setQuantity(certificate.getNumber());
        // 物料类型
        if (StringUtils.isNotEmpty(product.getMaterialType())) {
            applyLineList.setMaterialType(MaterialTypeEnum.getMessage(product.getMaterialType()));
        }
        // 关键件
        if (StringUtils.isNotEmpty(product.getIsKeyPart())) {
            applyLineList.setCrucialFlag(MessageEnum.getMessage(product.getIsKeyPart()));
        }
        // 跟踪方式
        if (StringUtils.isNotEmpty(product.getTrackType())) {
            applyLineList.setTrackingMode(TrackTypeEnum.getMessage(product.getTrackType()));
        }
        // 物料名称
        applyLineList.setMaterialDesc(product.getProductName());
        List<ApplyLineProductList> list = new ArrayList<>(trackFlows.size());
        for (TrackFlow trackFlow : trackFlows) {
            ApplyLineProductList applyLineProductList = new ApplyLineProductList();
            // 申请单行id
            applyLineProductList.setApplyLineId(certificate.getId());
            // 产品编号
            applyLineProductList.setProductNum(trackFlow.getProductNo());
            // 数量
            applyLineProductList.setQuantity(trackFlow.getNumber());
            list.add(applyLineProductList);
        }
        applyLineList.setLineList(list);
        lineList.add(applyLineList);
    }

    /**
     * @param certificate   合格证
     * @param products      物料list
     * @param trackFlows    跟单分流list
     * @param tenantErpCode 租户erp标识
     */
    public ApplyListUpload(Certificate certificate, List<Product> products, List<TrackFlow> trackFlows, String tenantErpCode) {
        this.id = certificate.getId();
        // MES申请单号
        this.applyNum = UUID.randomUUID().toString().replaceAll("-", "");
        if (certificate.getTenantId().equals(TENANT_ID)) {
            // 单据类型
            this.transType = AUTO_TYPE;
        } else {
            this.transType = NORMAL_TYPE;
        }
        // 工厂
        this.workCode = tenantErpCode;
        // 车间
        this.workshop = certificate.getBranchCode();
        // 库存地点 待定
        this.invCode = null;
        // 工作号
        this.jobNo = certificate.getWorkNo();
        // 生产订单
        this.prodNum = certificate.getProductionOrder();
        // 合格证
        this.certificate = certificate.getCertificateNo();
        // 炉批号
        this.batchNo = certificate.getBatchNo();
        // 创建人
        this.createBy = certificate.getCreateBy();
        // 创建日期
        this.createTime = certificate.getCreateTime();
        // 明细列表
        this.lineList = new ArrayList<>(products.size());
        int i = 0;
        for (Product product : products) {
            //TODO  这个判断有社么意义吗？
            if (this.id.equals(certificate.getId())) {
                ApplyLineList applyLineList = new ApplyLineList();
                // 申请单id
                applyLineList.setApplyId(UUID.randomUUID().toString().replaceAll("-", ""));
                // id
                applyLineList.setId(this.id);
                // 行项目
                applyLineList.setLineNum(i++);
                // 图号
                applyLineList.setDrawingNo(certificate.getDrawingNo());
                // 物料号
                applyLineList.setMaterialNum(certificate.getMaterialNo());
                // 物料名称
                applyLineList.setMaterialDesc(certificate.getMaterialName());
                // 单位
                applyLineList.setUnit(product.getUnit());
                // 产品编号
                applyLineList.setQuantity(certificate.getNumber());
                // 物料类型
                if (StringUtils.isNotEmpty(product.getMaterialType())) {
                    applyLineList.setMaterialType(MaterialTypeEnum.getMessage(product.getMaterialType()));
                }
                // 关键件
                if (StringUtils.isNotEmpty(product.getIsKeyPart())) {
                    applyLineList.setCrucialFlag(MessageEnum.getMessage(product.getIsKeyPart()));
                }
                // 跟踪方式
                if (StringUtils.isNotEmpty(product.getTrackType())) {
                    applyLineList.setTrackingMode(TrackTypeEnum.getMessage(product.getTrackType()));
                }
                // 物料名称
                applyLineList.setMaterialDesc(product.getProductName());
                List<ApplyLineProductList> list = new ArrayList<>(trackFlows.size());
                for (TrackFlow trackFlow : trackFlows) {
                    //TODO  这个判断有社么意义吗？
                    if (applyLineList.getId().equals(certificate.getId())) {
                        ApplyLineProductList applyLineProductList = new ApplyLineProductList();
                        // 申请单行id
                        applyLineProductList.setApplyLineId(certificate.getId());
                        // 产品编号
                        applyLineProductList.setProductNum(trackFlow.getProductNo());
                        // 数量
                        applyLineProductList.setQuantity(trackFlow.getNumber());
                        list.add(applyLineProductList);
                    }
                }
                applyLineList.setLineList(list);
                lineList.add(applyLineList);
            }

        }
    }
}