package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @author 马峰
 * @Description 工艺
 */
@Data
public class Device extends BaseEntity<Device> {

    /**
     * 所属设备组
     */
    private String parentId;
    /**
     * 设备编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 型号
     */
    private String model;
    /**
     * 类型
     */
    private String type;
    /**
     * 制造商
     */
    private String maker;

    /**
     * 入库日期
     */
    private Date inTime;
    /**
     * 出库日期
     */
    private Date outTime;
    /**
     * 启用状态
     */
    private String status;
    /**
     * 运行状态
     */
    private String runStatus;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 图标
     */
    private String icon;
    /**
     * 制造商
     */
    private String manufacturers;
}
