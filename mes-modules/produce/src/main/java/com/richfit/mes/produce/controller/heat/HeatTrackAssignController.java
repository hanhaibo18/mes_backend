package com.richfit.mes.produce.controller.heat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.service.heat.HeatTrackAssignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.*;

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

    @ApiOperation(value = "未装炉生产查询")
    @PostMapping("/query_not_produce")
    public CommonResult<IPage<Assign>> queryNotProduce(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryWhetherProduce(dispatchingDto, false));
    }
    @ApiOperation(value = "未装炉生产查询--热工")
    @PostMapping("/query_not_produce_hot")
    public CommonResult<IPage<AssignHot>> queryNotProduceHot(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryWhetherProduceHot(dispatchingDto, false));
    }
    @ApiOperation(value = "装炉生产查询")
    @PostMapping("/query_produce")
    public CommonResult<IPage<Assign>> queryProduce(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryWhetherProduce(dispatchingDto, true));
    }

    @ApiOperation(value = "装炉生产查询--热工")
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
}
