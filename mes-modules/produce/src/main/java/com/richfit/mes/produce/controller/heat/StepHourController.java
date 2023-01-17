package com.richfit.mes.produce.controller.heat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.StepHour;
import com.richfit.mes.produce.service.heat.StepHourService;
import com.richfit.mes.produce.utils.OrderUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @Author: renzewen
 */
@Slf4j
@Api(value = "热工步骤工时", tags = {"热工步骤工时"})
@RestController
@RequestMapping("/api/produce/step/hour")
public class StepHourController {

    @Autowired
    private StepHourService stepHourService;

    /**
     * 查询步骤工时列表
     */
    @ApiOperation(value = "查询步骤工时列表", notes = "查询步骤工时列表")
    @GetMapping("/list")
    public CommonResult queryPage(String order,String orderCol,String verId) throws GlobalException {

        QueryWrapper<StepHour> queryWrapper = new QueryWrapper<StepHour>();
        queryWrapper.eq("ver_id",verId);
        if(!StringUtils.isNullOrEmpty(orderCol)){
            //排序
            OrderUtil.query(queryWrapper, orderCol, order);
        }else{
            queryWrapper.orderByAsc("step_type")
                    .orderByAsc("step_name");
        }

        return CommonResult.success(stepHourService.list(queryWrapper));

    }

    /**
     * 保存工时步骤信息
     */
    @ApiOperation(value = "保存工时步骤信息", notes = "保存工时步骤信息")
    @PostMapping("/saveOrUpdate")
    public CommonResult saveOrUpdate(@RequestBody StepHour stepHour) throws GlobalException {
        QueryWrapper<StepHour> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("step_type",stepHour.getStepType())
                .eq("ver_id",stepHour.getVerId())
                .eq("step_name",stepHour.getStepName());
        List<StepHour> list = stepHourService.list(queryWrapper);
        if(list.size()>0){
            if((!StringUtils.isNullOrEmpty(stepHour.getId()) && !list.get(0).getId().equals(stepHour.getId())) || StringUtils.isNullOrEmpty(stepHour.getId())){
                return CommonResult.failed("此版本下已经存在报工类型（"+stepHour.getStepType()+"）步骤名（"+stepHour.getStepName()+"）的步骤工时，无法保存！");
            }
        }
        return CommonResult.success(stepHourService.saveOrUpdate(stepHour));
    }


    /**
     * 删除步骤工时信息
     */
    @ApiOperation(value = "删除步骤工时信息", notes = "删除步骤工时信息")
    @DeleteMapping("/delById/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException {
        return CommonResult.success(stepHourService.removeById(id));
    }

}
