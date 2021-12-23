package com.richfit.mes.common.model.sys.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseVo;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.TenantUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Set;

/**
 * @author sun
 * @Description 租户用户
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TenantUserVo extends BaseVo<TenantUser> {
    public TenantUserVo(TenantUser user) {
        BeanUtils.copyProperties(user, this);
    }

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 手机
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 账号状态
     */
    private Boolean status;

    /**
     * 员工姓名
     */
    private String emplName;

    /**
     * 二级单位
     */
    private String orgId;

    /**
     * 所在结构ID
     */
    private String belongOrgId;

    /**
     * 角色
     */
    private List<Role> roleList;

    private String orgName;

    private String belongOrgName;
}
