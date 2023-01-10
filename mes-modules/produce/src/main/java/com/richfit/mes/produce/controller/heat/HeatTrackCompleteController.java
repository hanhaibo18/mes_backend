package com.richfit.mes.produce.controller.heat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.entity.heat.HeatCompleteDto;
import com.richfit.mes.produce.service.heat.HeatTrackCompleteService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

/**
 * @author renzewen
 * @Description 跟单报工Controller
 */
@Slf4j
@Api(value = "热工报工", tags = {"热工报工"})
@RestController
@RequestMapping("/api/produce/heat/complete")
public class HeatTrackCompleteController extends BaseController {

    @Autowired
    private HeatTrackCompleteService heatTrackCompleteService;

    @ApiOperation(value = "报工")
    @PostMapping("/saveComplete")
    public CommonResult<Boolean> saveComplete(@ApiParam(value = "查询条件", required = true) @RequestBody HeatCompleteDto heatCompleteDto) throws Exception {
        return CommonResult.success(heatTrackCompleteService.saveComplete(heatCompleteDto));
    }

    @ApiOperation(value = "开工")
    @GetMapping("/startWork")
    public CommonResult<Boolean> startWork(@ApiParam(value = "预装炉id", required = true) String prechargeFurnaceId){
        return CommonResult.success(heatTrackCompleteService.startWork(prechargeFurnaceId));
    }

    @ApiOperation(value = "根据预装炉id获取报工信息")
    @GetMapping("/getCompleteInfoByFuId")
    public CommonResult<Map<String,Object>> getCompleteInfoByFuId(@ApiParam(value = "预装炉id", required = true) String prechargeFurnaceId){
        return CommonResult.success(heatTrackCompleteService.getCompleteInfoByFuId(prechargeFurnaceId));
    }

}
