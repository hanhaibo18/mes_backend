package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmLogMapper;
import com.richfit.mes.common.model.base.PdmLog;
import org.springframework.stereotype.Service;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmLogServiceImpl extends ServiceImpl<PdmLogMapper, PdmLog> implements PdmLogService {
}
