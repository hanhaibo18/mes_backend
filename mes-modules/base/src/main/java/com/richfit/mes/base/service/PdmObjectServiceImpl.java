package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.PdmObjectMapper;
import com.richfit.mes.common.model.base.PdmBom;
import com.richfit.mes.common.model.base.PdmDraw;
import com.richfit.mes.common.model.base.PdmObject;
import com.richfit.mes.common.model.base.PdmProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmObjectServiceImpl extends ServiceImpl<PdmObjectMapper, PdmObject> implements PdmObjectService {

    @Autowired
    private PdmBomService pdmBomService;
    @Autowired
    private PdmDrawService pdmDrawService;
    @Autowired
    private PdmProcessService pdmProcessService;

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
    public List<PdmObject> queryIndustrialAssembly(String id, String dataGroup) {
        QueryWrapper<PdmObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("dataGroup", dataGroup);
        return this.list(queryWrapper);
    }

    @Override
    public List<PdmObject> selectFixtureList(String optId) {
        QueryWrapper<PdmObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("op_id", optId);
        return this.list(queryWrapper);
    }

    @Override
    public IPage getPdmPage(String drawNo, String type, String branchCode, int page, int limit) {
        switch (type) {
            case "draw":
                QueryWrapper<PdmDraw> drawQueryWrapper = new QueryWrapper<>();
                drawQueryWrapper.eq("datagroup", branchCode);
                if (!StringUtils.isNullOrEmpty(drawNo)) {
                    drawQueryWrapper.eq("item_id", drawNo);
                }
                Page<PdmDraw> pdmDrawPage = pdmDrawService.page(new Page<>(page, limit), drawQueryWrapper);
                for (PdmDraw pdmDraw : pdmDrawPage.getRecords()) {
                    pdmDraw.setType("图纸");
                }
                return pdmDrawPage;
            case "bom":
                QueryWrapper<PdmBom> bomQueryWrapper = new QueryWrapper<>();
                bomQueryWrapper.eq("datagroup",branchCode);
                if (!StringUtils.isNullOrEmpty(drawNo)){
                    bomQueryWrapper.eq("id",drawNo);
                }
                Page<PdmBom> pdmBomPage = pdmBomService.page(new Page<>(page, limit), bomQueryWrapper);
                for (PdmBom pdmBom : pdmBomPage.getRecords()) {
                    pdmBom.setType("BOM");
                }
                return pdmBomPage;
            case "router":
                QueryWrapper<PdmProcess> processQueryWrapper = new QueryWrapper<>();
                processQueryWrapper.eq("dataGroup",branchCode);
                if (!StringUtils.isNullOrEmpty(drawNo)){
                    processQueryWrapper.eq("draw_no",drawNo);
                }
                Page<PdmProcess> pdmProcessPage = pdmProcessService.page(new Page<>(page, limit), processQueryWrapper);
                for (PdmProcess process : pdmProcessPage.getRecords()) {
                    process.setType("工艺");
                }
                return pdmProcessPage;
        }
        return new Page<>();
    }
}
