package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 正火去氢工序执行记录(NormalizeDehydroExecuteRecord)表实体类
 *
 * @author makejava
 * @since 2023-05-06 14:13:03
 */
@Data
@TableName("produce_normalize_dehydro_execute_record")
public class NormalizeDehydroExecuteRecord extends BaseEntity<NormalizeDehydroExecuteRecord> {
    @ApiModelProperty(value = "正火去氢工序控制记录表id", dataType = "String")
    private String recordId;
    @ApiModelProperty(value = "记录人", dataType = "String")
    private String recordBy;
    @ApiModelProperty(value = "班次", dataType = "String")
    private String workTime;
    @ApiModelProperty(value = "转运记录", dataType = "String")
    private String transferRecord;
    @ApiModelProperty(value = "时间", dataType = "Date")
    private Date date;
}

