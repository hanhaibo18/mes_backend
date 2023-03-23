package com.richfit.mes.produce.controller.heat.forg;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.forg.ForgHour;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.produce.service.forg.ForgHourService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: renzewen
 */
@Slf4j
@Api(value = "热工工时", tags = {"热工工时"})
@RestController
@RequestMapping("/api/produce/forg/hour")
public class ForgHourController {

    @Autowired
    private ForgHourService forgHourService;

    /**
     * 查询工时标准列表
     */
    @ApiOperation(value = "查询工时标准列表", notes = "查询工时标准列表")
    @GetMapping("/page")
    public CommonResult queryPage(String order, String orderCol,String branchCode, String optName,String optId,String startTime,String endTime,int page,int limit) throws GlobalException {

        QueryWrapper<ForgHour> queryWrapper = new QueryWrapper<ForgHour>();
        queryWrapper.ge(!StringUtils.isNullOrEmpty(startTime),"modify_time",startTime);
        queryWrapper.le(!StringUtils.isNullOrEmpty(endTime),"modify_time",endTime);
        queryWrapper.eq(!StringUtils.isNullOrEmpty(optId),"opt_id", optId);
        queryWrapper.eq(!StringUtils.isNullOrEmpty(optName),"opt_name", optName);
        queryWrapper.eq(!StringUtils.isNullOrEmpty(branchCode),"branch_code", branchCode);
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            //排序
            OrderUtil.query(queryWrapper, orderCol, order);
        } else {
            queryWrapper.orderByAsc("modify_time");
        }

        return CommonResult.success(forgHourService.page(new Page<>(page,limit),queryWrapper));

    }

    /**
     * 保存工时信息
     */
    @ApiOperation(value = "保存工时信息", notes = "保存工时信息")
    @PostMapping("/saveOrUpdate")
    public CommonResult saveOrUpdate(@RequestBody ForgHour forgHour) throws GlobalException {
        return CommonResult.success(forgHourService.saveOrUpdate(forgHour));
    }


    /**
     * 删除工时信息
     */
    @ApiOperation(value = "删除工时信息", notes = "删除工时信息")
    @DeleteMapping("/delById/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException {
        return CommonResult.success(forgHourService.removeById(id));
    }

}
