package com.richfit.mes.common.model.sys.dto;

import com.richfit.mes.common.model.sys.Menu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author sun
 * @Description 菜单树
 */
@Data
@ApiModel(value = "菜单树")
@EqualsAndHashCode(callSuper = true)
public class MenuTree extends TreeNode implements Serializable {
    /**
     * 名称
     */
    @ApiModelProperty(value = "菜单名称")
    private String menuName;
    /**
     * 菜单标签
     */
    @ApiModelProperty(value = "菜单标签")
    private String label;
    /**
     * 类型
     */
    @ApiModelProperty(value = "菜单类型")
    private Integer menuType;

    /**
     * 打开方式
     */
    @ApiModelProperty(value = "打开方式")
    private Integer openType;

    /**
     * 链接
     */
    @ApiModelProperty(value = "链接")
    private String menuUrl;

    /**
     * 菜单图标
     */
    @ApiModelProperty(value = "菜单图标")
    private String menuIcon;

    /**
     * 菜单顺序
     */
    @ApiModelProperty(value = "菜单顺序")
    private Integer menuOrder;

    /**
     * 显示URL
     */
    @ApiModelProperty(value = "显示URL")
    private String menuShow;

    /**
     * 权限标识
     */
    @ApiModelProperty(value = "权限标识")
    private String permission;
    /**
     * 是否选中  分配菜单用
     */
    @ApiModelProperty(value = "是否显示")
    private boolean checked;



    public MenuTree() {
    }

    public MenuTree(String id, String menuName, String parentId) {
        this.id = id;
        this.parentId = parentId;
        this.menuName = menuName;
        this.label = menuName;
    }

    public MenuTree(String id, String menuName, MenuTree parent) {
        this.id = id;
        this.parentId = parent.getId();
        this.menuName = menuName;
        this.label = menuName;
    }

    public MenuTree(Menu menu) {
        this.id = menu.getId();
        this.parentId = menu.getParentId();
        this.menuName = menu.getMenuName();
        this.menuIcon = menu.getMenuIcon();
        this.menuOrder = menu.getMenuOrder();
        this.label = menu.getMenuName();
        this.menuType = menu.getMenuType();
        this.menuShow = menu.getMenuShow();
        this.menuUrl = menu.getMenuUrl();
        this.permission = menu.getPermission();
    }
}
