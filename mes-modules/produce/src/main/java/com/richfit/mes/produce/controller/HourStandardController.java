package com.richfit.mes.produce.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Hour;
import com.richfit.mes.common.model.produce.HourStandard;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.HourService;
import com.richfit.mes.produce.service.HourStandardService;
import com.richfit.mes.produce.utils.OrderUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: renzewen
 */
@Slf4j
@Api(value = "工时版本", tags = {"工时版本"})
@RestController
@RequestMapping("/api/produce/hour/standard")
public class HourStandardController {

    @Autowired
    private HourStandardService hourStandardService;
    @Autowired
    private HourService hourService;

    /**
     * 查询工时版本列表
     */
    @ApiOperation(value = "查询工时版本列表", notes = "查询工时版本列表")
    @GetMapping("/page")
    public CommonResult queryPage(String startTime, String endTime, int page, int limit,String branchCode,String order,String orderCol) throws GlobalException {

        QueryWrapper<HourStandard> queryWrapper = new QueryWrapper<HourStandard>();
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
            queryWrapper.orderByDesc("activate_time");
        }

        return CommonResult.success(hourStandardService.page(new Page<HourStandard>(page, limit), queryWrapper));

    }

    /**
     * 新增工时版本信息
     */
    @ApiOperation(value = "新增工时版本信息", notes = "新增工时版本信息")
    @PostMapping("/save")
    public CommonResult<Boolean> saveOrUpdate(@RequestBody HourStandard hourStandard) throws GlobalException {
        hourStandard.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(hourStandardService.saveOrUpdate(hourStandard));
    }


    /**
     * 删除工时版本信息
     */
    @ApiOperation(value = "删除工时版本信息", notes = "根据id删除工时版本信息")
    @DeleteMapping("/delById/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException {
        return CommonResult.success(hourStandardService.removeById(id));
    }

    /**
     * 激活
     */
    @ApiOperation(value = "根据id激活工时版本", notes = "根据id激活工时版本")
    @GetMapping("/activate/{id}")
    public CommonResult<Boolean> activate(@PathVariable String id) throws GlobalException {
        HourStandard hourStandard = hourStandardService.getById(id);
        if(ObjectUtil.isEmpty(hourStandard)){
            throw new GlobalException("版本信息不存在", ResultCode.FAILED);
        }
        UpdateWrapper<HourStandard> hourStandardUpdateWrapper = new UpdateWrapper<>();
        hourStandardUpdateWrapper.eq("id",id)
                .set("is_activate","1")
                .set("is_activated","1")
                .set("activate_time",new Date())
                .set("activate_by",SecurityUtils.getCurrentUser().getUsername());
        return CommonResult.success(hourStandardService.update(hourStandardUpdateWrapper));
    }

    /**
     * 激活
     */
    @ApiOperation(value = "复制", notes = "复制")
    @GetMapping("/activate/copy")
    public CommonResult<Boolean> copy(String ver,String remark) throws GlobalException {
        QueryWrapper<HourStandard> hourStandardQueryWrapper = new QueryWrapper<>();
        hourStandardQueryWrapper.eq("ver",ver);
        List<HourStandard> list = hourStandardService.list(hourStandardQueryWrapper);
        if(list.size()>0){
            //版本复制
            HourStandard hourStandard = new HourStandard();
            hourStandard.setRemark(remark);
            String copyModelName = getCopyModelName(ver);
            hourStandard.setVer(copyModelName);
            hourStandard.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            hourStandardService.save(hourStandard);
            //设备复制
            QueryWrapper<Hour> hourQueryWrapper = new QueryWrapper<>();
            hourQueryWrapper.eq("ver_id",list.get(0).getId());
            List<Hour> hours = hourService.list(hourQueryWrapper);
            for (Hour hour : hours) {
                hour.setVerId(hourStandard.getId());
            }
            return CommonResult.success(hourService.saveBatch(hours));
        }else{
            throw new GlobalException("版本信息不存在", ResultCode.FAILED);
        }
    }

    private String getCopyModelName(String ver){

        String finalStr  = "副本";
        String pattern = finalStr+"\\d+$";
        String copyIndex = matchParam(ver,pattern);

        String originalVer = "";
        if(org.apache.commons.lang3.StringUtils.isEmpty(copyIndex) || copyIndex.startsWith("副本0")){
            originalVer = ver;
        }else{
            originalVer = ver.substring(0,ver.indexOf(copyIndex));
        }

        QueryWrapper<HourStandard> hourStandardQueryWrapper = new QueryWrapper<>();
        hourStandardQueryWrapper.like("ver",originalVer);
        List<HourStandard> list = hourStandardService.list(hourStandardQueryWrapper);
        String newVer = "";
        int maxIndex = 0 ;
        for(HourStandard hourStandard : list){
            String willCopyIndex = matchParam(hourStandard.getVer(),pattern);
            if(org.apache.commons.lang3.StringUtils.isEmpty(willCopyIndex) || willCopyIndex.startsWith("副本0")){
                if(maxIndex == 0) {
                    newVer = originalVer + finalStr + "1";
                }
            }else{
                String currentIndex = willCopyIndex.substring(finalStr.length(),willCopyIndex.length());
                int currentIndexNum= Integer.parseInt(currentIndex);
                if(currentIndexNum > maxIndex){
                    newVer = originalVer + finalStr + (currentIndexNum+1);
                    maxIndex = currentIndexNum;
                }
            }
        }
        return newVer;
    }

    /*
     *
     * @param value  要检验的值
     * @param pattern 正则表达式
     * @return   匹配的字符，为空时没有匹配上
     * @author renzewen
     * date 2022/12/27
     */
    public String matchParam(String value,String pattern) {
        String endStr = "";
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(value)) {
            Pattern p = Pattern.compile(pattern); //正则匹配
            Matcher matcherS = p.matcher(value);
            while (matcherS.find()) {
                endStr = matcherS.group(0);//开始数
            }
        }
        return endStr;
    }
}
