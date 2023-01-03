package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.*;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Service
@Transactional(rollbackFor = Exception.class)
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

    @Autowired
    private OperationTypeSpecService operatiponTypeSpecService;

    @Autowired
    private RouterCheckService routerCheckService;
    @Autowired
    private PdmMesOptionMapper pdmMesOptionMapper;
    @Autowired
    private PdmMesBomService pdmMesBomService;
    @Autowired
    private PdmMesObjectMapper pdmMesObjectMapper;
    @Autowired
    PdmMesDrawMapper pdmMesDrawMapper;
    @Autowired
    private PdmMesBomMapper pdmMesBomMapper;

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
    public CommonResult<PdmMesProcess> release(PdmMesProcess pdmMesProcess) throws Exception {
        //校验 如果存在图号+版本号+类型的工艺 跳过发布
        QueryWrapper<Router> routerQueryWrapper = new QueryWrapper<>();
        routerQueryWrapper.eq("router_no", pdmMesProcess.getDrawNo())
                .eq("version", pdmMesProcess.getRev())
                .eq("router_type", pdmMesProcess.getProcessType())
                .eq("branch_code", pdmMesProcess.getDataGroup());
        List<Router> list = routerService.list(routerQueryWrapper);
        //存在的话跳过发布
        if (list.size() > 0) {
            pdmMesProcess.setItemStatus("已发布");
            pdmMesProcessMapper.updateById(pdmMesProcess);
            return CommonResult.success(pdmMesProcess, "当前工艺已发布，该工艺将不进行发布，修改状态改为以发布");
        }
        //不存在的话发布新版本
        String message = "发布成功";
        try {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            String routerId = UUID.randomUUID().toString();
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
                //工序的id
                String sequenceId = UUID.randomUUID().toString().replace("-", "");
                //添加工序
                Sequence sequence = new Sequence();
                if (operatipons.size() > 0) {
                    //工序字典存在当前工序
                    sequence.setOptId(operatipons.get(0).getId());
                    sequence.setOptCode(operatipons.get(0).getOptCode());
                } else {
                    //工序字典不存在当前工序
                    //添加工序字典
                    String optId = UUID.randomUUID().toString().replace("-", "");
                    Operatipon operatipon = new Operatipon();
                    operatipon.setId(optId);
                    operatipon.setOptCode(pdmMesOption.getName());
                    operatipon.setOptName(pdmMesOption.getName());
                    operatipon.setOptType(0);

                    operatipon.setBranchCode(pdmMesOption.getDataGroup());
                    operatipon.setTenantId(user.getTenantId());
                    operatipon.setCreateTime(new Date());
                    operatipon.setCreateBy(user.getUsername());
                    operatipon.setModifyTime(new Date());
                    operatipon.setModifyBy(user.getUsername());
                    operatiponService.save(operatipon);
                    sequence.setOptId(optId);
                    sequence.setOptCode(operatipon.getOptCode());
                }
                sequence.setRouterId(routerId);
                sequence.setId(sequenceId);
                //关联工装、图纸的id
                sequence.setPdmMesOptionId(pdmMesOption.getId());
                sequence.setOptOrder(Integer.parseInt(pdmMesOption.getOpNo()));
                sequence.setOpNo(pdmMesOption.getOpNo());
                sequence.setType(pdmMesOption.getType());

                sequence.setOptName(pdmMesOption.getName());
                sequence.setContent(pdmMesOption.getContent());
                sequence.setGzs(pdmMesOption.getGzs());
                sequence.setDrawing(pdmMesOption.getDrawing());
                sequence.setVersionCode(pdmMesOption.getRev());
                //工序类型
                sequence.setOptType("0");
                //质检确认
                sequence.setIsQualityCheck("1");
                //调度确认
                sequence.setIsScheduleCheck("1");
                //是否并行
                sequence.setIsParallel("0");
                //自动派工
                sequence.setIsAutoAssign("0");
                sequence.setBranchCode(pdmMesOption.getDataGroup());
                sequence.setTenantId(user.getTenantId());
                sequence.setCreateTime(new Date());
                sequence.setCreateBy(user.getUsername());
                sequence.setModifyTime(new Date());
                sequence.setModifyBy(user.getUsername());
                sequenceService.saveOrUpdate(sequence);

//                //删除工序已关联的质量资料历史数据
//                QueryWrapper<RouterCheck> queryWrapperRouterCheck = new QueryWrapper<>();
//                queryWrapperRouterCheck.eq("sequence_id", sequence.getId());
//                queryWrapperRouterCheck.eq("type", "质量资料");
//                queryWrapperRouterCheck.eq("branch_code", sequence.getBranchCode());
//                queryWrapperRouterCheck.eq("tenant_id", user.getTenantId());
//                routerCheckService.remove(queryWrapperRouterCheck);
//
//                //工序质量资料
//                if (!StringUtils.isNullOrEmpty(sequence.getOptType())) {
//                    //查询类型关联的质量资料
//                    QueryWrapper<OperationTypeSpec> queryWrapperOperationTypeSpec = new QueryWrapper<OperationTypeSpec>();
//                    queryWrapperOperationTypeSpec.eq("opt_type", sequence.getOptType());
//                    queryWrapperOperationTypeSpec.eq("branch_code", sequence.getBranchCode());
//                    queryWrapperOperationTypeSpec.eq("tenant_id", user.getTenantId());
//                    List<OperationTypeSpec> operationTypeSpecs = operatiponTypeSpecService.list(queryWrapperOperationTypeSpec);
//                    for (OperationTypeSpec dts : operationTypeSpecs) {
//                        RouterCheck routerCheck = new RouterCheck();
//                        routerCheck.setId(UUID.randomUUID().toString().replaceAll("-", ""));
//                        routerCheck.setSequenceId(sequence.getId());
//                        routerCheck.setRouterId(sequence.getRouterId());
//                        routerCheck.setName(dts.getPropertyName());
//                        routerCheck.setType("质量资料");
//                        routerCheck.setStatus("1");
//                        routerCheck.setDefualtValue(dts.getPropertyValue());
//                        routerCheck.setPropertyObjectname(dts.getPropertyName());
//
//                        routerCheck.setBranchCode(sequence.getBranchCode());
//                        routerCheck.setTenantId(user.getTenantId());
//                        routerCheck.setCreateTime(new Date());
//                        routerCheck.setCreateBy(user.getUsername());
//                        routerCheck.setModifyTime(new Date());
//                        routerCheck.setModifyBy(user.getUsername());
//                        routerCheckService.save(routerCheck);
//                    }
//                }

                // MES数据中工序的工装
//                    QueryWrapper<PdmMesObject> queryWrapperPdmMesObject = new QueryWrapper<>();
//                    queryWrapperPdmMesObject.eq("op_id", pdmMesOption.getId());
//                    List<PdmMesObject> pdmMesObjectList = pdmMesObjectService.list(queryWrapperPdmMesObject);

                // MES数据中工序的图纸
//                    QueryWrapper<PdmMesDraw> queryWrapperPdmMesDraw = new QueryWrapper<>();
//                    queryWrapperPdmMesDraw.eq("op_id", pdmMesOption.getId());
//                    List<PdmMesDraw> pdmMesDrawList = pdmMesDrawService.list(queryWrapperPdmMesDraw);
            }
            //图纸
//                QueryWrapper<PdmMesDraw> queryWrapperPdmMesDraw = new QueryWrapper<>();
//                queryWrapperPdmMesDraw.eq("isop", '1');
//                queryWrapperPdmMesDraw.and(wrapper -> wrapper.eq("op_id", pdmMesProcess.getDrawIdGroup()).or().eq("op_id", pdmMesProcess.getDrawNo() + "@" + pdmMesProcess.getDrawNo() + "@" + pdmMesProcess.getDataGroup()));
//                queryWrapperPdmMesDraw.eq("dataGroup", pdmMesProcess.getDataGroup());
//                List<PdmMesDraw> pdmMesDrawList = pdmMesDrawService.list(queryWrapperPdmMesDraw);
            // 保存&更新MES工艺，并更新工艺接收状态
            pdmMesProcess.setItemStatus("已发布");
            pdmMesProcess.setModifyTime(new Date());
            pdmMesProcess.setModifyBy(user.getUsername());
            // 特别添加部分pdm图号两边带有空格的问题
            pdmMesProcess.setDrawNo(pdmMesProcess.getDrawNo().trim());
            pdmMesProcessMapper.updateById(pdmMesProcess);

            //更新老工艺状态模块
            Router router = new Router();
            router.setIsActive("0");
            router.setStatus("2");
            QueryWrapper<Router> queryWrapperRouter = new QueryWrapper<Router>();
            queryWrapperRouter.eq("status", "1");
            queryWrapperRouter.eq("router_no", pdmMesProcess.getDrawNo());
            queryWrapperRouter.eq("branch_code", pdmMesProcess.getDataGroup());
            queryWrapperRouter.eq("tenant_id", user.getTenantId());
            //工艺唯一  工艺类型+图号（历史数据）
            queryWrapperRouter.eq("router_type", pdmMesProcess.getProcessType());
            routerService.update(router, queryWrapperRouter);
            //添加新工艺模块
            router.setId(routerId);
            router.setPdmDrawIdGroup(pdmMesProcess.getDrawIdGroup());
            router.setVersion(pdmMesProcess.getRev());
            router.setRouterName(pdmMesProcess.getName());
            router.setRouterNo(pdmMesProcess.getDrawNo());
            router.setRouterType(pdmMesProcess.getProcessType());
            router.setDrawNo(pdmMesProcess.getDrawNo());
            router.setRemark(pdmMesProcess.getDrawNo());
            router.setBranchCode(pdmMesProcess.getDataGroup());
            router.setTenantId(user.getTenantId());
            router.setCreateTime(new Date());
            router.setCreateBy(user.getUsername());
            router.setModifyTime(new Date());
            router.setModifyBy(user.getUsername());
            router.setStatus("1");
            router.setIsActive("1");
            routerService.saveOrUpdate(router);
        } catch (Exception e) {
            e.printStackTrace();
            message = "同步MES出现异常:" + e.getMessage();
            throw new GlobalException("同步MES出现异常", ResultCode.FAILED);
        } finally {
            return CommonResult.success(pdmMesProcess, message);
        }
    }

    @Override
    public CommonResult deleteMesPDMProcess(List<String> drawIdGroup, String dataGroup) {
        //删除工艺
        QueryWrapper<PdmMesProcess> processWrapper = new QueryWrapper<>();
        processWrapper.in("draw_id_group", drawIdGroup);
        processWrapper.eq("dataGroup", dataGroup);
        PdmMesProcess pdmMesProcess = pdmMesProcessMapper.selectOne(processWrapper);
        pdmMesProcessMapper.delete(processWrapper);

        //删除当前工艺关联的工序
        QueryWrapper<PdmMesOption> optionWrapper = new QueryWrapper<>();
        optionWrapper.in("process_id", drawIdGroup);
        optionWrapper.eq("dataGroup", dataGroup);
        List<PdmMesOption> pdmMesOptions = pdmMesOptionMapper.selectList(optionWrapper);
        //工序id
        List<String> optionsId = pdmMesOptions.stream().map(x -> x.getId()).collect(Collectors.toList());
        pdmMesOptionMapper.delete(optionWrapper);

        //删除工艺图纸
        QueryWrapper<PdmMesDraw> drawWrapper = new QueryWrapper<>();
        drawWrapper.eq("isop", '1');
        drawWrapper.and(wrapper -> wrapper.eq("op_id", pdmMesProcess.getDrawIdGroup()).or().eq("op_id", pdmMesProcess.getDrawNo() + "@" + pdmMesProcess.getDrawNo() + "@" + pdmMesProcess.getDataGroup()));
        drawWrapper.eq("datagroup", dataGroup);
        //List<PdmMesDraw> pdmMesDraws = pdmMesDrawMapper.selectList(drawWrapper);
        //pdmMesDrawService.remove(drawWrapper);


        //删除工艺bom
        QueryWrapper<PdmMesBom> bomWrapper = new QueryWrapper<>();
        bomWrapper.eq("datagroup", dataGroup);
        bomWrapper.in("id", pdmMesProcess.getDrawNo());
        //List<PdmMesBom> pdmMesBoms = pdmMesBomMapper.selectList(bomWrapper);
        //pdmMesBomService.remove(bomWrapper);


        //删除工序工装信息
        QueryWrapper<PdmMesObject> objectWrapper = new QueryWrapper<>();
        objectWrapper.eq("dataGroup", dataGroup);
        objectWrapper.in("op_id", optionsId);
        //List<PdmMesObject> pdmMesObjects = pdmMesObjectMapper.selectList(objectWrapper);
        pdmMesObjectMapper.delete(objectWrapper);

        //删除工序图纸
        QueryWrapper<PdmMesDraw> drawWrapperTwo = new QueryWrapper<>();
        drawWrapperTwo.eq("datagroup", dataGroup);
        drawWrapperTwo.in("op_id", optionsId);
        //List<PdmMesDraw> pdmMesDraws1 = pdmMesDrawMapper.selectList(drawWrapperTwo);
        //pdmMesDrawService.remove(drawWrapperTwo);
        return CommonResult.success(ResultCode.SUCCESS);
    }
}
