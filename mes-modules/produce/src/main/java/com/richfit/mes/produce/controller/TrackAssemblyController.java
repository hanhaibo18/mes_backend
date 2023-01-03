package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackAssemblyBinding;
import com.richfit.mes.produce.entity.AdditionalMaterialDto;
import com.richfit.mes.produce.entity.AssembleKittingVo;
import com.richfit.mes.produce.entity.BindingDto;
import com.richfit.mes.produce.entity.NonKeyDto;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 马峰
 * @Description 跟单产品装配Controller
 */
@Slf4j
@Api(value = "跟单产品装配", tags = {"装配相关"})
@RestController
@RequestMapping("/api/produce/trackassembly")
public class TrackAssemblyController extends BaseController {

    @Autowired
    public TrackAssemblyService trackAssemblyService;
    @Autowired
    public TrackAssignService trackAssignService;
    @Autowired
    public TrackItemService trackItemService;
    @Autowired
    public TrackHeadService trackHeadService;

    @Resource
    private TrackAssemblyBindingService assemblyBingService;


    @ApiOperation(value = "新增绑定信息(新)", notes = "新增绑定信息(新)")
    @PostMapping("/saveAssemblyBinding")
    public CommonResult<Boolean> saveAssemblyBinding(@RequestBody TrackAssemblyBinding assembly) {
        return assemblyBingService.saveAssemblyBinding(assembly);
    }

    @ApiOperation(value = "绑定(新)", notes = "绑定(新)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "isBinding", value = "是否绑定", required = true, dataType = "String", paramType = "query"),
    })
    @PutMapping("/updateBinding")
    public CommonResult<Boolean> updateBinding(@RequestBody BindingDto bindingDto) {
        return assemblyBingService.updateBinding(bindingDto.getId(), bindingDto.getIsBinding(), bindingDto.getItemId());
    }

    @ApiOperation(value = "删除(新)", notes = "删除(新)")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query")
    @DeleteMapping("/deleteAssemblyBinding")
    public CommonResult<Boolean> deleteAssemblyBinding(String id) {
        return assemblyBingService.deleteAssemblyBinding(id);
    }

    @ApiOperation(value = "查询绑定详情(新)", notes = "查询绑定详情(新)")
    @ApiImplicitParam(name = "assemblyId", value = "assemblyId", required = true, dataType = "String", paramType = "query")
    @GetMapping("/queryAssemblyBindingList")
    public CommonResult<List<TrackAssemblyBinding>> queryAssemblyBindingList(String assemblyId) {
        return CommonResult.success(assemblyBingService.queryAssemblyBindingList(assemblyId));
    }

    @ApiOperation(value = "分页查询绑定信息(新)", notes = "分页查询绑定信息(新)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "trackHeadId", value = "跟单Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "车间Code", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("/queryTrackAssemblyPage")
    public CommonResult<IPage<TrackAssembly>> queryTrackAssemblyPage(Long page, Long limit, String trackHeadId, String branchCode, String order, String orderCol) {
        return CommonResult.success(trackAssemblyService.queryTrackAssemblyPage(new Page<>(page, limit), trackHeadId, branchCode, order, orderCol));
    }


    @ApiOperation(value = "绑定非关键件(新)", notes = "绑定非关键件(新)")
    @ApiImplicitParam(name = "idList", value = "Id列表", required = true, dataType = "List<String>", paramType = "body")
    @PutMapping("/updateComplete")
    public CommonResult<Boolean> updateComplete(@RequestBody NonKeyDto nonKeyDto) {
        return trackAssemblyService.updateComplete(nonKeyDto.getIdList(), nonKeyDto.getItemId());
    }

    @ApiOperation(value = "解绑非关键件(新)", notes = "解绑非关键件(新)")
    @ApiImplicitParam(name = "idList", value = "Id列表", required = true, dataType = "String", paramType = "query")
    @PutMapping("/unbindComplete")
    public CommonResult<Boolean> unbindComplete(@RequestBody List<String> idList) {
        return CommonResult.success(trackAssemblyService.unbindComplete(idList));
    }

    @ApiOperation(value = "齐套性检查", notes = "齐套性检查")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "trackHeadId", value = "跟单Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "车间", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("/kittingExamine")
    public CommonResult<List<AssembleKittingVo>> kittingExamine(String trackHeadId, String branchCode) {
        return CommonResult.success(trackAssemblyService.kittingExamine(trackHeadId, branchCode));
    }

    @ApiOperation(value = "齐套性检查(计划)", notes = "齐套性检查(计划)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "trackHeadId", value = "跟单Id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "车间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "isComponent", value = "是否是部件", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("/planKittingExamine")
    public CommonResult<List<TrackAssembly>> planKittingExamine(String trackHeadId, String branchCode, Boolean isComponent) {
        return CommonResult.success(trackAssemblyService.planKittingExamine(trackHeadId, branchCode, isComponent));
    }


    @ApiOperation(value = "查询装配信息", notes = "根据跟单ID查询装配信息")
    @GetMapping("/query_track_assembly/{flowId}")
    public CommonResult<Map> queryTrackAssemblyByTrackNo(@PathVariable String flowId) {
        Map<String, List<TrackAssembly>> collect = trackAssemblyService.queryTrackAssemblyByTrackNo(flowId).stream().collect(Collectors.groupingBy(t -> t.getDrawingNo().split("-")[0]));
        return CommonResult.success(collect);
    }


    @ApiOperation(value = "发送申请单", notes = "发送申请单")
    @PostMapping("/application")
    public CommonResult<ApplicationResult> application(@RequestBody AdditionalMaterialDto additionalMaterialDto) {
        return CommonResult.success(trackAssemblyService.application(additionalMaterialDto));
    }

    @ApiOperation(value = "查询绑定信息", notes = "根据装配Id查询已绑定信息")
    @GetMapping("/queryBindingList")
    public CommonResult<List<TrackAssemblyBinding>> queryBindingList(String assemblyIdList) {
        return CommonResult.success(assemblyBingService.queryBindingList(assemblyIdList));
    }
}
