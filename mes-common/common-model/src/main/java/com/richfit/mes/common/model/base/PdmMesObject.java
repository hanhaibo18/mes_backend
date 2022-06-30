package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Data
@TableName("base_mes_pdm_object")
public class PdmMesObject {

    private static final long serialVersionUID = 1L;
    @TableField(value = "ob_id")
    private String obId;
    /**
     * 工序
     **/
    @ApiModelProperty(value = "工序", dataType = "String")
    private String opId;
    /**
     * 子件类型
     **/
    private String type;
    /**
     * 子件
     **/
    private String id;
    /**
     * 子件名称
     **/
    private String name;
    /**
     * 子件版本
     **/
    private String rev;
    /**
     * 子件数量
     **/
    private String quantity;

    @TableField(value = "datagroup")
    private String dataGroup;

}
