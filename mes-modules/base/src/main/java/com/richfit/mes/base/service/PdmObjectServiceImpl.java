package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.PdmObjectMapper;
import com.richfit.mes.base.entity.PdmDto;
import com.richfit.mes.common.model.base.PdmBom;
import com.richfit.mes.common.model.base.PdmDraw;
import com.richfit.mes.common.model.base.PdmObject;
import com.richfit.mes.common.model.base.PdmProcess;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public IPage<PdmDto> getPdmPage(String drawNo, String type, String branchCode, int page, int limit) {
        Page<PdmDto> pdmDtoPage;
        switch (type) {
            case "draw":
                QueryWrapper<PdmDraw> drawQueryWrapper = new QueryWrapper<>();
                drawQueryWrapper.eq("datagroup", branchCode).eq("isop", "1").orderByDesc("sys_time");
                if (!StringUtils.isNullOrEmpty(drawNo)) {
                    drawQueryWrapper.eq("item_id", drawNo);
                }
                Page<PdmDraw> pdmDrawPage = pdmDrawService.page(new Page<>(page, limit), drawQueryWrapper);
                //返回值统一
                pdmDtoPage = buildTemplateForDraw(pdmDrawPage);
                return pdmDtoPage;
            case "bom":
                QueryWrapper<PdmBom> bomQueryWrapper = new QueryWrapper<>();
                bomQueryWrapper.eq("datagroup", branchCode).orderByDesc("sys_time");
                if (!StringUtils.isNullOrEmpty(drawNo)) {
                    bomQueryWrapper.eq("id", drawNo);
                }
                Page<PdmBom> pdmBomPage = pdmBomService.page(new Page<>(page, limit), bomQueryWrapper);
                pdmDtoPage = buildTemplateForBom(pdmBomPage);
                return pdmDtoPage;
            case "router":
                QueryWrapper<PdmProcess> processQueryWrapper = new QueryWrapper<>();
                processQueryWrapper.eq("dataGroup", branchCode).orderByDesc("sys_time");
                if (!StringUtils.isNullOrEmpty(drawNo)) {
                    processQueryWrapper.eq("draw_no", drawNo);
                }
                Page<PdmProcess> pdmProcessPage = pdmProcessService.page(new Page<>(page, limit), processQueryWrapper);
                //返回值统一
                pdmDtoPage = buildTemplateForProcess(pdmProcessPage);
                return pdmDtoPage;
        }
        return new Page<>();
    }

    private Page<PdmDto> buildTemplateForProcess(Page<PdmProcess> pdmProcessPage) {
        List<PdmDto> pdmDtoList = new ArrayList<>();
        if (pdmProcessPage != null) {
            for (PdmProcess process : pdmProcessPage.getRecords()) {
                PdmDto pdmDto = new PdmDto();
                pdmDto.setDrawNo(process.getDrawNo());
                pdmDto.setVer(process.getRev());
                pdmDto.setType("工艺");
                pdmDto.setName(process.getName());
                pdmDto.setFileType(process.getProcessType());
                pdmDto.setFileUrl(null);
                pdmDto.setPublisher(process.getProcessUser());
                pdmDto.setPublishTime(process.getSycTime());
                pdmDto.setDrawIdGroup(process.getDrawIdGroup());
                pdmDtoList.add(pdmDto);
            }
            Page<PdmDto> pdmDtoPage = new Page<>();
            BeanUtils.copyProperties(pdmProcessPage, pdmDtoPage);
            pdmDtoPage.setRecords(pdmDtoList);
            return pdmDtoPage;
        }
        return new Page<>();
    }

    private Page<PdmDto> buildTemplateForBom(Page<PdmBom> pdmBomPage) {
        List<PdmDto> pdmDtoList = new ArrayList<>();
        if (pdmBomPage != null) {
            for (PdmBom pdmBom : pdmBomPage.getRecords()) {
                PdmDto pdmDto = new PdmDto();
                pdmDto.setDrawNo(pdmBom.getId());
                pdmDto.setVer(pdmBom.getVer());
                pdmDto.setType("BOM");
                pdmDto.setName(pdmBom.getName());
                pdmDto.setFileType(pdmBom.getObjectType());
                pdmDto.setFileUrl(null);
                pdmDto.setPublisher(pdmBom.getItemUser());
                pdmDto.setPublishTime(pdmBom.getSycTime());
                pdmDtoList.add(pdmDto);
            }
            Page<PdmDto> pdmDtoPage = new Page<>();
            BeanUtils.copyProperties(pdmBomPage, pdmDtoPage);
            pdmDtoPage.setRecords(pdmDtoList);
            return pdmDtoPage;
        }
        return new Page<>();
    }

    private Page<PdmDto> buildTemplateForDraw(Page<PdmDraw> pdmDrawPage) {
        List<PdmDto> pdmDtoList = new ArrayList<>();
        if (pdmDrawPage != null) {
            for (PdmDraw pdmDraw : pdmDrawPage.getRecords()) {
                PdmDto pdmDto = new PdmDto();
                pdmDto.setDrawNo(pdmDraw.getItemId());
                pdmDto.setVer(pdmDraw.getItemRev());
                pdmDto.setType("图纸");
                pdmDto.setName(pdmDraw.getFileName());
                pdmDto.setFileType(pdmDraw.getFileType());
                pdmDto.setFileUrl(pdmDraw.getFileUrl());
                pdmDto.setPublisher(null);
                pdmDto.setPublishTime(pdmDraw.getSycTime());
                pdmDtoList.add(pdmDto);
            }
            Page<PdmDto> pdmDtoPage = new Page<>();
            BeanUtils.copyProperties(pdmDrawPage, pdmDtoPage);
            pdmDtoPage.setRecords(pdmDtoList);
            return pdmDtoPage;
        }
        return new Page<>();
    }
}
