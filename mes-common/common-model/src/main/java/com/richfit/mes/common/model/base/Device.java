package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
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
     * 关联设备人员账号(:隔开)
     */
    @TableField(exist = false)
    private String userAccount;

    /**
     * 派工默认
     */
    @TableField(exist = false)
    private String task;

    /**
     * 人员名称
     */
    @TableField(exist = false)
    private String userName;

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
