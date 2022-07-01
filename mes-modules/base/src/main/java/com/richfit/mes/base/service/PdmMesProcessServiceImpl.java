package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmMesProcessMapper;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @Autowired
    public RouterService routerService;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private OperatiponService operatiponService;

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
            String routerId = UUID.randomUUID().toString().replace("-", "");
            // MES数据中工序
            QueryWrapper<PdmMesOption> queryWrapperPdmMesOption = new QueryWrapper<>();
            queryWrapperPdmMesOption.eq("process_id", pdmMesProcess.getDrawIdGroup());
            List<PdmMesOption> pdmMesOptionList = pdmMesOptionService.list(queryWrapperPdmMesOption);
            for (PdmMesOption pdmMesOption : pdmMesOptionList) {

                //工序字典
                QueryWrapper<Operatipon> queryWrapperOperatipon = new QueryWrapper<>();
                queryWrapperOperatipon.eq("opt_name", pdmMesOption.getName());
                queryWrapperOperatipon.eq("branch_code", pdmMesOption.getDataGroup());
                queryWrapperOperatipon.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                List<Operatipon> operatipons = operatiponService.list(queryWrapperOperatipon);

                //添加工序
                Sequence sequence = new Sequence();
                if (operatipons.size() > 0) {
                    //工序字典存在当前工序
                    sequence.setOptId(operatipons.get(0).getId());
                } else {
                    //工序字典不存在当前工序
                    //添加工序字典
                    String optId = UUID.randomUUID().toString().replace("-", "");
                    Operatipon operatipon = new Operatipon();
                    operatipon.setId(optId);
                    operatipon.setOptCode(pdmMesOption.getName());
                    operatipon.setOptName(pdmMesOption.getName());
                    operatipon.setBranchCode(pdmMesOption.getDataGroup());
                    operatipon.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                    operatipon.setCreateTime(new Date());
                    operatipon.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    operatipon.setModifyTime(new Date());
                    operatipon.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    operatiponService.save(operatipon);
                    sequence.setOptId(optId);
                }
                sequence.setRouterId(routerId);
                sequence.setId(UUID.randomUUID().toString().replace("-", ""));
                sequence.setOptOrder(Integer.parseInt(pdmMesOption.getOpNo()));
                sequence.setOp_no(pdmMesOption.getOpNo());
                sequence.setType(pdmMesOption.getType());
                sequence.setOptName(pdmMesOption.getName());
                sequence.setContent(pdmMesOption.getContent());
                sequence.setGzs(pdmMesOption.getGzs());
                sequence.setDrawing(pdmMesOption.getDrawing());
                sequence.setVersionCode(pdmMesOption.getRev());
                sequence.setBranchCode(pdmMesOption.getDataGroup());
                sequence.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                sequence.setCreateTime(new Date());
                sequence.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                sequence.setModifyTime(new Date());
                sequence.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                sequenceService.save(sequence);

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

            //更新老工艺状态模块
            Router router = new Router();
            router.setIsActive("0");
            router.setStatus("2");
            QueryWrapper<Router> queryWrapperRouter = new QueryWrapper<Router>();
            queryWrapperRouter.eq("router_no", pdmMesProcess.getDrawNo());
            queryWrapperRouter.eq("branch_code", pdmMesProcess.getDataGroup());
            queryWrapperRouter.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            routerService.update(router, queryWrapperRouter);
            //添加新工艺模块
            router.setId(routerId);
            router.setVersion(pdmMesProcess.getRev());
            router.setRouterName(pdmMesProcess.getName());
            router.setRouterNo(pdmMesProcess.getDrawNo());
            router.setRemark(pdmMesProcess.getDrawNo());
            router.setBranchCode(pdmMesProcess.getDataGroup());
            router.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            router.setCreateTime(new Date());
            router.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            router.setModifyTime(new Date());
            router.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            router.setStatus("1");
            router.setIsActive("1");
            routerService.save(router);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("同步MES出现异常");
        }
    }
}
