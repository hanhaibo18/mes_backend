package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.ProduceInspectionRecord;
import com.richfit.mes.produce.service.ProduceInspectionRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/**
 * @Author: renzewen
 * @Date: 2022/8/22 13:10
 */
@Slf4j
@Api(tags = "探伤记录")
@RestController
@RequestMapping("/api/produce/inspection/records")
public class ProduceInspectionRecordController extends BaseController {

    @Autowired
    private ProduceInspectionRecordService produceInspectionRecordService;

    @ApiOperation(value = "根据工序id查询探伤记录信息", notes = "根据工序id查询探伤记录信息")
    @GetMapping("/queryInfoByItemId")
    public CommonResult queryInfoByItemId(@ApiParam(value = "工序id", required = true) String itemId) {
        List<ProduceInspectionRecord> produceInspectionRecords = produceInspectionRecordService.list(new QueryWrapper<ProduceInspectionRecord>().eq("item_id", itemId));
        return CommonResult.success(produceInspectionRecords);
    }

    @ApiOperation(value = "保存探伤记录", notes = "保存探伤记录")
    @PostMapping("/save")
    public CommonResult<ProduceInspectionRecord> saveOrUpdate(@RequestBody ProduceInspectionRecord produceInspectionRecord){
        produceInspectionRecordService.saveOrUpdate(produceInspectionRecord);
        return CommonResult.success(produceInspectionRecord);
    }


}
