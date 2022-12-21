package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.HotDemand;
import com.richfit.mes.common.model.produce.HotLongProduct;
import com.richfit.mes.common.model.produce.HotLongProductQueryVo;
import com.richfit.mes.common.model.produce.HotModelStore;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.HotDemandParam;
import com.richfit.mes.produce.service.HotDemandService;
import com.richfit.mes.produce.service.HotLongProductService;
import com.richfit.mes.produce.service.HotModelStoreService;
import com.richfit.mes.produce.utils.DateUtils;
import com.richfit.mes.produce.utils.OrderUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Api(value = "热工需求管理", tags = {"热工需求管理"})
@RestController
@RequestMapping("/api/produce/demand")
public class HotDemandController extends BaseController {


    @Resource
    private HotDemandService hotDemandService;
    @Resource
    private HotLongProductService hotLongProductService;

    @Resource
    public HotModelStoreService hotModelStoreService;

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
        //0 :未提报  1 :已提报'
        if(hotDemandParam.getSubmitState()!=null){
            queryWrapper.eq("submit_state",hotDemandParam.getSubmitState());
        }
        //0 :未批准 ,1 已批准',
        if(hotDemandParam.getProduceRatifyState()!=null){
            queryWrapper.eq("produce_ratify_state",hotDemandParam.getProduceRatifyState());
        }


         //需求日期
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(hotDemandParam.getDemandStartTime()==null?"":hotDemandParam.getDemandStartTime().toString())) {
            queryWrapper.ge("demand_time", DateUtils.getStartOfDay(hotDemandParam.getDemandStartTime()));
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(hotDemandParam.getDemandEndTime()==null?"":hotDemandParam.getDemandEndTime().toString())) {
            queryWrapper.le("demand_time", DateUtils.getEndOfDay(hotDemandParam.getDemandEndTime()));
        }
        //提单日期
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(hotDemandParam.getSubmitStartTime()==null?"":hotDemandParam.getSubmitStartTime().toString())) {
            queryWrapper.ge("submit_order_time", DateUtils.getStartOfDay(hotDemandParam.getSubmitStartTime()));
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(hotDemandParam.getSubmitEndTime()==null?"":hotDemandParam.getSubmitEndTime().toString())) {
            queryWrapper.le("submit_order_time", DateUtils.getEndOfDay(hotDemandParam.getSubmitEndTime()));
        }
        //排序工具
        OrderUtil.query(queryWrapper, hotDemandParam.getOrderCol(), hotDemandParam.getOrder());

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
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query")
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

    @ApiOperation(value = "需求提报与撤回", notes = "需求提报与撤回")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "submitState", value = "提报状态 0 :未提报  1 :已提报", required = true, paramType = "query")
    })
    @PostMapping("/submit_or_revocation")
    public CommonResult submitDemand(@RequestBody List<String> idList,Integer submitState) {

        UpdateWrapper updateWrapper=new UpdateWrapper();
        updateWrapper.set("submit_state",submitState);//设置提报状态
        updateWrapper.in("id",idList);
        boolean update = hotDemandService.update(updateWrapper);
        if (update) return CommonResult.success(ResultCode.SUCCESS);
        return CommonResult.failed();
    }
//
//    @ApiOperation(value = "检查长周期", notes = "检查长周期")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query")
//    })
//    @PostMapping("/check_long_product")
//    public CommonResult checkLongProduct(@RequestBody List<String> idList) {
//        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
//
//        //毛坯需求数据
//
//
//        QueryWrapper<HotLongProduct> longWrapper=new QueryWrapper();
//        longWrapper.eq("tenant_id",currentUser.getTenantId());
//        longWrapper.in("product_drawing_no",);
//        List<HotLongProduct> list = hotLongProductService.list(longWrapper);
//        //长周期产品
//        Map<String, HotLongProduct> longMap = list.stream().collect(Collectors.toMap(x -> x.getProductDrawingNo(), x -> x));
//
//
//        //遍历毛坯需求数据,根据图号在长周期产品map中获取,不为空则为长周期产品
//
//
//        //收集长周期产品的毛坯需求id
//
//
//        UpdateWrapper updateWrapper=new UpdateWrapper();
//        updateWrapper.set("is_long_period",1);//设置为长周期
//        updateWrapper.in("id",);
//        boolean update = hotDemandService.update(updateWrapper);
//        if (update) return CommonResult.success(ResultCode.SUCCESS);
//        return CommonResult.failed();
//    }
//
//
//    @ApiOperation(value = "检查模型", notes = "检查模型")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query")
//    })
//    @PostMapping("/check_model")
//    public CommonResult checkModel(@RequestBody List<String> idList) {
//        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
//
//        //毛坯需求数据
//
//
//        QueryWrapper<HotModelStore> modelWrapper=new QueryWrapper();
//        modelWrapper.eq("tenant_id",currentUser.getTenantId());
//        modelWrapper.in("model_drawing_no",);
//        List<HotModelStore> list = hotModelStoreService.list(modelWrapper);
//        //模型
//        Map<String, HotModelStore> ModelMap = list.stream().collect(Collectors.toMap(x -> x.getModelDrawingNo(), x -> x));
//
//
//        //遍历毛坯需求数据,根据图号在模型map中获取,不为空则有模型
//
//
//        //收集有模型的毛坯需求id和无模型的id
//
//
//        UpdateWrapper updateWrapper=new UpdateWrapper();
//        updateWrapper.set("is_exist_model",1);//设置为有模型
//        updateWrapper.in("id",);
//        boolean update = hotDemandService.update(updateWrapper);
//        if (update) return CommonResult.success(ResultCode.SUCCESS);
//        return CommonResult.failed();
//    }






}
