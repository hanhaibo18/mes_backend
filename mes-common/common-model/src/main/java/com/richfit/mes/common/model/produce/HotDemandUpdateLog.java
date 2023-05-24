package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * produce_hot_demand
 *
 * @author
 */
@Data
public class HotDemandUpdateLog extends BaseEntity<HotDemandUpdateLog> implements Serializable {

    @ApiModelProperty(value = "需求提报id ", dataType = "String")
    private String demandId;
    /**
     * 图号
     */
    @ApiModelProperty(value = "图号 ", dataType = "String")
    private String drawNo;
    /**
     * 材质
     */
    @ApiModelProperty(value = " 变更之前材质", dataType = "String")
    private String oldTexture;
    @ApiModelProperty(value = " 变更之后材质", dataType = "String")
    private String newTexture;
    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号 ", dataType = "String")
    private String versionNum;
    /**
     * 凭证号
     */
    @ApiModelProperty(value = " 凭证号", dataType = "String")
    private String voucherNo;

    private static final long serialVersionUID = 1L;
}