package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.NormalizeDehydroRecord;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.NormalizeDehydroRecordExecuteMapper;
import com.richfit.mes.produce.dao.NormalizeDehydroRecordMapper;
import com.richfit.mes.produce.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 正火去氢工序控制记录(ProduceNormalizeDehydroRecord)表服务实现类
 *
 * @author makejava
 * @since 2023-03-23 14:18:24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class NormalizeDehydroRecordServiceImpl extends ServiceImpl<NormalizeDehydroRecordMapper, NormalizeDehydroRecord> implements NormalizeDehydroRecordService {

    @Autowired
    private NormalizeDehydroRecordMapper normalizeDehydroRecordMapper;
    @Autowired
    private NormalizeDehydroRecordExecuteMapper normalizeDehydroRecordExecuteMapper;
    @Autowired
    private NormalizeDehydroExecuteRecordService normalizeDehydroExecuteRecordService;
    /**
     * 添加正火去氢记录
     * @param record
     * @return
     */
    @Override
    public Boolean saveNormalizeDehydroRecord(NormalizeDehydroRecord record) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //记录编号
        String timeStemp = String.valueOf(System.currentTimeMillis());
        String yyyyMMddhhmmss = DateUtils.dateToString(new Date(), "yyyyMMddhhmmss");
        //记录编号
        record.setSerialNo(yyyyMMddhhmmss+timeStemp.substring(timeStemp.length()-4));
        //'审核状态 0 未通过  1 通过'
        record.setAuditStatus(0);
        if (CollectionUtils.isNotEmpty(record.getExecuteRecord())){
            //保存工艺执行记录
            normalizeDehydroExecuteRecordService.saveBatch(record.getExecuteRecord());
        }
        int insert = normalizeDehydroRecordMapper.insert(record);
        if(insert>0){
            return true;
        }else {
            return false;
        }

    }

    /**
     * 修改正火去氢记录
     * @param normalizeDehydroRecord
     * @return
     */

    @Override
    public boolean updateNormalizeDehydroRecord(NormalizeDehydroRecord normalizeDehydroRecord) {
        if (CollectionUtils.isNotEmpty(normalizeDehydroRecord.getExecuteRecord())){
            //修改或者添加工艺执行记录
            normalizeDehydroExecuteRecordService.saveOrUpdateBatch(normalizeDehydroRecord.getExecuteRecord());
        }
        int i = normalizeDehydroRecordMapper.updateById(normalizeDehydroRecord);
        if(i>0){
            return true;
        }else {
            return false;
        }
    }

}

