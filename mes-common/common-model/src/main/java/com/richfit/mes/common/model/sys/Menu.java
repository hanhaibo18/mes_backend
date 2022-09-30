package com.richfit.mes.common.model.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;

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


    private static final long serialVersionUID = -7226701090703246871L;
    /**
     * 名称
     */
    private String menuName;

    /**
     * 类型
     */
    private Integer menuType;

    /**
     * 菜单唯一编码
     */
    private String menuCode;

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


    private Integer menuOrder = 1;

    private String parentId;

    private String menuShow;
    /**
     * 菜单权限标识
     */
    private String permission;

    /**
     * 公司代码
     */
    private String companyCode;

    /**
     * 按钮权限 分配权限用
     */
    @TableField(exist = false)
    private Object checkedButton;

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


    @Override
    public int hashCode() {
        return Objects.hash(id, menuName);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Menu menu = (Menu) o;
        return Objects.equals(menu.id, id);
    }
}
