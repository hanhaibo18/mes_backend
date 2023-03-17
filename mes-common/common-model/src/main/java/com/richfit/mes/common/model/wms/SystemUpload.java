package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * system_upload  MES系统将锁定、释放的工厂锁定库存上传WMS
 */
@Data
public class SystemUpload implements Serializable {
    /**
     * 库存ID
     */
    private String inventoryId;

    /**
     * 工厂
     */
    private String workCode;

    /**
     * 库存地点  车间关联库存地点
     */
    private String invCode;

    /**
     * 物料编码
     */
    private String materialNum;

    /**
     * 数量
     */
    private String quantity;

    /**
     * 操作类型  锁库/解锁
     */
    private Integer optType;

    /**
     * MES领料单ID
     */
    private String applyId;

    /**
     * MES领料单编号
     */
    private String applyNum;

    /**
     * 工作号
     */
    private String jobNo;

    /**
     * 跟单id
     */
    private String trackId;

    /**
     * 跟单号
     */
    private String trackNo;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 产品图号
     */
    private String drawingNum;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * MES领料单行id
     */
    private String applyLineId;

    /**
     * MES领料单行项目
     */
    private String applyLineNum;

    /**
     * 操作人
     */
    private String optUser;

    /**
     * 操作时间
     */
    private String optDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}