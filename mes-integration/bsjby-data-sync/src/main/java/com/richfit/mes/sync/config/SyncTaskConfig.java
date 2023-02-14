package com.richfit.mes.sync.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author gaol
 * @date 2023/2/9
 * @apiNote
 */
@Data
public class SyncTaskConfig {

    @ApiModelProperty(value = "单个表配置", dataType = "SyncTableConfig")
    List<SyncTableConfig> tableConfigs;

    String tenantId;

    @ApiModelProperty(value = "原附件的目录位置", dataType = "SyncTableConfig")
    String fileCatalog;
}
