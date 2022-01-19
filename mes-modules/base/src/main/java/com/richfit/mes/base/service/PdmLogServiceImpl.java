package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmLogMapper;
import com.richfit.mes.common.model.base.PdmLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmLogServiceImpl extends ServiceImpl<PdmLogMapper, PdmLog> implements PdmLogService {

    @Autowired
    private PdmLogMapper pdmLogMapper;

    @Override
    public IPage<PdmLog> queryPageList(int page,int limit,String type, String par, String queryTimeStart, String queryTimeEnd) {
        Page<PdmLog> pdmLogPage = new Page<>(page,limit);
        return pdmLogMapper.queryPageList(pdmLogPage,type,par,queryTimeStart,queryTimeEnd);
    }
}
