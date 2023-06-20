package com.richfit.mes.produce.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wcy
 * <p>
 * 钻机整改单据附件
 */
@Data
public class ProduceDrillingRectificationFileDTO implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private String fileId;

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