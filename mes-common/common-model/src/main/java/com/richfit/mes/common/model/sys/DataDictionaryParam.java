package com.richfit.mes.common.model.sys;

import java.util.Date;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 数据字典参数表(SysDataDictionaryParam)表实体类
 *
 * @author makejava
 * @since 2023-04-03 15:11:45
 */
@Data
public class DataDictionaryParam extends BaseEntity<DataDictionaryParam> {

    //数据字典id
    private String dictionaryId;
    //序号
    private Integer orderNum;
    //物料编码
    private String materialNo;
    //物料名称
    private String materialName;
    //材质
    private String texture;
    //物料规格
    private String specifications;
    //车间编码
    private String branchCode;


}

