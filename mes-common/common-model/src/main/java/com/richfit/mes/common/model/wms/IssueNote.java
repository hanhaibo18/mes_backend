package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * MES接收/拒绝WMS出库单
 */
@Data
public class IssueNote implements Serializable {

    /**
     * 工厂
     */
    private String workCode;

    /**
     * 库房
     */
    private String invCode;

    /**
     * 参考单ID
     */
    private String refCodeId;

    /**
     * 参考单号
     */
    private String refCode;

    /**
     * 出库单ID
     */
    private String transId;

    /**
     * 出库单号
     */
    private String transNum;

    /**
     * 接收与否
     */
    private String receiveFlag;

    /**
     * 操作人
     */
    private String optUser;

    /**
     * 操作日期
     */
    private String optDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
