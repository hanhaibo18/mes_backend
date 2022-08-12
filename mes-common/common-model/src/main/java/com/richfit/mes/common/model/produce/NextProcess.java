package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * produce_next_process
 *
 * @author hou
 */
@Data
public class NextProcess extends BaseEntity<NextProcess> {

    /**
     * 当前工序Id
     */
    @ApiModelProperty(value = "当前工序ID", dataType = "String")
    private String currentProcessId;

    /**
     * 工序名称
     */
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String processName;

    /**
     * 下工序Id
     */
    @ApiModelProperty(value = "下工序Id", dataType = "String")
    private String nextProcessId;

    /**
     * 工序号
     */
    @ApiModelProperty(value = "工序号", dataType = "String")
    private String optSequence;
    @TableField(exist = false)
    @ApiModelProperty(value = "跟单Id", dataType = "String")
    private String trackHeadId;

    /**
     * 处理方式
     */
    @ApiModelProperty(value = "处理方式", dataType = "String")
    private String processMode;

    private String tenantId;

    private String branchCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "处理方式", dataType = "String")
    private String drawingNo;
    @TableField(exist = false)
    @ApiModelProperty(value = "工序类型", dataType = "String")
    private String optType;
    @TableField(exist = false)
    @ApiModelProperty(value = "opt工序Id", dataType = "String")
    private String optId;
}
