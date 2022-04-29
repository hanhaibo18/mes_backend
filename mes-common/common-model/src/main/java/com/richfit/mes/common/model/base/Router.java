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
     * 物料号，必填
     */
    private String materialNo;
        /**
     * 物料号，必填
     */
    private String materialVersion;
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
     * 创建人
     */
    private String createBy;

        /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

        /**
     * 修改时间
     */
    private Date modifyTime;
        /**
     * G6流程节点
     */
    private String flow;

    /**
     *图号
     */
   private String drawNo;


}
