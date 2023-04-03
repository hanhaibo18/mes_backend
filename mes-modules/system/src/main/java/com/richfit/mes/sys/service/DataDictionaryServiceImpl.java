package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.sys.DataDictionary;
import com.richfit.mes.sys.dao.DataDictionaryDao;
import org.springframework.stereotype.Service;

/**
 * 数据字典表(SysDataDictionary)表服务实现类
 *
 * @author makejava
 * @since 2023-04-03 15:18:30
 */
@Service
public class DataDictionaryServiceImpl extends ServiceImpl<DataDictionaryDao, DataDictionary> implements DataDictionaryService {

}

