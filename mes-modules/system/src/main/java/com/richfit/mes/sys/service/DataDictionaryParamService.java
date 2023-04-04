package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.sys.DataDictionaryParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 数据字典参数表(SysDataDictionaryParam)表服务接口
 *
 * @author makejava
 * @since 2023-04-03 15:19:28
 */
public interface DataDictionaryParamService extends IService<DataDictionaryParam> {

    String improtExcel(MultipartFile file, String id);
}

