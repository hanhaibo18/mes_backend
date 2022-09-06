package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.InspectionDictionary;
import com.richfit.mes.common.model.produce.NextProcess;
import com.richfit.mes.produce.dao.InspectionDictionaryMapper;
import com.richfit.mes.produce.dao.NextProcessMapper;
import org.springframework.stereotype.Service;

/**
 * 探伤模板数据字典
 */
@Service
public class InspectionDictionaryServiceImpl extends ServiceImpl<InspectionDictionaryMapper, InspectionDictionary> implements InspectionDictionaryService {
}
