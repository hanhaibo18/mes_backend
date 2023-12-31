package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * inventory_query  MES实时查询WMS库存
 */
@Data
public class InventoryQuery implements Serializable {
    /**
     * 工厂
     */
    private String workCode;

    /**
     * 库房
     */
    private String invCode;

    /**
     * 物料编码
     */
    private String materialNum;

    /**
     * 库存类型  正式/在途/无参考
     */
    private String invType;

    /**
     * 工作号
     */
    private String jobNo;

    //图号
    @TableField(exist = false)
    private String drawingNo;

    //单重
    @TableField(exist = false)
    private String weight;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}