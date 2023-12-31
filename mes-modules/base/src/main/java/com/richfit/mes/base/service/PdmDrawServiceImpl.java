package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmDrawMapper;
import com.richfit.mes.common.model.base.PdmDraw;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmDrawServiceImpl extends ServiceImpl<PdmDrawMapper, PdmDraw> implements PdmDrawService {
    @Override
    public List<PdmDraw> queryDraw(String itemId, String dataGroup) {
        QueryWrapper<PdmDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        queryWrapper.eq("dataGroup", dataGroup);
        return this.list(queryWrapper);
    }

    @Override
    public List<PdmDraw> queryDrawList(String itemId) {
        QueryWrapper<PdmDraw> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        return this.list(queryWrapper);
    }
}
