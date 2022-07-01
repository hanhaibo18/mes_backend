package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmMesProcessMapper;
import com.richfit.mes.common.model.base.PdmMesDraw;
import com.richfit.mes.common.model.base.PdmMesObject;
import com.richfit.mes.common.model.base.PdmMesOption;
import com.richfit.mes.common.model.base.PdmMesProcess;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Service
public class PdmMesProcessServiceImpl extends ServiceImpl<PdmMesProcessMapper, PdmMesProcess> implements PdmMesProcessService {

    @Autowired
    private PdmMesProcessMapper pdmMesProcessMapper;

    @Autowired
    private PdmMesOptionService pdmMesOptionService;

    @Autowired
    private PdmMesObjectService pdmMesObjectService;

    @Autowired
    private PdmMesDrawService pdmMesDrawService;

    @Override
    public IPage<PdmMesProcess> queryPageList(int page, int limit, PdmMesProcess pdmProcess) {
        Page<PdmMesProcess> ipage = new Page<>(page, limit);
        return pdmMesProcessMapper.queryPageList(ipage, pdmProcess);
    }

    @Override
    public List<PdmMesProcess> queryList(PdmMesProcess pdmProcess) {
        return pdmMesProcessMapper.queryList(pdmProcess);
    }

    @Override
    public void release(PdmMesProcess pdmMesProcess) throws Exception {
        try {
            // MES数据中工序
            QueryWrapper<PdmMesOption> queryWrapperPdmMesOption = new QueryWrapper<>();
            queryWrapperPdmMesOption.eq("process_id", pdmMesProcess.getDrawIdGroup());
            List<PdmMesOption> pdmMesOptionList = pdmMesOptionService.list(queryWrapperPdmMesOption);
            for (PdmMesOption pdmMesOption : pdmMesOptionList) {
                // MES数据中工序的工装
                QueryWrapper<PdmMesObject> queryWrapperPdmMesObject = new QueryWrapper<>();
                queryWrapperPdmMesObject.eq("op_id", pdmMesOption.getId());
                List<PdmMesObject> pdmMesObjectList = pdmMesObjectService.list(queryWrapperPdmMesObject);

                // MES数据中工序的图纸
                QueryWrapper<PdmMesDraw> queryWrapperPdmMesDraw = new QueryWrapper<>();
                queryWrapperPdmMesDraw.eq("op_id", pdmMesOption.getId());
                List<PdmMesDraw> pdmMesDrawList = pdmMesDrawService.list(queryWrapperPdmMesDraw);
            }
            //图纸
            QueryWrapper<PdmMesDraw> queryWrapperPdmMesDraw = new QueryWrapper<>();
            queryWrapperPdmMesDraw.eq("isop", '1');
            queryWrapperPdmMesDraw.and(wrapper -> wrapper.eq("op_id", pdmMesProcess.getDrawIdGroup()).or().eq("op_id", pdmMesProcess.getDrawNo() + "@" + pdmMesProcess.getDrawNo() + "@" + pdmMesProcess.getDataGroup()));
            queryWrapperPdmMesDraw.eq("dataGroup", pdmMesProcess.getDataGroup());
            List<PdmMesDraw> pdmMesDrawList = pdmMesDrawService.list(queryWrapperPdmMesDraw);

            // 保存&更新MES工艺，并更新工艺接收状态
            pdmMesProcess.setItemStatus("已发布");
            pdmMesProcess.setModifyTime(new Date());
            pdmMesProcess.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            pdmMesProcessMapper.updateById(pdmMesProcess);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("同步MES出现异常");
        }
    }
}
