package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.sys.SystemLog;
import com.richfit.mes.sys.dao.LogMapper;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 日志服务
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, SystemLog> implements LogService {
}
