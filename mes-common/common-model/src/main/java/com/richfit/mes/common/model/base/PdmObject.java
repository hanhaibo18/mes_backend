package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author rzw
 * @date 2022-01-04 10:46
 */
@Data
@TableName("base_i_pdm_object")
public class PdmObject {

    private static final long serialVersionUID = 1L;
    @TableField(value = "ob_id")
    private String obId;
    //工序
    private String opId;
    //子件类型
    private String type;
    //子件
    private String id;
    //子件名称
    private String name;
    //子件版本
    private String rev;
    //子件数量
    private String quantity;

    @TableField(value = "datagroup")
    private String dataGroup;

}
