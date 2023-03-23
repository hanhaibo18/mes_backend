package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ForgControlRecord;
import com.richfit.mes.produce.dao.ForgControlRecordMapper;
import org.springframework.stereotype.Service;

/**
 * 锻造工序控制记录表(ProduceForgControlRecord)表服务实现类
 *
 * @author makejava
 * @since 2023-03-23 14:07:26
 */
@Service("produceForgControlRecordService")
public class ForgControlRecordServiceImpl extends ServiceImpl<ForgControlRecordMapper, ForgControlRecord> implements ForgControlRecordService {

}

