package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmMesProcessMapper;
import com.richfit.mes.common.model.base.PdmMesProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Service
public class PdmMesProcessServiceImpl extends ServiceImpl<PdmMesProcessMapper, PdmMesProcess> implements PdmMesProcessService {


    @Autowired
    private PdmMesProcessMapper pdmMesProcessMapper;


    @Override
    public IPage<PdmMesProcess> queryPageList(int page, int limit, PdmMesProcess pdmProcess) {
        Page<PdmMesProcess> ipage = new Page<>(page, limit);
        return pdmMesProcessMapper.queryPageList(ipage, pdmProcess);
    }

    @Override
    public List<PdmMesProcess> queryList(PdmMesProcess pdmProcess) {
        return pdmMesProcessMapper.queryList(pdmProcess);
    }
}
