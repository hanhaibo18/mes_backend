package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.HotPlanNode;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.HotPlanNodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Api(value = "关键工序计划节点", tags = {"关键工序计划节点"})
@RestController
@RequestMapping("/api/produce/plan_node")
public class HotPlanNodeController extends BaseController {

    @Resource
    HotPlanNodeService hotPlanNodeService;

    @ApiOperation(value = "根据需求id查询关键工序计划节点列表", notes = "根据需求id查询关键工序计划节点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "demandId", value = "需求提报Id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/get_by_demandId")
    public CommonResult getByDemandId(@RequestParam String demandId, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<HotPlanNode> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("branch_code",branchCode);
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.eq("demand_id",demandId);
        List<HotPlanNode> list = hotPlanNodeService.list(queryWrapper);
        return CommonResult.success(list);
    }

    @ApiOperation(value = "修改工序计划", notes = "修改工序计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "planNodes", value = "需求提报Id", required = true, paramType = "List")
    })
    @PostMapping("/batch_update")
    public CommonResult batchUpdate(@RequestBody List<HotPlanNode> planNodes) {
        UpdateWrapper<HotPlanNode> updateWrapper=new UpdateWrapper<>();
        for (HotPlanNode planNode : planNodes) {
            if(planNode.getFinishTime()!=null){
                updateWrapper.set("finish_time",planNode.getFinishTime());
                updateWrapper.eq("id",planNode.getId());
                hotPlanNodeService.update(updateWrapper);
            }
        }
        return CommonResult.success("操作成功");
    }



}
