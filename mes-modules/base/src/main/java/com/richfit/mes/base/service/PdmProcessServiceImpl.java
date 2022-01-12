package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmProcessMapper;
import com.richfit.mes.common.model.base.PdmProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmProcessServiceImpl extends ServiceImpl<PdmProcessMapper, PdmProcess> implements PdmProcessService {


    @Autowired
    private PdmProcessMapper pdmProcessMapper;


    @Override
    public IPage<PdmProcess> queryPageList(int page, int limit,PdmProcess pdmProcess) {
        Page<PdmProcess> ipage = new Page<>(page,limit);
        return pdmProcessMapper.queryPageList(ipage, pdmProcess);
    }

    @Override
    public List<PdmProcess> queryList(PdmProcess pdmProcess) {
        return pdmProcessMapper.queryList(pdmProcess);
    }
}
