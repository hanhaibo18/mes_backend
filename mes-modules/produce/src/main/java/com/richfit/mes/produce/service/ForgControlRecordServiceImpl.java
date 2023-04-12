package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ForgControlRecord;
import com.richfit.mes.produce.dao.ForgControlRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 锻造工序控制记录表(ProduceForgControlRecord)表服务实现类
 *
 * @author makejava
 * @since 2023-03-23 14:07:26
 */
@Service
public class ForgControlRecordServiceImpl extends ServiceImpl<ForgControlRecordMapper, ForgControlRecord> implements ForgControlRecordService {

    @Autowired
    private ForgControlRecordMapper forgControlRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBatch(List<ForgControlRecord> forgControlRecordlist, String itemId) {
        //先查出该工单所有的正火去氢记录
        QueryWrapper<ForgControlRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        List<ForgControlRecord> forgControlRecords = forgControlRecordMapper.selectList(queryWrapper);

        //获取需要删除的List(根据itemId查询出来存在但是传入参数没有的数据)
        List<ForgControlRecord> deleteList = new ArrayList<>();
        deleteList.addAll(forgControlRecords);
        deleteList.removeAll(forgControlRecordlist);
        if (!deleteList.isEmpty()) {
            forgControlRecordMapper.deleteBatchIds(deleteList);
        }
        //获取新增的List
        List<ForgControlRecord> addList = forgControlRecordlist.stream().filter(x -> x.getId() == null).collect(Collectors.toList());
        if (!addList.isEmpty()) {
            this.saveBatch(addList);
        }

        //updateList
        forgControlRecordlist.removeAll(addList);
        if (!forgControlRecordlist.isEmpty()) {
            this.updateBatchById(forgControlRecordlist);
        }
        return false;
    }

    @Override
    public List<ForgControlRecord> queryForgControlRecordCacheByItemId(String tiId) {
        return forgControlRecordMapper.queryForgControlRecordCacheByItemId(tiId);
    }
}

