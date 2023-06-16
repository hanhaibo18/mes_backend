package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.enums.MaterialTypeEnum;
import com.richfit.mes.common.model.enums.MessageEnum;
import com.richfit.mes.common.model.enums.ObjectTypeEnum;
import com.richfit.mes.common.model.enums.TrackTypeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * material_basis MES物料基础数据同步
 */
@Data
public class MaterialBasis implements Serializable {

    /**
     * 工厂 泵业/热工等
     */
    private String workCode;

    /**
     * 物料编码
     */
    private String materialNum;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 关键件 是/否
     */
    private String crucialFlag;

    /**
     * 跟踪方式 单件/批次
     */
    private String trackingMode;

    /**
     * 材质 为零件材料
     */
    private String partsMaterial;

    /**
     * 规格
     */
    private String spec;

    /**
     * 单重
     */
    private String singleWeight;

    /**
     * 实物配送 是/否
     */
    private String deliveryFlag;

    /**
     * 制造类型 外购、外协、自制
     */
    private String produceType;

    /**
     * 物料类型 毛坯、成品、半成品
     */
    private String materialType;

    /**
     * 预留字段1
     */
    private String field1;

    /**
     * 预留字段2
     */
    private String field2;

    /**
     * 预留字段3
     */
    private String field3;

    /**
     * 预留字段4
     */
    private String field4;

    /**
     * 预留字段5
     */
    private String field5;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public MaterialBasis() {

    }
    public MaterialBasis(Product product, String tenantErpCode) {
        //工厂
        this.workCode = tenantErpCode;
        //物料编码
        this.materialNum = product.getMaterialNo();
        //物料描述
        this.materialDesc = product.getMaterialDesc();
        //计量单位
        this.unit = product.getUnit();
        //关键件
        if (StringUtils.isNotEmpty(product.getIsKeyPart())) {
            this.crucialFlag = MessageEnum.getMessage(product.getIsKeyPart());
        }
        //跟踪方式
        if (StringUtils.isNotEmpty(product.getTrackType())) {
            this.trackingMode = TrackTypeEnum.getMessage(product.getTrackType());
        }
        this.trackingMode = product.getTrackType();
        //材质
        this.partsMaterial = product.getTexture();
        //规格
        this.spec = product.getSpecification();
        //单重
        this.singleWeight = String.valueOf(product.getWeight());
        //实物配送
        if (StringUtils.isNotEmpty(product.getObjectType())) {
            this.deliveryFlag = MessageEnum.getMessage(product.getObjectType());
        }
        //制造类型
        if (StringUtils.isNotEmpty(product.getObjectType())) {
            this.produceType = ObjectTypeEnum.getMessage(product.getObjectType());
        }
        //物料类型
        if (StringUtils.isNotEmpty(product.getObjectType())) {
            this.materialType = MaterialTypeEnum.getMessage(product.getMaterialType());
        }
        //备注
        this.field1 = product.getRemark();
    }
}
