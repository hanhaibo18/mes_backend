package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.RawMaterialRecord;
import com.richfit.mes.produce.dao.RawMaterialRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 原材料消耗记录表(RawMaterialRecord)表服务实现类
 *
 * @author makejava
 * @since 2023-04-27 14:16:36
 */
@Service
public class RawMaterialRecordServiceImpl extends ServiceImpl<RawMaterialRecordMapper, RawMaterialRecord> implements RawMaterialRecordService {
    @Autowired
    private RawMaterialRecordMapper rawMaterialRecordMapper;

    @Override
    public List<RawMaterialRecord> queryrawMaterialRecordCacheByItemId(String tiId) {
        return rawMaterialRecordMapper.queryrawMaterialRecordCacheByItemId(tiId);
    }
}

