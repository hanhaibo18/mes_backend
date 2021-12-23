package com.richfit.mes.common.model.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 系统菜单
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class Menu extends BaseEntity<Menu> {


    /**
     * 名称
     */
    private String menuName;

    /**
     * 类型
     */
    private Integer menuType;

    /**
     * 打开方式
     */
    private Integer openType;

    /**
     * 链接
     */
    private String menuUrl;

    /**
     * 菜单图标
     */
    private String menuIcon;

    private Integer menuOrder;

    private String parentId;

    private String menuShow;
    /**
     * 菜单权限标识
     */
    private String permission;
    /**
     * 是否选中  分配菜单用
     */
    @TableField(exist = false)
    private boolean checked;

    /**
     * 租户菜单Id
     */
    @TableField(exist = false)
    private String tenantMenuId;

}