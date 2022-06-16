package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 马峰
 * @Description 工艺
 */
@Data
public class Router extends BaseEntity<Router> {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 工艺图号
     */
    private String routerNo;

    /**
     * 工艺名称
     */
    private String routerName;

    /**
     * 工艺描述
     */
    private String remark;

    /**
     * 工艺版本号
     */
    private String version;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态 0=未激活 1=激活
     */
    private String status;

    /**
     * 2==历史
     */
    private String isActive;

    /**
     * 物料号，必填
     */
    private String materialNo;
    /**
     * 物料号，必填
     */
    private String materialVersion;

    /**
     * G6流程节点
     */
    private String flow;

    /**
     * 图号
     */
    private String drawNo;

    @ApiModelProperty(value = "该工艺历史版本")
    @TableField(exist = false)
    private List<Router> children;
}
