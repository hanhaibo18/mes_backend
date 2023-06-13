package com.richfit.mes.base.entity;


import com.richfit.mes.common.model.wms.MaterialBasis;
import lombok.Data;

@Data
public class MaterialBasisDto {
    /**
     * 工厂 泵业/热工等
     */
    private String workCode;

    /**
     * 物料编码
     */
    private String materialNum;

    public MaterialBasisDto(String workCode, String materialNum) {
        this.workCode = workCode;
        this.materialNum = materialNum;
    }

    public static MaterialBasisDto map(MaterialBasis materialBasis) {
        return new MaterialBasisDto(materialBasis.getWorkCode() , materialBasis.getMaterialNum());
    }
}
