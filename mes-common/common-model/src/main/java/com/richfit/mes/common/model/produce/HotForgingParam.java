package com.richfit.mes.common.model.produce;

import java.io.Serializable;
import java.util.Date;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * produce_hot_forging_param
 * @author 
 */
@Data
public class HotForgingParam extends BaseEntity<HotForgingParam> implements Serializable {


    /**
     * 仿型库表ID
     */
    @ApiModelProperty(value = "仿型库表ID",required = false)
    private String forgingStoreId;

    /**
     * 参数名称
     */
    @ApiModelProperty(value = "参数名称",required = true)
    private String paramName;


    private static final long serialVersionUID = 1L;
}