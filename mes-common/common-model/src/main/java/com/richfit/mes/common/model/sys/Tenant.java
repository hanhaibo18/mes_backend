package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 租户
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class Tenant extends BaseEntity<Tenant> {

    private static final long serialVersionUID = 1L;

    /**
     * 租户姓名
     */
    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    /**
     * 租户名称简称
     */
    private String tenantNameForShort;

    /**
     * 租户标识
     */
    @NotBlank(message = "租户标识不能为空")
    private String tenantCode;

    /**
     * 租户ERP标识
     */
    private String tenantErpCode;

    /**
     * 当前状态
     */
    private Integer tenantStatus;

    /**
     * 描述
     */
    private String tenantDesc;

    /**
     * 地址
     */
    private String tenantAddr;

    /**
     * 联系人
     */
    private String tenantContact;

    /**
     * 联系人电话
     */
    private String tenantTel;

    /**
     * 联系人邮件
     */
    private String tenantMail;


}
