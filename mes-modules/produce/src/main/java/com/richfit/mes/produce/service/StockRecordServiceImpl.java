package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.StockRecord;
import com.richfit.mes.produce.dao.StockRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: 王瑞
 * @Date: 2020/8/11
 */
@Slf4j
@Service
@Transactional
public class StockRecordServiceImpl extends ServiceImpl<StockRecordMapper, StockRecord> implements StockRecordService{

    @Autowired
    StockRecordMapper stockRecordMapper;


}
