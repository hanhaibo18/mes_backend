package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmMesObjectMapper;
import com.richfit.mes.common.model.base.PdmMesObject;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmMesObjectServiceImpl extends ServiceImpl<PdmMesObjectMapper, PdmMesObject> implements PdmMesObjectService {

    /**
     * 功能描述: 根据图号和数据分组查询工装
     *
     * @param id        图号
     * @param dataGroup 数据粉最
     * @Author: xinYu.hou
     * @Date: 2022/4/21 14:58
     * @return: List<PdmObject>
     **/
    @Override
    public List<PdmMesObject> queryIndustrialAssembly(String id, String dataGroup) {
        QueryWrapper<PdmMesObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("dataGroup", dataGroup);
        return this.list(queryWrapper);
    }

    @Override
    public List<PdmMesObject> selectFixtureList(String optId) {
        QueryWrapper<PdmMesObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("op_id", optId);
        return this.list(queryWrapper);
    }
}
