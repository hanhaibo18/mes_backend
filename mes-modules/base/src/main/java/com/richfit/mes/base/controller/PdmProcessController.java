package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.base.service.*;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 13:27
 */
@Slf4j
@Api(value = "PDM工艺接收", tags = {"PDM工艺接收"})
@RestController
@RequestMapping("/api/base/pdmProcess")
public class PdmProcessController {

    @Autowired
    private PdmProcessService pdmProcessService;
    @Autowired
    private PdmBomService pdmBomService;
    @Autowired
    private PdmObjectService pdmObjectService;
    @Autowired
    private PdmOptionService pdmOptionService;
    @Autowired
    private PdmDrawService pdmDrawService;
    @Autowired
    public RouterService routerService;
    @Autowired
    public SequenceService sequenceService;
    @Autowired
    public OperatiponService OperationService;

    @Autowired
    private PdmMesProcessService pdmMesProcessService;

    @PostMapping(value = "/query/list")
    @ApiOperation(value = "工艺查询", notes = "工艺查询")
    @ApiImplicitParam(name = "pdmProcess", value = "工艺VO", required = true, dataType = "PdmProcess", paramType = "body")
    public CommonResult<List<PdmProcess>> getList(PdmProcess pdmProcess) {
        List<PdmProcess> list = pdmProcessService.queryList(pdmProcess);
        return CommonResult.success(list);
    }

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工艺分页查询", notes = "工艺分页查询")
    @ApiImplicitParam(name = "pdmProcess", value = "工艺VO", required = true, dataType = "PdmProcess", paramType = "body")
    public CommonResult<IPage<PdmProcess>> getPageList(int page, int limit, PdmProcess pdmProcess) {
        return CommonResult.success(pdmProcessService.queryPageList(page, limit, pdmProcess));
    }

    @PostMapping("/delete/pdm_process")
    @ApiOperation(value = "删除工艺", notes = "删除工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawIdGroup", value = "工艺id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dataGroup", value = "工厂代码", required = true, paramType = "query", dataType = "string")
    })
    public CommonResult deletedPDMProcess(String drawIdGroup,String dataGroup) {
        return CommonResult.success(pdmProcessService.deletePDMProcess(drawIdGroup,dataGroup));
    }


    @PostMapping("/synctomes")
    @ApiOperation(value = "同步到MES", notes = "同步到MES")
    public void synctomes(@ApiParam(value = "同步的工艺列表") @RequestBody List<PdmProcess> pdmProcesses) throws Exception {
        for (PdmProcess p : pdmProcesses) {
            pdmProcessService.synctomes(p);
        }
    }

    @PostMapping("/synctomes1")
    @ApiOperation(value = "同步到MES", notes = "同步到MES")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Router> synctomes1(String processId) {

        PdmProcess pdmProcess = pdmProcessService.getOne(new QueryWrapper<PdmProcess>().eq("id", processId));
        // 获取工序
        List<PdmOption> pdmOptions = pdmOptionService.list(new QueryWrapper<PdmOption>().like("process_id", "%" + processId + "%").orderByAsc("op_no"));
        // 获取图纸
        List<PdmDraw> pdmDraws = pdmDrawService.list(new QueryWrapper<PdmDraw>().eq("item_id", pdmProcess.getDrawNo()));
        for (int i = 0; i < pdmOptions.size(); i++) {
            //  List<PdmObject> pdmObjects = pdmObjectService.list(new QueryWrapper<PdmObject>().eq("op_id", pdmOptions.get(i)));
        }
        // 获取MES工艺
        List<Router> routers = routerService.list(new QueryWrapper<Router>().eq("router_no", pdmProcess.getDrawNo()).eq("is_active", "1").like("branch_code", "%" + pdmProcess.getDataGroup() + "%"));
        // 如果存在相同版本的工艺，则将工艺改为历史版本
        boolean isAxistVer = false;
        Router r = new Router();
        for (int i = 0; i < routers.size(); i++) {
            if (routers.get(i).getVersion().equals(pdmProcess.getRev())) {
                isAxistVer = true;
                r = routers.get(i);
                r.setStatus("1");
                r.setIsActive(("1"));
                r.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                routerService.updateById(r);

            } else {
                r = routers.get(i);
                r.setStatus("2");
                r.setIsActive(("0"));
                r.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                routerService.updateById(r);
            }
        }
        if (routers.size() == 0) {
            // 新增工艺
            r = new Router();
            r.setId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
            r.setRouterNo(pdmProcess.getDrawNo());
            r.setRouterName(pdmProcess.getName());
            r.setVersion(pdmProcess.getRev());
            r.setBranchCode(pdmProcess.getDataGroup());
            if (pdmProcess.getProcessType().contains("机加")) {
                r.setType("1");
            } else if (pdmProcess.getProcessType().contains("装配")) {
                r.setType("2");
            } else {
                r.setType("0");
            }
            r.setStatus("1");
            r.setIsActive("1");
            routerService.save(r);
        }
        // 获取插入的工艺
        //List<Router> newrouters = routerService.list(new QueryWrapper<Router>().eq("router_no", pdmProcess.getDrawNo()).eq("status", "1").eq("is_active", "1").eq("version", pdmProcess.getRev()).like("branch_code", "%" + pdmProcess.getDataGroup() + "%"));
        //r = new newrouters.get(0);
        // 写入工序
        List<Sequence> sequenceList = sequenceService.list(new QueryWrapper<Sequence>().eq("router_id", r.getId()));
        for (int i = 0; i < sequenceList.size(); i++) {
            sequenceService.removeById(sequenceList.get(i).getId());
        }
        for (int i = 0; i < pdmOptions.size(); i++) {
            List<Operatipon> opts = OperationService.list(new QueryWrapper<Operatipon>().eq("opt_name", pdmOptions.get(i).getName()).eq("branch_code", pdmOptions.get(i).getDataGroup()));
            Operatipon opt = new Operatipon();
            if (opts.size() == 0) {

                opt.setId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
                opt.setOptCode(pdmOptions.get(i).getId());
                opt.setBranchCode(pdmOptions.get(i).getDataGroup());
                opt.setStatus(1);
                opt.setOptName(pdmOptions.get(i).getName());
                opt.setOptOrder(i + 1);
                if (pdmOptions.get(i).getType().contains("机加")) {
                    opt.setOptType(0);
                } else if (pdmOptions.get(i).getType().contains("装配")) {
                    opt.setOptType(2);
                } else {
                    opt.setOptType(0);
                }
                opt.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                OperationService.save(opt);

            } else {
                opt = opts.get(0);
            }
            Sequence sequence = new Sequence();
            sequence.setBranchCode(opt.getBranchCode());
            sequence.setOptCode(opt.getOptCode());
            sequence.setIsParallel("0");
            sequence.setOptOrder(Integer.parseInt(pdmOptions.get(i).getOpNo()));
            sequence.setOptName(opt.getOptName());
            sequence.setOptId(opt.getId());
            sequence.setOptNextOrder(i + 2);
            sequence.setOptType(String.valueOf(opt.getOptType()));
            sequence.setRouterId(r.getId());
            sequence.setStatus("1");
            sequence.setTenantId(r.getTenantId());
            sequence.setRemark(pdmOptions.get(i).getContent());
            sequence.setTechnologySequence(pdmOptions.get(i).getOpNo());

            sequenceService.save(sequence);
        }
        return CommonResult.success(r);
    }
}
