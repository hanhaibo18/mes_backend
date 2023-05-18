package com.richfit.mes.produce.controller.heat;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.service.heat.HeatTrackAssignService;
import com.richfit.mes.produce.utils.ProcessFiltrationUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhiqiang.lu
 * @Description 跟单派工Controller
 */
@Slf4j
@Api(value = "跟单派工", tags = {"跟单派工"})
@RestController
@RequestMapping("/api/produce/heat/assign")
public class HeatTrackAssignController extends BaseController {

    @Autowired
    private HeatTrackAssignService heatTrackAssignService;
    @Autowired
    public TrackHeadService trackHeadService;
    @Autowired
    public SystemServiceClient systemServiceClient;
    @Autowired
    public ProduceRoleOperationService roleOperationService;
    @Autowired
    public TrackAssignService trackAssignService;
    @Autowired
    public BaseServiceClient baseServiceClient;
    @Autowired
    public ModelApplyService modelApplyService;

    @ApiOperation(value = "未装炉生产查询-（热处理）")
    @PostMapping("/query_not_produce")
    public CommonResult<IPage<Assign>> queryNotProduce(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryWhetherProduce(dispatchingDto, false));
    }
    @ApiOperation(value = "未装炉生产查询-(铸造、铸钢、冶炼)")
    @PostMapping("/query_not_produce_hot")
    public CommonResult<IPage<AssignHot>> queryNotProduceHot(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryWhetherProduceHot(dispatchingDto, false));
    }
    @ApiOperation(value = "装炉生产查询-（热处理）")
    @PostMapping("/query_produce")
    public CommonResult<IPage<Assign>> queryProduce(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryWhetherProduce(dispatchingDto, true));
    }

    @ApiOperation(value = "装炉生产查询-（铸造、铸钢、冶炼)")
    @PostMapping("/query_produce_hot")
    public CommonResult<IPage<AssignHot>> queryProduceHot(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryWhetherProduceHot(dispatchingDto, true));
    }

    /**
     * @param assign
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "热处理批量新增派工", notes = "热处理批量新增派工")
    @ApiImplicitParam(name = "assigns", value = "派工", required = true, dataType = "Assign[]", paramType = "body")
    @PostMapping("/assignItem")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> assignItem(@RequestBody List<Assign> assign) throws Exception {
        return CommonResult.success(heatTrackAssignService.assignItem(assign), "操作成功！");
    }

    @ApiOperation(value = "热工铸钢车间派工查询", notes = "热工铸钢车间派工查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "trackNo", value = "跟单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "drawingNo", value = "图号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workNo", value = "工作号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "texture", value = "材质", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "isLongPeriod", value = "是否长周期", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "priority", value = "优先级", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productName", value = "产品名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "optName", value = "工序名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "机构", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderCol", value = "排序字段", dataType = "String", paramType = "query")
    })
    @GetMapping("/getPageAssignsByStatus")
    public CommonResult<IPage<TrackItem>> getPageAssignsByStatus(int page, int limit, String trackNo, String
            drawingNo, String workNo,String texture,String isLongPeriod,String priority,String productName, String optName,String startTime, String endTime,String branchCode, String order, String orderCol, String productNo) throws ParseException {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        //增加工序过滤
        ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);

        if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.le("date_format(modify_time, '%Y-%m-%d')", endTime);
            queryWrapper.ge("date_format(modify_time, '%Y-%m-%d')", startTime);
        }
        if(!StringUtils.isNullOrEmpty(drawingNo)){
            DrawingNoUtil.queryEq(queryWrapper,"drawing_no",drawingNo);
        }
        if(!StringUtils.isNullOrEmpty(trackNo)){
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.eq("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '')", trackNo);
        }
        queryWrapper.eq(!StringUtils.isNullOrEmpty(texture),"texture", texture);
        queryWrapper.eq(!StringUtils.isNullOrEmpty(isLongPeriod),"is_long_period", isLongPeriod);
        queryWrapper.eq(!StringUtils.isNullOrEmpty(priority),"priority", priority);
        queryWrapper.eq(!StringUtils.isNullOrEmpty(branchCode),"branch_code", branchCode);
        queryWrapper.like(!StringUtils.isNullOrEmpty(productNo),"product_no", productNo);
        queryWrapper.like(!StringUtils.isNullOrEmpty(productName),"product_name", productName);
        queryWrapper.eq(!StringUtils.isNullOrEmpty(optName),"opt_name", optName);
        queryWrapper.eq(!StringUtils.isNullOrEmpty(workNo),"work_no", workNo);
        queryWrapper.ne("is_schedule", 1);

        //排序
        if (StringUtils.isNullOrEmpty(orderCol)) {
            queryWrapper.orderByDesc(new String[]{"modify_time", "sequence_order_by"});
        } else {
            if(orderCol.endsWith(",")){
                orderCol = orderCol.substring(0, orderCol.length()-1);
            }
            OrderUtil.query(queryWrapper, orderCol, StringUtils.isNullOrEmpty(order)?"desc":order);
        }

        IPage<TrackItem> pageAssignsHot = trackAssignService.getPageAssignsHot(new Page(page, limit), queryWrapper);
        for (TrackItem data : pageAssignsHot.getRecords()) {
            //默认未配送
            data.setApplyStatus(0);
            Router router = baseServiceClient.getRouter(data.getRouterId()).getData();
            if(!ObjectUtil.isEmpty(router)){
                data.setWeightMolten(router.getWeightMolten());
            }
            //模型配送状态
            String modelDrawingNo = data.getDrawingNo();
            String optVer = data.getOptVer();
            QueryWrapper<ModelApply> modelApplyQueryWrapper = new QueryWrapper<>();
            modelApplyQueryWrapper.eq("model_drawing_no",modelDrawingNo)
                    .eq("model_version",optVer);
            List<ModelApply> modelApplyList = modelApplyService.list(modelApplyQueryWrapper);
            if(modelApplyList.size() == 0){
                continue;
            }
            //一次性的
            List<ModelApply> mode = modelApplyList.stream().filter(item -> data.getId().equals(item.getItemId())).collect(Collectors.toList());
            if(!CollectionUtil.isEmpty(mode)){
                data.setApplyStatus(mode.get(0).getApplyStatus());
            }else{
                data.setApplyStatus(modelApplyList.get(0).getApplyStatus());
            }

        }

        return CommonResult.success(pageAssignsHot, "操作成功！");
    }

    @ApiOperation(value = "热工铸钢车间已派工查询")
    @PostMapping("/queryForDispatching")
    public CommonResult<IPage<AssignHot>> queryForDispatching(@RequestBody ForDispatchingDto dispatchingDto){
        return CommonResult.success(trackAssignService.queryDispatched(dispatchingDto));
    }
}
