package com.richfit.mes.produce.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.PhysChemResult;
import com.richfit.mes.produce.dao.PhysChemResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author renzewen
 * @Description 理化检验试验结果
 */
@Slf4j
@Service
public class PhysChemResultServiceImpl extends ServiceImpl<PhysChemResultMapper, PhysChemResult> implements PhysChemResultService {


    /**
     * 根据id修改实验结果数据
     * @param physChemResult
     * @return
     */
    @Override
    public boolean updateResult(PhysChemResult physChemResult) {
        return this.updateById(physChemResult);
    }


}
