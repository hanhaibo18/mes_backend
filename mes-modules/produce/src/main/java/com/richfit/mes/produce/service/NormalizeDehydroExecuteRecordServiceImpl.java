package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.NormalizeDehydroExecuteRecord;
import com.richfit.mes.common.model.produce.NormalizeDehydroRecord;
import com.richfit.mes.produce.dao.NormalizeDehydroRecordExecuteMapper;
import com.richfit.mes.produce.dao.NormalizeDehydroRecordMapper;
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
public class NormalizeDehydroExecuteRecordServiceImpl extends ServiceImpl<NormalizeDehydroRecordExecuteMapper, NormalizeDehydroExecuteRecord> implements NormalizeDehydroExecuteRecordService {

    @Autowired
    private NormalizeDehydroRecordExecuteMapper normalizeDehydroRecordExecuteMapper;

}

