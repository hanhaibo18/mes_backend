package com.richfit.mes.produce.controller.heat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * @author zhiqiang.lu
 * @Description 预装炉Controller
 */
@Slf4j
@Api(value = "预装炉", tags = {"预装炉"})
@RestController
@RequestMapping("/api/produce/heat/precharge/furnace")
public class PrechargeFurnaceController extends BaseController {


    @Autowired
    private TrackItemService trackItemService;

    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;

    @ApiOperation(value = "装炉")
    @PostMapping("/furnace_charging")
    public CommonResult<List<Assign>> furnaceCharging(@ApiParam(value = "保存信息", required = true) @RequestBody List<Assign> assignList) throws ParseException {
        prechargeFurnaceService.furnaceCharging(assignList);
        return CommonResult.success(assignList);
    }

    @ApiOperation(value = "装炉查询", tags = "不分页装炉列表查询")
    @PostMapping("/query")
    public CommonResult<List<PrechargeFurnace>> query(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        QueryWrapper<PrechargeFurnace> queryWrapper = new QueryWrapper();
        queryWrapper.orderByAsc("modify_time");
        return CommonResult.success(prechargeFurnaceService.list(queryWrapper));
    }

    @ApiOperation(value = "装炉跟单工序查询", tags = "不分页装炉跟单工序查询")
    @GetMapping("/query/track/item")
    public CommonResult<List<Assign>> queryTrackItem(@ApiParam(value = "预装炉ID", required = true) @RequestParam String id) throws ParseException {
        return CommonResult.success(prechargeFurnaceService.queryTrackItem(id));
    }
}
