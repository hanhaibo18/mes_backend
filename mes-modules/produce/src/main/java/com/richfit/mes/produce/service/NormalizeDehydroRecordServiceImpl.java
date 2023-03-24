package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.NormalizeDehydroRecord;
import com.richfit.mes.produce.dao.NormalizeDehydroRecordMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 正火去氢工序控制记录(ProduceNormalizeDehydroRecord)表服务实现类
 *
 * @author makejava
 * @since 2023-03-23 14:18:24
 */
@Service
public class NormalizeDehydroRecordServiceImpl extends ServiceImpl<NormalizeDehydroRecordMapper, NormalizeDehydroRecord> implements NormalizeDehydroRecordService {

    @Autowired
    private NormalizeDehydroRecordMapper normalizeDehydroRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatch(List<NormalizeDehydroRecord> normalizeDehydroRecordList, String itemId) {
        //先查出该工单所有的正火去氢记录
        QueryWrapper<NormalizeDehydroRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        List<NormalizeDehydroRecord> normalizeDehydroRecords = normalizeDehydroRecordMapper.selectList(queryWrapper);

        //获取需要删除的List(根据itemId查询出来存在但是传入参数没有的数据)
        List<NormalizeDehydroRecord> deleteList = new ArrayList<>();
        deleteList.addAll(normalizeDehydroRecords);
        deleteList.removeAll(normalizeDehydroRecordList);
        if (!deleteList.isEmpty()) {
            normalizeDehydroRecordMapper.deleteBatchIds(deleteList);
        }
        //获取新增的List
        List<NormalizeDehydroRecord> addList = normalizeDehydroRecordList.stream().filter(x -> x.getId() == null).collect(Collectors.toList());
        if (!addList.isEmpty()) {
            this.saveBatch(addList);
        }

        //updateList
        normalizeDehydroRecordList.removeAll(addList);
        if (!normalizeDehydroRecordList.isEmpty()) {
            this.updateBatchById(normalizeDehydroRecordList);
        }

        return false;
    }
}

