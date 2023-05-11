package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

/**
 * 正火去氢工序控制记录(ProduceNormalizeDehydroRecord)表实体类
 *
 * @author makejava
 * @since 2023-03-23 14:13:03
 */
@Data
@TableName("produce_normalize_dehydro_record")
public class NormalizeDehydroRecord extends BaseEntity<NormalizeDehydroRecord> {

    @ApiModelProperty(value = "记录编号", dataType = "String")
    private String serialNo;
    @ApiModelProperty(value = "0 正火  1 去氢", dataType = "Integer")
    private Integer type;
    @ApiModelProperty(value = "设备编号", dataType = "String")
    private String equipmentNo;
    @ApiModelProperty(value = "审核状态 0 未通过  1 通过", dataType = "Integer")
    private Integer auditStatus;
    @ApiModelProperty(value = "附件记录id", dataType = "String")
    private String attachmentId;
    @ApiModelProperty(value = "预装炉id", dataType = "String")
    private String furnaceId;
    @ApiModelProperty(value = "问题处理", dataType = "String")
    private String problemProcessing;

    @ApiModelProperty(value = "开始时间", dataType = "String")
    @TableField(exist = false)
    private String startTime;
    @ApiModelProperty(value = "结束时间", dataType = "String")
    @TableField(exist = false)
    private String endTime;
    @ApiModelProperty(value = "工艺执行记录List", dataType = "String")
    @TableField(exist = false)
    private List<NormalizeDehydroExecuteRecord> executeRecord;

}

