package com.richfit.mes.common.model.sys.vo;

import com.richfit.mes.common.model.sys.Menu;
import com.richfit.mes.common.model.sys.dto.MenuTree;
import com.richfit.mes.common.model.sys.dto.TreeNode;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sun
 * @Description 树形菜单工具类
 */
@UtilityClass
public class MenuTreeUtil {
    /**
     * 两层循环实现建树
     *
     * @param treeNodes 传入的树节点列表
     * @return
     */
    public <T extends TreeNode> List<T> build(List<T> treeNodes, Object root) {

        List<T> trees = new ArrayList<>();

        for (T treeNode : treeNodes) {

            if (root.equals(treeNode.getParentId())) {
                trees.add(treeNode);
            }

            for (T it : treeNodes) {
                if (it.getParentId().equals(treeNode.getId())) {
                    if (treeNode.getChildren() == null) {
                        treeNode.setChildren(new ArrayList<>());
                    }
                    treeNode.setHasChildren(true);
                    treeNode.add(it);
                }
            }
        }
        return trees;
    }

    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return
     */
    public <T extends TreeNode> List<T> buildByRecursive(List<T> treeNodes, Object root) {
        List<T> trees = new ArrayList<T>();
        for (T treeNode : treeNodes) {
            if (root.equals(treeNode.getParentId())) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public <T extends TreeNode> T findChildren(T treeNode, List<T> treeNodes) {
        for (T it : treeNodes) {
            if (treeNode.getId() == it.getParentId()) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }

    /**
     * 通过Menu创建树形节点
     *
     * @param menus
     * @param root
     * @return
     */
    public List<MenuTree> buildTree(List<Menu> menus, String root) {
        List<MenuTree> trees = new ArrayList<>();
        MenuTree node;
        for (Menu menu : menus) {
            node = new MenuTree();
            node.setMenuName(menu.getMenuName());
            node.setMenuType(menu.getMenuType());
            node.setOpenType(menu.getOpenType());
            node.setMenuUrl(menu.getMenuUrl());
            node.setMenuIcon(menu.getMenuIcon());
            node.setMenuOrder(menu.getMenuOrder());
            node.setMenuCode(menu.getMenuCode());
            node.setParentId(menu.getParentId());
            node.setMenuShow(menu.getMenuShow());
            node.setPermission(menu.getPermission());
            node.setChecked(menu.isChecked());
            node.setHasChildren(true);
            node.setId(menu.getId());
            trees.add(node);
        }
        return MenuTreeUtil.build(trees, root);
    }

}
