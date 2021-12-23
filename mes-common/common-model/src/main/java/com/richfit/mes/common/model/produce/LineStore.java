package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 线边库
 */
@Data
public class LineStore extends BaseEntity<LineStore> {

    /**
     * 租户ID
     */
    private String tenantId;

    private String trackNo;

    /**
     * 物料编号
     */
    private String materialNo;

    /**
     * 图号
     */
    private String drawingNo;

    /**
     * 状态 0在制 1完工 2作废 3已消耗
     */
    private String status;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 合格证编号
     */
    private String certificateNo;

    /**
     * 物料来源
     */
    private String materialSource;

    /**
     * 炉批号
     */
    private String batchNo;

    /**
     * 工作号
     */
    private String workNo;

    /**
     * 毛坯编号
     */
    private String workblankNo;

    /**
     * 物料类型 0毛坯 1半成品/成品
     */
    private String materialType;

    /**
     * 跟踪方式 0单件 1批次
     */
    private String trackType;

    /**
     * 已使用数量
     */
    private Integer userNum;

    @TableField(exist = false)
    private String assemblyId;

    /**
     * 入库时间
     */
    private Date inTime;

    /**
     * 出库时间
     */
    private Date outTime;
}
