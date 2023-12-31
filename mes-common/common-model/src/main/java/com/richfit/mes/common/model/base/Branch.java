package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 王瑞
 * @Description 组织机构
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Branch extends BaseEntity<Branch> {

    private static final long serialVersionUID = -5801273490970600632L;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", dataType = "String")
    private String tenantId;

    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码", dataType = "String")
    private String branchCode;

    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称", dataType = "String")
    private String branchName;

    /**
     * 上级机构编码
     */
    @ApiModelProperty(value = "上级机构编码", dataType = "String")
    private String mainBranchCode;

    /**
     * 机构层级
     */
    @ApiModelProperty(value = "机构层级", dataType = "String")
    private String branchLevel;

    /**
     * 机构类型
     */
    @ApiModelProperty(value = "机构类型", dataType = "String")
    private String branchType;

    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号", dataType = "String")
    private Integer orderNo;

    /**
     * 是否使用
     */
    @ApiModelProperty(value = "是否使用", dataType = "String")
    private String isUse;

    @TableField(exist = false)
    @ApiModelProperty(value = "子节点", dataType = "String")
    private List<Branch> branchList;


    /**
     * 描述: 分类：1机加  2装配 3热处理 4钢结构
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/27 10:25
     **/
    private String classes;

}
