package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.HotDemand;
import com.richfit.mes.common.model.produce.HotLongProduct;
import com.richfit.mes.common.model.produce.HotLongProductQueryVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.HotDemandParam;
import com.richfit.mes.produce.service.HotDemandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Slf4j
@Api(value = "热工需求管理", tags = {"热工需求管理"})
@RestController
@RequestMapping("/api/produce/demand")
public class HotDemandController extends BaseController {


    @Resource
    private HotDemandService hotDemandService;

    @ApiOperation(value = "新增需求提报", notes = "新增需求提报")
    @PostMapping("/save")
    public CommonResult saveDemand(@RequestBody HotDemand hotDemand){
        hotDemand.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        boolean save = hotDemandService.save(hotDemand);
        if (save){
           return CommonResult.success(ResultCode.SUCCESS);
        }else {
           return CommonResult.failed(ResultCode.FAILED);
        }
    }

    @ApiOperation(value = "需求提报列表查询", notes = "需求提报列表查询")
    @PostMapping("/demand_page")
    public CommonResult<IPage<HotDemand>> demandPage(@RequestBody HotDemandParam hotDemandParam) {
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<HotDemand>();
        if(StringUtils.isNotEmpty(hotDemandParam.getProjectName())){//项目名称
            queryWrapper.eq("project_name",hotDemandParam.getProjectName());
        }
        if(StringUtils.isNotEmpty(hotDemandParam.getWorkNo())){//工作号
            queryWrapper.eq("work_no",hotDemandParam.getWorkNo());
        }
        if(StringUtils.isNotEmpty(hotDemandParam.getDrawNo())){//图号
            queryWrapper.eq("draw_no",hotDemandParam.getDrawNo());
        }
        if(StringUtils.isNotEmpty(hotDemandParam.getVoucherNo())){//凭证号
            queryWrapper.eq("voucher_no",hotDemandParam.getVoucherNo());
        }
        if(StringUtils.isNotEmpty(hotDemandParam.getSubmitOrderOrg())){//提单单位
            queryWrapper.eq("submit_order_org",hotDemandParam.getSubmitOrderOrg());
        }
        if(StringUtils.isNotEmpty(hotDemandParam.getWorkblankType())){//毛坯类型
            queryWrapper.eq("workblank_type",hotDemandParam.getWorkblankType());
        }
        //提报状态
        if(hotDemandParam.getSubmitState()!=null){
            queryWrapper.eq("submit_state",hotDemandParam.getSubmitState());
        }
//        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(dispatchingDto.getStartTime())) {
//            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) >= UNIX_TIMESTAMP('" + dispatchingDto.getStartTime() + " ')");
//        }
//        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(dispatchingDto.getEndTime())) {
//            Calendar calendar = new GregorianCalendar();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            calendar.setTime(sdf.parse(dispatchingDto.getEndTime()));
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
//        }
        Page<HotDemand> page = hotDemandService.page(new Page<HotDemand>(hotDemandParam.getPage(), hotDemandParam.getLimit()), queryWrapper);
        return CommonResult.success(page, ResultCode.SUCCESS.getMessage());
    }

    @ApiOperation(value = "修改需求提报", notes = "修改需求提报")
    @PostMapping("/update_demand")
    public CommonResult updateDemand(@RequestBody HotDemand hotDemand) {
        boolean b = hotDemandService.updateById(hotDemand);
        if (b){
            return CommonResult.success(ResultCode.SUCCESS);
        }else {
            return CommonResult.failed(ResultCode.FAILED);
        }
    }


    @ApiOperation(value = "删除需求提报", notes = "删除需求提报")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hotForgingParamIdList", value = "仿型参数IdList", required = true, paramType = "query")
    })
    @PostMapping("/delete_demand")
    public CommonResult deleteDemand(@RequestBody List<String> idList) {
        List<HotDemand> hotDemands = hotDemandService.listByIds(idList);
        for (HotDemand hotDemand : hotDemands) {
            //提报状态 0 :未提报  1 :已提报
            if(hotDemand.getSubmitState()!=null & hotDemand.getSubmitState()==1) throw new GlobalException("已提报的不能删除",ResultCode.FAILED);
        }
        boolean b = hotDemandService.removeByIds(idList);
        if (b==true) return CommonResult.success(ResultCode.SUCCESS);
        return CommonResult.failed();
    }

    @ApiOperation(value = "导入需求提报", notes = "根据Excel文档导入导入需求提报")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/import_demand")
    public CommonResult importExcelDemand(HttpServletRequest request, @RequestParam("file") MultipartFile file, String branchCode) {
        return hotDemandService.importDemand(file, branchCode);
    }








}
