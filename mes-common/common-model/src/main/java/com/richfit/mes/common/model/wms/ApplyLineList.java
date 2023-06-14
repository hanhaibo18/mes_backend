package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
}