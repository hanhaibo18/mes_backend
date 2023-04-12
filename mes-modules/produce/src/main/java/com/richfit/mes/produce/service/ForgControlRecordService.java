package com.richfit.mes.produce.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ForgControlRecord;

import java.util.List;

/**
 * 锻造工序控制记录表(ProduceForgControlRecord)表服务接口
 *
 * @author makejava
 * @since 2023-03-23 14:07:26
 */
public interface ForgControlRecordService extends IService<ForgControlRecord> {

    Boolean updateBatch(List<ForgControlRecord> forgControlRecordlist, String itemId);

    List<ForgControlRecord> queryForgControlRecordCacheByItemId(String tiId);
}

