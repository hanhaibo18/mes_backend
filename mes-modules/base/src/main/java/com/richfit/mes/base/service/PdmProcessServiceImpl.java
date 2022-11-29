package com.richfit.mes.base.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmObjectMapper;
import com.richfit.mes.base.dao.PdmOptionMapper;
import com.richfit.mes.base.dao.PdmProcessMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmProcessServiceImpl extends ServiceImpl<PdmProcessMapper, PdmProcess> implements PdmProcessService {


    @Autowired
    private PdmProcessMapper pdmProcessMapper;

    @Autowired
    private PdmOptionService pdmOptionService;

    @Autowired
    private PdmObjectService pdmObjectService;

    @Autowired
    private PdmDrawService pdmDrawService;

    @Autowired
    private PdmBomService pdmBomService;

    @Autowired
    private PdmMesProcessService pdmMesProcessService;

    @Autowired
    private PdmMesOptionService pdmMesOptionService;

    @Autowired
    private PdmMesObjectService pdmMesObjectService;

    @Autowired
    private PdmMesDrawService pdmMesDrawService;

    @Autowired
    private PdmMesBomService pdmMesBomService;
    @Autowired
    private PdmOptionMapper pdmOptionMapper;
    @Autowired
    private PdmObjectMapper pdmObjectMapper;


    @Override
    public IPage<PdmProcess> queryPageList(int page, int limit, PdmProcess pdmProcess) {
        Page<PdmProcess> ipage = new Page<>(page, limit);
        return pdmProcessMapper.queryPageList(ipage, pdmProcess);
    }

    @Override
    public List<PdmProcess> queryList(PdmProcess pdmProcess) {
        return pdmProcessMapper.queryList(pdmProcess);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synctomes(PdmProcess pdmProcess) throws Exception {
        try {
            // 查询要同步的工艺，修改工艺状态
            pdmProcess.setItemStatus("已同步");

            // 删除MES数据中工序
            QueryWrapper<PdmMesOption> queryWrapperPdmMesOption = new QueryWrapper<>();
            queryWrapperPdmMesOption.eq("process_id", pdmProcess.getDrawIdGroup());
            pdmMesOptionService.remove(queryWrapperPdmMesOption);

            // 查询并保存工序到MES
            QueryWrapper<PdmOption> queryWrapperPdmOption = new QueryWrapper<>();
            queryWrapperPdmOption.eq("process_id", pdmProcess.getDrawIdGroup());
            List<PdmOption> PdmOptionList = pdmOptionService.list(queryWrapperPdmOption);
            for (PdmOption pdmOption : PdmOptionList) {
                // 删除MES数据中工序的工装
                QueryWrapper<PdmMesObject> queryWrapperPdmMesObject = new QueryWrapper<>();
                queryWrapperPdmMesObject.eq("op_id", pdmOption.getId());
                pdmMesObjectService.remove(queryWrapperPdmMesObject);
                // 查询并保存工序的工装到MES
                QueryWrapper<PdmObject> queryWrapperPdmObject = new QueryWrapper<>();
                queryWrapperPdmObject.eq("op_id", pdmOption.getId());
                List<PdmObject> pdmObjectList = pdmObjectService.list(queryWrapperPdmObject);
                for (PdmObject pdmObject : pdmObjectList) {
                    PdmMesObject pdmMesOption = JSON.parseObject(JSON.toJSONString(pdmObject), PdmMesObject.class);
                    pdmMesObjectService.save(pdmMesOption);
                }
                // 删除MES数据中工序的图纸
                QueryWrapper<PdmMesDraw> queryWrapperPdmMesDraw = new QueryWrapper<>();
                queryWrapperPdmMesDraw.eq("op_id", pdmOption.getId());
                pdmMesDrawService.remove(queryWrapperPdmMesDraw);
                // 查询并保存工序的图纸到MES
                QueryWrapper<PdmDraw> queryWrapperPdmDraw = new QueryWrapper<>();
                queryWrapperPdmDraw.eq("op_id", pdmOption.getId());
                List<PdmDraw> pdmDrawList = pdmDrawService.list(queryWrapperPdmDraw);
                for (PdmDraw pdmDraw : pdmDrawList) {
                    PdmMesDraw pdmMesOption = JSON.parseObject(JSON.toJSONString(pdmDraw), PdmMesDraw.class);
                    pdmMesDrawService.save(pdmMesOption);
                }
                // 保存工序到MES
                PdmMesOption pdmMesOption = JSON.parseObject(JSON.toJSONString(pdmOption), PdmMesOption.class);
                pdmMesOptionService.save(pdmMesOption);
            }

            //删除图纸
            QueryWrapper<PdmMesDraw> queryWrapperPdmMesDraw = new QueryWrapper<>();
            queryWrapperPdmMesDraw.eq("isop", '1');
            queryWrapperPdmMesDraw.and(wrapper -> wrapper.eq("op_id", pdmProcess.getDrawIdGroup()).or().eq("op_id", pdmProcess.getDrawNo() + "@" + pdmProcess.getDrawNo() + "@" + pdmProcess.getDataGroup()));
            queryWrapperPdmMesDraw.eq("dataGroup", pdmProcess.getDataGroup());
            pdmMesDrawService.remove(queryWrapperPdmMesDraw);
            // 查询并保存图纸到MES
            QueryWrapper<PdmDraw> queryWrapperPdmDraw = new QueryWrapper<>();
            queryWrapperPdmDraw.eq("isop", '1');
            queryWrapperPdmDraw.and(wrapper -> wrapper.eq("op_id", pdmProcess.getDrawIdGroup()).or().eq("op_id", pdmProcess.getDrawNo() + "@" + pdmProcess.getDrawNo() + "@" + pdmProcess.getDataGroup()));
            queryWrapperPdmDraw.eq("dataGroup", pdmProcess.getDataGroup());
            List<PdmDraw> pdmDrawList = pdmDrawService.list(queryWrapperPdmDraw);
            for (PdmDraw pdmDraw : pdmDrawList) {
                PdmMesDraw pdmMesOption = JSON.parseObject(JSON.toJSONString(pdmDraw), PdmMesDraw.class);
                pdmMesDrawService.save(pdmMesOption);
            }

            //全量保存更新BOM(暂时不保存，使用pdm i bom)
//            PdmBom bom = pdmBomService.getBomByProcessIdAndRev(pdmProcess.getDrawNo(), pdmProcess.getRev());
//            if (bom != null) {
//                PdmMesBom pdmMesBom = JSON.parseObject(JSON.toJSONString(bom), PdmMesBom.class);
//                if (!StringUtils.isNullOrEmpty(pdmMesBom.getBomId())) {
//                    pdmMesBomService.saveOrUpdate(pdmMesBom);
//                    getBomList(bom.getChildBom());
//                }
//            }

            // 保存&更新MES工艺，并更新工艺接收状态
            PdmMesProcess pdmMesProcess = JSON.parseObject(JSON.toJSONString(pdmProcess), PdmMesProcess.class);
            pdmMesProcess.setItemStatus("待发布");
            pdmMesProcess.setModifyTime(new Date());
            pdmMesProcess.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            pdmMesProcessService.saveOrUpdate(pdmMesProcess);
            pdmProcessMapper.updateById(pdmProcess);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("同步MES出现异常");
        }
    }

    @Override
    public CommonResult deletePDMProcess(String drawIdGroup, String dataGroup) {
        //删除工艺
        QueryWrapper<PdmProcess> processWrapper=new QueryWrapper<>();
        processWrapper.eq("draw_id_group",drawIdGroup);
        processWrapper.eq("dataGroup",dataGroup);
        //pdmProcessMapper.delete(processWrapper);

        //删除当前工艺关联的工序
        QueryWrapper<PdmOption> optionWrapper=new QueryWrapper<>();
        optionWrapper.eq("process_id",drawIdGroup);
        processWrapper.eq("dataGroup",dataGroup);
        List<PdmOption> pdmOptions = pdmOptionMapper.selectList(optionWrapper);
        //工序id
        List<String> optionsId = pdmOptions.stream().map(x -> x.getId()).collect(Collectors.toList());
        //pdmOptionMapper.delete(optionWrapper);

        //删除工序工装信息
        QueryWrapper<PdmObject> objectWrapper=new QueryWrapper<>();
        objectWrapper.eq("dataGroup",dataGroup);
        objectWrapper.in("op_id",optionsId);
        List<PdmObject> pdmObjects = pdmObjectMapper.selectList(objectWrapper);
        List<String> objectsId = pdmObjects.stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
        //pdmObjectMapper.delete(objectWrapper);

        //删除图
        QueryWrapper<PdmDraw> drawWrapper=new QueryWrapper<>();
        drawWrapper.eq("datagroup",dataGroup);
        drawWrapper.in("op_id",drawIdGroup);
        //pdmDrawService.remove(drawWrapper);

        //删除bom
        QueryWrapper<PdmBom> bomWrapper=new QueryWrapper<>();
        bomWrapper.eq("datagroup",dataGroup);
        bomWrapper.in("id",objectsId);
        //pdmBomService.remove(bomWrapper);

        return  CommonResult.success(ResultCode.SUCCESS);
    }

    //递归函数
    public void getBomList(List<PdmBom> pdmBoms) {
        for (PdmBom bom : pdmBoms) {
            PdmMesBom pdmMesBom = JSON.parseObject(JSON.toJSONString(bom), PdmMesBom.class);
            pdmMesBomService.saveOrUpdate(pdmMesBom);
            if (bom.getChildBom().size() > 0) {
                getBomList(bom.getChildBom());
            }
        }
    }
}
