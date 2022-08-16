package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackAssemblyBinding;
import com.richfit.mes.produce.entity.*;
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
//
//    /**
//     * ***
//     * 分页查询
//     *
//     * @param page
//     * @param limit
//     * @return
//     */
//    @ApiOperation(value = "产品装配分页查询", notes = "产品装配分页查询")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
//            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
//            @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, paramType = "query", dataType = "string")
//    })
//    @GetMapping("/page")
//    public CommonResult<IPage<TrackAssembly>> page(int page, int limit, String tiId, String thId, String productNo, String mainCompNo, String startTime, String endTime) {
//        try {
//            QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<TrackAssembly>();
//            if (!StringUtils.isNullOrEmpty(tiId)) {
//                queryWrapper.eq("ti_id", tiId);
//            }
//            if (!StringUtils.isNullOrEmpty(thId)) {
//                queryWrapper.eq("th_id", thId);
//            }
//            if (!StringUtils.isNullOrEmpty(productNo)) {
//                queryWrapper.eq("product_no", productNo);
//            }
//            if (!StringUtils.isNullOrEmpty(mainCompNo)) {
//                queryWrapper.eq("main_comp_no", mainCompNo);
//            }
//            if (!StringUtils.isNullOrEmpty(startTime)) {
//                queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");
//
//            }
//            if (!StringUtils.isNullOrEmpty(endTime)) {
//                queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + endTime + "')");
//
//            }
//            queryWrapper.orderByAsc("modify_time");
//            IPage<TrackAssembly> assemblys = trackAssemblyService.page(new Page<TrackAssembly>(page, limit), queryWrapper);
//            return CommonResult.success(assemblys);
//        } catch (Exception e) {
//            return CommonResult.failed(e.getMessage());
//        }
//    }
//
//    @ApiOperation(value = "新增产品装配", notes = "新增产品装配")
//    @ApiImplicitParam(name = "assembly", value = "产品装配", required = true, dataType = "Assembly", paramType = "path")
//    @PostMapping("/add")
//    @Transactional(rollbackFor = Exception.class)
//    public CommonResult<TrackAssembly> addAssembly(@RequestBody TrackAssembly assembly) {
//
//        boolean bool = trackAssemblyService.save(assembly);
//        if (bool) {
//            return CommonResult.success(assembly, "操作成功！");
//        } else {
//            return CommonResult.failed("操作失败，请重试！");
//        }
//    }
//
//    @ApiOperation(value = "修改产品装配", notes = "修改产品装配")
//    @ApiImplicitParam(name = "device", value = "产品装配", required = true, dataType = "Assembly", paramType = "path")
//    @PostMapping("/update")
//    @Transactional(rollbackFor = Exception.class)
//    public CommonResult<TrackAssembly> updateAssembly(@RequestBody TrackAssembly assembly) {
//
//        boolean bool = trackAssemblyService.updateById(assembly);
//        if (bool) {
//            return CommonResult.success(assembly, "操作成功！");
//        } else {
//            return CommonResult.failed("操作失败，请重试！");
//        }
//    }
//
//    @ApiOperation(value = "修改产品名称", notes = "修改产品名称")
//    @ApiImplicitParam(name = "productNo", value = "产品名称", required = true, dataType = "String", paramType = "query")
//    @PostMapping("/updateProduct")
//    @Transactional(rollbackFor = Exception.class)
//    public CommonResult<List<TrackAssembly>> updateProduct(String assignId, String productOrder, String productNo) {
//        QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<TrackAssembly>();
//        if (!StringUtils.isNullOrEmpty(assignId)) {
//            queryWrapper.eq("assign_id", assignId);
//        }
//        if (!StringUtils.isNullOrEmpty(productOrder)) {
//            queryWrapper.eq("product_order", Integer.parseInt(productOrder));
//        }
//        List<TrackAssembly> result = trackAssemblyService.list(queryWrapper);
//        for (int i = 0; i < result.size(); i++) {
//            result.get(i).setProductNo(productNo);
//            trackAssemblyService.updateById(result.get(i));
//        }
//        return CommonResult.success(result, "操作成功！");
//    }
//
//    @ApiOperation(value = "报工查询", notes = "报工查询")
//    @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, dataType = "String", paramType = "path")
//    @GetMapping("/find")
//    public CommonResult<List<TrackAssembly>> find(String tiId, String thId, String productNo, String mainCompNo, String subCompDrawNo, String productOrder, String assignId) {
//
//        QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<TrackAssembly>();
//        if (!StringUtils.isNullOrEmpty(tiId)) {
//            queryWrapper.eq("ti_id", tiId);
//        }
//        if (!StringUtils.isNullOrEmpty(thId)) {
//            queryWrapper.eq("th_id", thId);
//        }
//        if (!StringUtils.isNullOrEmpty(productNo)) {
//            queryWrapper.eq("product_no", productNo);
//        }
//        if (!StringUtils.isNullOrEmpty(mainCompNo)) {
//            queryWrapper.eq("main_comp_no", mainCompNo);
//        }
//        if (!StringUtils.isNullOrEmpty(subCompDrawNo)) {
//            queryWrapper.eq("sub_comp_draw_no", subCompDrawNo);
//        }
//        if (!StringUtils.isNullOrEmpty(productOrder)) {
//            queryWrapper.eq("product_order", Integer.parseInt(productOrder));
//        }
//        if (!StringUtils.isNullOrEmpty(assignId)) {
//            queryWrapper.eq("assign_id", assignId);
//        }
//
//        queryWrapper.orderByAsc("product_order");
//        List<TrackAssembly> result = trackAssemblyService.list(queryWrapper);
//        return CommonResult.success(result, "操作成功！");
//    }
//
//    @ApiOperation(value = "删除产品装配", notes = "根据id删除产品装配")
//    @ApiImplicitParam(name = "ids", value = "ID", required = true, dataType = "String[]", paramType = "path")
//    @PostMapping("/delete")
//    @Transactional(rollbackFor = Exception.class)
//    public CommonResult<TrackAssembly> delete(@RequestBody String[] ids) {
//
//
//        String msg = "";
//        for (int i = 0; i < ids.length; i++) {
//            trackAssemblyService.removeById(ids[i]);
//        }
//        if (msg.equals("")) {
//            return CommonResult.success(null, "删除成功！");
//        } else {
//            return CommonResult.failed("操作失败，请重试！" + msg);
//        }
//    }


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
        return CommonResult.success(trackAssemblyService.updateComplete(nonKeyDto.getIdList(), nonKeyDto.getItemId()));
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
    @GetMapping("/queryTrackAssemblyByTrackNo/{trackNo}")
    public CommonResult<Map> queryTrackAssemblyByTrackNo(@PathVariable String trackNo) {
        Map<String, List<TrackAssembly>> collect = trackAssemblyService.queryTrackAssemblyByTrackNo(trackNo).stream().collect(Collectors.groupingBy(t -> t.getDrawingNo().split("-")[0]));
        return CommonResult.success(collect);
    }


    @ApiOperation(value = "发送申请单", notes = "发送申请单")
    @PostMapping("/application")
    public ApplicationResult application(@RequestBody AdditionalMaterialDto additionalMaterialDto) {
        return trackAssemblyService.application(additionalMaterialDto);
    }
}
