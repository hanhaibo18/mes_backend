package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 合格证表
 */
@Data
public class Certificate extends BaseEntity<Certificate> {

    /**
     * 租户ID
     */
    private String tenantId;

    private String branchCode;

    /**
     * 合格证编号
     */
    private String certificateNo;

    /**
     * 状态 0工序合格证 1完工合格证
     */
    private String type;

    /**
     * 下道工序车间
     */
    private String nextOptWork;

    /**
     * 检测员
     */
    private String checkName;

    /**
     * 检测日期
     */
    private Date checkTime;

    @TableField(exist = false)
    private String drawingNo;

    @TableField(exist = false)
    private String productNo;

    @TableField(exist = false)
    private String materialNo;

    @TableField(exist = false)
    private Integer number;

    @TableField(exist = false)
    private String optId;

    @TableField(exist = false)
    private String optName;

    @TableField(exist = false)
    private String optVer;

    @TableField(exist = false)
    private List<TrackCertificate> trackCertificates;

}
