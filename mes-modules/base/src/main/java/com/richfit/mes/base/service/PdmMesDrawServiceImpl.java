package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmMesDrawMapper;
import com.richfit.mes.common.model.base.PdmMesDraw;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Service
public class PdmMesDrawServiceImpl extends ServiceImpl<PdmMesDrawMapper, PdmMesDraw> implements PdmMesDrawService {
    @Override
    public List<PdmMesDraw> queryDraw(String itemId, String dataGroup) {
        QueryWrapper<PdmMesDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        queryWrapper.eq("dataGroup", dataGroup);
        return this.list(queryWrapper);
    }
}
