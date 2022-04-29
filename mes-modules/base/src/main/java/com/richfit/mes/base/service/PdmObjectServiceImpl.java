package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmObjectMapper;
import com.richfit.mes.common.model.base.PdmObject;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmObjectServiceImpl extends ServiceImpl<PdmObjectMapper, PdmObject> implements PdmObjectService {

    /**
     * 功能描述: 根据图号和数据分组查询工装
     * @Author: xinYu.hou
     * @Date: 2022/4/21 14:58
     * @param id 图号
     * @param dataGroup 数据粉最
     * @return: List<PdmObject>
     **/
    @Override
    public List<PdmObject> queryIndustrialAssembly(String id, String dataGroup) {
        QueryWrapper<PdmObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        queryWrapper.eq("dataGroup",dataGroup);
        return this.list(queryWrapper);
    }
}
