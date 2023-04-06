package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 数据字典表(SysDataDictionary)表实体类
 *
 * @author makejava
 * @since 2023-04-03 15:07:46
 */
@Data
public class DataDictionary extends BaseEntity<DataDictionary> {

    //车间名称
    private String branchName;
    //车间编码
    private String branchCode;
    //租户id
    private String tenantId;
}

