package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.NormalizeDehydroExecuteRecord;
import com.richfit.mes.common.model.produce.NormalizeDehydroRecord;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.NormalizeDehydroExecuteRecordService;
import com.richfit.mes.produce.service.NormalizeDehydroRecordService;
import com.richfit.mes.produce.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author 胡甲
 * @Description 正火去氢工序控制记录Controller
 */
@Slf4j
@Api(value = "正火去氢工艺执行记录", tags = {"正火去氢工艺执行记录"})
@RestController
@RequestMapping("/api/produce/executeRecord")
public class NormalizeDehydroExecuteRecordController extends BaseController {

    @Autowired
    private NormalizeDehydroExecuteRecordService normalizeDehydroExecuteRecordService;


    @ApiOperation(value = "添加工艺执行记录", notes = "工艺执行记录")
    @PostMapping("/saveExecuteRecord")
    public CommonResult<Boolean> saveExecuteRecordRecord(@RequestBody NormalizeDehydroExecuteRecord record) {
        return CommonResult.success(normalizeDehydroExecuteRecordService.save(record));
    }

    @ApiOperation(value = "根据正火去氢工序控制记录表查询工艺执行记录", notes = "根据正火去氢工序控制记录表查询工艺执行记录")
    @ApiImplicitParam(name = "recordId", value = "正火去氢工序控制记录表id", required = true, dataType = "String", paramType = "query")
    @GetMapping("/getExecuteRecordByRecordId")
    public CommonResult<List<NormalizeDehydroExecuteRecord>> getExecuteRecordByRecordId(String recordId) {
        QueryWrapper<NormalizeDehydroExecuteRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_id", recordId);
        return CommonResult.success(normalizeDehydroExecuteRecordService.list(queryWrapper));
    }
//
//
//
//    @ApiOperation(value = "正火去氢工序控制记录修改", notes = "正火去氢工序控制记录修改")
//    @PostMapping("/updateRecordById")
//    public CommonResult<Boolean> updateRecordById(@RequestBody NormalizeDehydroRecord normalizeDehydroRecord) {
//        return CommonResult.success(normalizeDehydroRecordService.updateById(normalizeDehydroRecord));
//    }
//
//    @ApiOperation(value = "正火去氢工序控制删除", notes = "正火去氢工序控制删除")
//    @PostMapping("/deleteNormalizeDehydroRecord")
//    public CommonResult<Boolean> deleteNormalizeDehydroRecord(@RequestBody List<NormalizeDehydroRecord> normalizeDehydroRecordList) {
//        return CommonResult.success(normalizeDehydroRecordService.removeByIds(normalizeDehydroRecordList));
//    }

}
