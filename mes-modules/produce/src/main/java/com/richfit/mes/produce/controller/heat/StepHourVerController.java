package com.richfit.mes.produce.controller.heat;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.StepHour;
import com.richfit.mes.common.model.produce.StepHourVer;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.heat.StepHourService;
import com.richfit.mes.produce.service.heat.StepHourVerService;
import com.richfit.mes.produce.utils.OrderUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
/**
 * @Author: renzewen
 */
@Slf4j
@Api(value = "热工步骤版本", tags = {"热工步骤版本"})
@RestController
@RequestMapping("/api/produce/step/hour/ver")
public class StepHourVerController {

    @Autowired
    private StepHourVerService stepHourVerService;
    @Autowired
    private StepHourService stepHourService;

    /**
     * 查询步骤工时版本列表
     */
    @ApiOperation(value = "查询步骤工时版本列表", notes = "查询步骤工时版本列表")
    @GetMapping("/page")
    public CommonResult queryPage(String startTime, String endTime, int page, int limit,String branchCode,String order,String orderCol) throws GlobalException {

        QueryWrapper<StepHourVer> queryWrapper = new QueryWrapper<StepHourVer>();
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.ge("activate_time", startTime);
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.le("activate_time", endTime);
        }
        //queryWrapper.eq(!StringUtils.isNullOrEmpty(branchCode),"branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        if(!StringUtils.isNullOrEmpty(orderCol)){
            //排序
            OrderUtil.query(queryWrapper, orderCol, order);
        }else{
            queryWrapper.orderByAsc("ver");
        }

        return CommonResult.success(stepHourVerService.page(new Page<StepHourVer>(page, limit), queryWrapper));

    }

    /**
     * 新增步骤工时版本信息
     */
    @ApiOperation(value = "新增步骤工时版本信息", notes = "新增步骤工时版本信息")
    @PostMapping("/saveOrUpdate")
    public CommonResult<Boolean> saveOrUpdate(@RequestBody StepHourVer stepHourVer) throws GlobalException {
        QueryWrapper<StepHourVer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ver",stepHourVer.getVer());
        List<StepHourVer> list = stepHourVerService.list(queryWrapper);
        if(list.size()>0){
            if((!StringUtils.isNullOrEmpty(stepHourVer.getId()) && !list.get(0).getId().equals(stepHourVer.getId())) || StringUtils.isNullOrEmpty(stepHourVer.getId())){
                throw new GlobalException("版本信息以存在！", ResultCode.FAILED);
            }
        }
        stepHourVer.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(stepHourVerService.saveOrUpdate(stepHourVer));
    }


    /**
     * 删除步骤工时版本信息
     */
    @ApiOperation(value = "删除步骤工时版本信息", notes = "删除步骤工时版本信息")
    @DeleteMapping("/delById/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException {
        //删除版本
        stepHourVerService.removeById(id);
        //删除步骤工时信息
        QueryWrapper<StepHour> stepHourQueryWrapper = new QueryWrapper<>();
        stepHourQueryWrapper.eq("ver_id",id);
        stepHourService.remove(stepHourQueryWrapper);
        return CommonResult.success(true);
    }

    /**
     * 激活
     */
    @ApiOperation(value = "根据id激活步骤工时版本", notes = "根据id激活步骤工时版本")
    @GetMapping("/activate/{id}")
    public CommonResult<Boolean> activate(@PathVariable String id) throws GlobalException {
        StepHourVer stepHourVer = stepHourVerService.getById(id);
        if(ObjectUtil.isEmpty(stepHourVer)){
            throw new GlobalException("版本信息不存在", ResultCode.FAILED);
        }
        //全部转非激活
        UpdateWrapper<StepHourVer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_activate","0");
        boolean update = stepHourVerService.update(updateWrapper);
        //将选中的转激活
        UpdateWrapper<StepHourVer> updateWrapper2 = new UpdateWrapper<>();
        updateWrapper2.eq("id",id)
                .set("is_activate","1")
                .set("is_activated","1")
                .set("activate_time",new Date())
                .set("activate_by",SecurityUtils.getCurrentUser().getUsername());
        return CommonResult.success(stepHourVerService.update(updateWrapper2));
    }
}
