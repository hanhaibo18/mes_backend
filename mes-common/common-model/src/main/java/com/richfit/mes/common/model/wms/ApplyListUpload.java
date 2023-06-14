package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.model.produce.Certificate;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * apply_list_upload MES申请单上传WMS（已上线）
 */
@Data
public class ApplyListUpload implements Serializable {
    /**
     * MES申请单ID 唯一
     */
    private String id;

    /**
     * MES申请单号
     */
    private String applyNum;

    /**
     * 数据类型
     */
    private String transType;

    /**
     * 工厂
     */
    private String workCode;

    /**
     * 车间  通过车间指向成品仓库
     */
    private String workshop;

    /**
     * 库存地点
     */
    private String invCode;

    /**
     * 工作号
     */
    private String jobNo;

    /**
     * 生产订单
     */
    private String prodNum;

    /**
     * 合格证 结构化
     */
    private String certificate;

    /**
     * 炉批号
     */
    private String batchNo;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 行数据 明细列表
     */
    @TableField(exist = false)
    private List<ApplyLineList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public ApplyListUpload() {

    }

    public ApplyListUpload(Certificate certificate) {

    }
}