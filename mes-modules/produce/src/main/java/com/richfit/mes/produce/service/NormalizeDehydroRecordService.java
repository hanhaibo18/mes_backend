package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.NormalizeDehydroRecord;

import java.util.List;

/**
 * 正火去氢工序控制记录(ProduceNormalizeDehydroRecord)表服务接口
 *
 * @author makejava
 * @since 2023-03-23 14:18:24
 */
public interface NormalizeDehydroRecordService extends IService<NormalizeDehydroRecord> {

    boolean updateBatch(List<NormalizeDehydroRecord> normalizeDehydroRecordList, String itemI);
}

