package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wcy
 * <p>
 * 整改单附件信息
 * @TableName produce_drilling_rectification_file
 */
@TableName(value = "produce_drilling_rectification_file")
@Data
public class ProduceDrillingRectificationFile implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 整改单号
     */
    @ApiModelProperty(value = "整改单号")
    private String orderNo;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * branch_code
     */
    @ApiModelProperty(value = "branch_code")
    private String branchCode;

    /**
     * branch_name
     */
    @ApiModelProperty(value = "branch_name")
    private String branchName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}