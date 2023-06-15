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
import java.util.List;
import java.util.UUID;

/**
 * 
 * apply_line_list 申请单上传 行数据
 */
@Data
public class ApplyLineList implements Serializable {
    /**
     * MES申请单ID
     */
    private String applyId;

    /**
     * MES申请单行id
     */
    private String id;

    /**
     * MES申请单行项目
     */
    private Integer lineNum;

    /**
     * 产品图号
     */
    private String drawingNo;

    /**
     * 物料编码
     */
    private String materialNum;

    /**
     * 物料名称
     */
    private String materialDesc;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 申请单数量
     */
    private Integer quantity;

    /**
     *  物料类型
     */
    private String materialType;

    /**
     *  跟踪方式
     */
    private String trackingMode;

    /**
     * 关键件
     */
    private String crucialFlag;

    /**
     * 行数据
     */
    @TableField(exist = false)
    private List<ApplyLineProductList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public ApplyLineList() {

    }

    public ApplyLineList(Certificate certificate, Product product, List<TrackFlow> trackFlows ) {
        // 申请单id
        this.applyId = UUID.randomUUID().toString().replaceAll("-", "");
        // id
        this.id = certificate.getId();
        // 行项目
        this.lineNum = 1;
        // 图号
        this.drawingNo = certificate.getDrawingNo();
        // 物料号
        this.materialNum = certificate.getMaterialNo();
        // 物料名称
        this.materialDesc = certificate.getMaterialName();
        // 单位
        this.unit = product.getUnit();
        // 产品数量
        this.quantity = certificate.getNumber();
        // 物料类型
        if (StringUtils.isNotEmpty(product.getMaterialType())) {
            this.materialType = MaterialTypeEnum.getMessage(product.getMaterialType());
        }
        // 跟踪方式
        if (StringUtils.isNotEmpty(product.getTrackType())) {
            this.trackingMode = TrackTypeEnum.getMessage(product.getTrackType());
        }
        // 关键件
        if (StringUtils.isNotEmpty(product.getIsKeyPart())) {
            this.crucialFlag = MessageEnum.getMessage(product.getIsKeyPart());
        }
        this.lineList = new ArrayList<>(trackFlows.size());
        for (TrackFlow trackFlow : trackFlows) {
            ApplyLineProductList applyLineProductList = new ApplyLineProductList(certificate, trackFlow);
            lineList.add(applyLineProductList);
        }

    }
}