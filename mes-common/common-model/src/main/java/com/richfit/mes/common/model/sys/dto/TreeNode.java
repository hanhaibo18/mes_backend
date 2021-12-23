package com.richfit.mes.common.model.sys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sun
 * @Description 树形节点
 */
@Data
@ApiModel(value = "树形节点")
public class TreeNode {

    @ApiModelProperty(value = "当前节点id")
    protected String id;

    @ApiModelProperty(value = "父节点id")
    protected String parentId;

    @ApiModelProperty(value = "子节点列表")
    protected List<TreeNode> children = new ArrayList<>();

    @ApiModelProperty(value = "是否有子菜单")
    private Boolean hasChildren = false;

    public void add(TreeNode node) {
        children.add(node);
    }
}
