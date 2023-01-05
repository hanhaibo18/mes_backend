package com.richfit.mes.produce.controller.heat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.service.heat.HeatTrackAssignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

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

    @ApiOperation(value = "未装炉生产查询")
    @PostMapping("/query_not_produce")
    public CommonResult<IPage<Assign>> queryNotProduce(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryWhetherProduce(dispatchingDto, false));
    }

    @ApiOperation(value = "装炉生产查询")
    @PostMapping("/query_produce")
    public CommonResult<IPage<Assign>> queryProduce(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(heatTrackAssignService.queryWhetherProduce(dispatchingDto, true));
    }
}
