package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.RecordsOfPourOperations;
import com.richfit.mes.produce.dao.RecordsOfPourOperationsMapper;
import org.springframework.stereotype.Service;

/**
 * (RecordsOfPourOperations)表服务实现类
 *
 * @author makejava
 * @since 2023-05-12 15:53:49
 */
@Service("recordsOfPourOperationsService")
public class RecordsOfPourOperationsServiceImpl extends ServiceImpl<RecordsOfPourOperationsMapper, RecordsOfPourOperations> implements RecordsOfPourOperationsService {

}

