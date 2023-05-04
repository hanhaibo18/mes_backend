package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.RawMaterialRecord;

import java.util.List;

/**
 * 原材料消耗记录表(RawMaterialRecord)表服务接口
 *
 * @author makejava
 * @since 2023-04-27 14:16:36
 */
public interface RawMaterialRecordService extends IService<RawMaterialRecord> {

    List<RawMaterialRecord> queryrawMaterialRecordCacheByItemId(String tiId);
}

