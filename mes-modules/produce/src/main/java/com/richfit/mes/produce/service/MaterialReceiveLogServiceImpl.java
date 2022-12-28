package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.MaterialReceiveLog;
import com.richfit.mes.produce.dao.MaterialReceiveLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @Description 用于存储物料接收日志记录
 * @Author zhiqiang.lu
 * @Date 2022/12/28 09:25
 */
@Slf4j
@Service
public class MaterialReceiveLogServiceImpl extends ServiceImpl<MaterialReceiveLogMapper, MaterialReceiveLog> implements MaterialReceiveLogService {

}
