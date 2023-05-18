package com.richfit.mes.produce.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.HotDemandParam;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
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

    @Resource
    public WmsServiceClient wmsServiceClient;

    @Resource
    private BaseServiceClient baseServiceClient;


    @ApiOperation(value = "新增需求提报", notes = "新增需求提报")
    @PostMapping("/save")
    public CommonResult saveDemand(@RequestBody HotDemand hotDemand) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        hotDemand.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        hotDemand.setSubmitOrderTime(new Date());
        hotDemand.setSubmitById(currentUser.getUserId());
        hotDemand.setSubmitOrderOrg(currentUser.getOrgId());
        hotDemand.setSubmitOrderOrgId(currentUser.getBelongOrgId());
        boolean save = hotDemandService.save(hotDemand);
        if (save) {
            return CommonResult.success(ResultCode.SUCCESS);
        } else {
            return CommonResult.failed(ResultCode.FAILED);
        }
    }

    @ApiOperation(value = "需求提报列表查询", notes = "需求提报列表查询")
    @PostMapping("/demand_page")
    public CommonResult<IPage<HotDemand>> demandPage(@RequestBody HotDemandParam hotDemandParam) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<HotDemand>();
        //queryWrapper.eq("tenant_id", currentUser.getTenantId());
        if (StringUtils.isNotEmpty(hotDemandParam.getBranchCode())) {//车间代码
            queryWrapper.eq("branch_code", hotDemandParam.getBranchCode());
        }
        if (StringUtils.isNotEmpty(hotDemandParam.getProjectName())) {//项目名称
            queryWrapper.eq("project_name", hotDemandParam.getProjectName());
        }
        if (StringUtils.isNotEmpty(hotDemandParam.getWorkNo())) {//工作号
            queryWrapper.eq("work_no", hotDemandParam.getWorkNo());
        }
        if (StringUtils.isNotEmpty(hotDemandParam.getDrawNo())) {//图号
            queryWrapper.eq("draw_no", hotDemandParam.getDrawNo());
        }
        if (StringUtils.isNotEmpty(hotDemandParam.getVoucherNo())) {//凭证号
            queryWrapper.eq("voucher_no", hotDemandParam.getVoucherNo());
        }
        if (StringUtils.isNotEmpty(hotDemandParam.getSubmitOrderOrg())) {//提单单位
            queryWrapper.eq("submit_order_org_id", hotDemandParam.getSubmitOrderOrg());
        }
        if (StringUtils.isNotEmpty(hotDemandParam.getWorkblankType())) {//毛坯类型
            queryWrapper.eq("workblank_type", hotDemandParam.getWorkblankType());
        } else {
            queryWrapper.notIn("workblank_type", "2");
        }
        if (hotDemandParam.getIsExistProcess() != null) {//有无工艺
            queryWrapper.eq("is_exist_process", hotDemandParam.getIsExistProcess());
        }
        if (hotDemandParam.getIsExistModel() != null) {//有无模型
            queryWrapper.eq("is_exist_model", hotDemandParam.getIsExistModel());
        }
        if (hotDemandParam.getProduceState() != null) {//是否排产
            queryWrapper.eq("produce_state", hotDemandParam.getProduceState());
        }
        if (hotDemandParam.getIngotCase() != null) {//锭型
            queryWrapper.eq("ingot_case", hotDemandParam.getIngotCase());
        }
        if (hotDemandParam.getTexture() != null) {//材质
            queryWrapper.eq("texture", hotDemandParam.getTexture());
        }
        //0 :未提报  1 :已提报'
        if (hotDemandParam.getSubmitState() != null) {
            queryWrapper.eq("submit_state", hotDemandParam.getSubmitState());
        }
        //0 :未批准 ,1 已批准',
        if (hotDemandParam.getProduceRatifyState() != null) {
            queryWrapper.eq("produce_ratify_state", hotDemandParam.getProduceRatifyState());
        }


        //需求日期
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(hotDemandParam.getDemandStartTime() == null ? "" : hotDemandParam.getDemandStartTime().toString())) {
            queryWrapper.ge("demand_time", DateUtils.getStartOfDay(hotDemandParam.getDemandStartTime()));
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(hotDemandParam.getDemandEndTime() == null ? "" : hotDemandParam.getDemandEndTime().toString())) {
            queryWrapper.le("demand_time", DateUtils.getEndOfDay(hotDemandParam.getDemandEndTime()));
        }
        //提单日期
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(hotDemandParam.getSubmitStartTime() == null ? "" : hotDemandParam.getSubmitStartTime().toString())) {
            queryWrapper.ge("submit_order_time", DateUtils.getStartOfDay(hotDemandParam.getSubmitStartTime()));
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(hotDemandParam.getSubmitEndTime() == null ? "" : hotDemandParam.getSubmitEndTime().toString())) {
            queryWrapper.le("submit_order_time", DateUtils.getEndOfDay(hotDemandParam.getSubmitEndTime()));
        }

        if (StringUtils.isNotEmpty(hotDemandParam.getOrderByColumns())) {//多字段排序
            queryWrapper.orderByAsc(hotDemandParam.getOrderByColumns());
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
        if (b) {
            return CommonResult.success(ResultCode.SUCCESS);
        } else {
            return CommonResult.failed(ResultCode.FAILED);
        }
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

    @ApiOperation(value = "导入需求提报(冶炼车间)", notes = "根据Excel文档导入导入需求提报(冶炼车间)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/import_demand_YL")
    public CommonResult importExcelDemandYL(HttpServletRequest request, @RequestParam("file") MultipartFile file, String branchCode) {
        return hotDemandService.importDemandYL(file, branchCode);
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
            if (hotDemand.getSubmitState() != null & hotDemand.getSubmitState() == 1) {
                throw new GlobalException("已提报的不能删除", ResultCode.FAILED);
            }
        }
        boolean b = hotDemandService.removeByIds(idList);
        if (b == true) {
            return CommonResult.success(ResultCode.SUCCESS);
        }
        return CommonResult.failed();
    }


    @ApiOperation(value = "需求提报与撤回", notes = "需求提报与撤回")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "submitState", value = "提报状态 0 :未提报  1 :已提报  2:需求退回", required = true, paramType = "query")
    })
    @PostMapping("/submit_or_revocation")
    public CommonResult submitDemand(@RequestBody List<String> idList, Integer submitState) {
        //需求撤回需要校验是否被热工确认,已经确认的不能撤回
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        List<HotDemand> demands = hotDemandService.list(queryWrapper);
        for (HotDemand demand : demands) {
            if (demand.getProduceRatifyState() != null && demand.getProduceRatifyState() == 1) {
                return CommonResult.failed(demand.getDemandName() + " 已经批准生产,不可撤回");
            }
        }

        UpdateWrapper<HotDemand> updateWrapper = new UpdateWrapper();
        updateWrapper.set("submit_state", submitState);//设置提报状态
        updateWrapper.in("id", idList);
        boolean update = hotDemandService.update(updateWrapper);
        if (update) {
            return CommonResult.success(ResultCode.SUCCESS);
        }
        return CommonResult.failed();
    }

    @ApiOperation(value = "检查长周期", notes = "检查长周期")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/check_long_product")
    public CommonResult checkLongProduct(@RequestBody List<String> idList, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        //长周期字段为空的毛坯需求数据
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        queryWrapper.eq("tenant_id", currentUser.getTenantId());
//        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.apply("is_long_period is null or is_long_period=0");
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        List<String> drawNos = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(drawNos)) {
            return CommonResult.success("所有均已校验完成");
        }
        //根据需求图号查询长周期产品库
        QueryWrapper<HotLongProduct> longWrapper = new QueryWrapper();
        longWrapper.eq("tenant_id", currentUser.getTenantId());
        longWrapper.in("product_drawing_no", drawNos);
        List<HotLongProduct> list = hotLongProductService.list(longWrapper);
        //长周期产品数据
        Map<String, HotLongProduct> longMap = list.stream().collect(Collectors.toMap(x -> x.getProductDrawingNo(), x -> x));

        List<String> ids = new ArrayList<>();
        //遍历毛坯需求数据,根据图号在长周期产品map中获取,不为空则为长周期产品
        for (HotDemand hotDemand : hotDemands) {
            HotLongProduct hotLongProduct = longMap.get(hotDemand.getDrawNo());
            if (ObjectUtils.isNotEmpty(hotLongProduct)) {
                //收集长周期产品的毛坯需求id
                ids.add(hotDemand.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(ids)) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            //设置为长周期
            updateWrapper.set("is_long_period", 1);
            updateWrapper.in("id", ids);
            boolean update = hotDemandService.update(updateWrapper);
            if (update) {
                return CommonResult.success(ResultCode.SUCCESS);
            }
            return CommonResult.failed();
        } else {
            return CommonResult.success("操作成功");
        }
    }


    @ApiOperation(value = "检查模型", notes = "检查模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/check_model")
    public CommonResult checkModel(@RequestBody List<String> idList, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        //无模型或者模型字段为空的毛坯需求数据
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        queryWrapper.eq("tenant_id", currentUser.getTenantId());
//        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.apply("(is_exist_model=0 or is_exist_model is null)");
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        List<String> drawNos = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(drawNos)) {
            return CommonResult.success("所有均已校验完成");
        }
        //根据需求数据中的图号查询模型库
        QueryWrapper<HotModelStore> modelWrapper = new QueryWrapper();
        modelWrapper.eq("tenant_id", currentUser.getTenantId());
        modelWrapper.in("model_drawing_no", drawNos);
        List<HotModelStore> list = hotModelStoreService.list(modelWrapper);
        //模型map<图号@版本号,模型数据>
        Map<String, HotModelStore> ModelMap = list.stream().collect(Collectors.toMap(x -> x.getModelDrawingNo() + "@" + x.getVersion(), x -> x));

        List<String> ids = new ArrayList<>();
        //遍历毛坯需求数据,根据图号在模型map中获取,不为空则有模型
        for (HotDemand hotDemand : hotDemands) {
            //根据图号+@+版本号去获取模型
            HotModelStore hotModelStore = ModelMap.get(hotDemand.getDrawNo() + "@" + hotDemand.getVersionNum());
            //模型不为空且模型数量大于0判断为有模型
            if (ObjectUtils.isNotEmpty(hotModelStore) && hotModelStore.getNormalNum() > 0) {
                //收集有模型的毛坯需求id
                ids.add(hotDemand.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(ids)) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("is_exist_model", 1);//设置为有模型
            updateWrapper.in("id", ids);
            boolean update = hotDemandService.update(updateWrapper);
            if (update) {
                return CommonResult.success(ResultCode.SUCCESS);
            }
            return CommonResult.failed();
        } else {
            return CommonResult.success("操作成功");
        }
    }

    @ApiOperation(value = "检查外协件", notes = "检查外协件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query")
    })
    @PostMapping("/check_outsource")
    public CommonResult checkOutsource(@RequestBody List<String> idList) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        try {
            //检查毛坯需求中{材质}字段，如果字段信息前几个字母包含“ZCU”、“HT”、“精ZG”任何一项，标记为外协件产品。
            UpdateWrapper<HotDemand> updateWrapper = new UpdateWrapper();
            updateWrapper.set("is_outsource", 1);//设置外协件
            updateWrapper.eq("tenant_id", currentUser.getTenantId());
            updateWrapper.and(wrapper -> wrapper.likeRight("texture", "ZCU").or().likeRight("texture", "HT").or().likeRight("texture", "精ZG"));
            hotDemandService.update(updateWrapper);
            return CommonResult.success(ResultCode.SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage());
            return CommonResult.failed();
        }
    }


    @ApiOperation(value = "核对库存", notes = "核对库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query")
    })
    @PostMapping("/check_repertory")
    public CommonResult checkRepertory(@RequestBody List<String> idList) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        queryWrapper.eq("tenant_id", currentUser.getTenantId());
        queryWrapper.apply("(is_exist_repertory=0 or is_exist_repertory is null)");
        //查询出没有库存的数据
        List<HotDemand> list = hotDemandService.list(queryWrapper);
        for (HotDemand hotDemand : list) {
            if (StringUtils.isNotEmpty(hotDemand.getErpProductCode())) {
                //库存数量
                Integer count = wmsServiceClient.queryMaterialCount(hotDemand.getErpProductCode()).getData();
                if (count > 0) {
                    UpdateWrapper<HotDemand> updateWrapper = new UpdateWrapper();
                    updateWrapper.set("repertory_num", count);//设置库存数量
                    updateWrapper.set("is_exist_repertory", 1);//设置为已有库存
                    updateWrapper.in("id", hotDemand.getId());
                    hotDemandService.update(updateWrapper);
                }
            }
        }
        return CommonResult.success("操作成功");
    }


    @ApiOperation(value = "检查工艺", notes = "检查工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/check_router")
    public CommonResult checkRouter(@RequestBody List<String> idList, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        //是否有工艺字段为空的毛坯需求数据
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        queryWrapper.eq("tenant_id", currentUser.getTenantId());
        queryWrapper.apply("(is_exist_process=0 or is_exist_process is null)");
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        if (CollectionUtils.isEmpty(hotDemands)) {
            return CommonResult.success("所有均已校验完成");
        }
        List<String> drawNos = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());
        //根据需求图号查询工艺库
        CommonResult<List<Router>> byDrawNo = baseServiceClient.getByDrawNo(drawNos, branchCode);
        //工艺库数据
        Map<String, Router> routerMap = byDrawNo.getData().stream().collect(Collectors.toMap(x -> x.getDrawNo(), x -> x));

        //遍历毛坯需求数据,根据图号在工艺map中获取,不为空则有工艺
        for (HotDemand hotDemand : hotDemands) {
            Router router = routerMap.get(hotDemand.getDrawNo());
            if (ObjectUtils.isNotEmpty(router)) {
                //把有工艺的需求数据状态进行修改
                UpdateWrapper updateWrapper = new UpdateWrapper();
                updateWrapper.set("is_exist_process", 1);//设置为有工艺
                updateWrapper.set("texture", router.getTexture());//设置材质
                //updateWrapper.set("workblank_type", );//设置毛坯类型
                updateWrapper.set("steel_water_weight", router.getWeightMolten());//设置钢水重量
                updateWrapper.set("piece_weight", router.getPieceWeight());//设置单重
                updateWrapper.set("weight", router.getForgWeight());//设置重量
                updateWrapper.eq("id", hotDemand.getId());
                hotDemandService.update(updateWrapper);
            }
        }
        return CommonResult.success("操作成功");
    }


    @ApiOperation(value = "一键检查", notes = "一键检查")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/one_check")
    public CommonResult oneCheck(@RequestBody List<String> idList, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        try {
            this.checkLongProduct(idList, branchCode);//校验长周期
            this.checkModel(idList, branchCode);//检查模型
            this.checkOutsource(idList);//检查外协件
            this.checkRepertory(idList);//核对库存
            this.checkRouter(idList, branchCode);//工艺检查
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException("一键检查异常", ResultCode.FAILED);
        }
        return CommonResult.success("操作成功");

    }


    @ApiOperation(value = "生产批准", notes = "生产批准")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "ratifyState", value = "生产批准状态 0 :未批准 ,1 已批准", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/ratify")
    public CommonResult ratify(@RequestBody List<String> idList, Integer ratifyState, String branchCode) {
        return hotDemandService.ratify(idList, ratifyState, branchCode);
    }

    @ApiOperation(value = "冶炼车间生产批准", notes = "冶炼车间生产批准")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "ratifyState", value = "生产批准状态 0 :未批准 ,1 已批准", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/ratify_yl")
    public CommonResult ratifyYL(@RequestBody List<String> idList, Integer ratifyState, String branchCode) {
        return hotDemandService.ratifyYL(idList, ratifyState, branchCode);
    }

    @ApiOperation(value = "撤销批准", notes = "撤销批准")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query")
    })
    @PostMapping("/revocation")
    public CommonResult revocation(@RequestBody List<String> idList) {
        return hotDemandService.revocation(idList);
    }


    @ApiOperation(value = "模型排产", notes = "模型排产")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/model_production_scheduling")

    public CommonResult modelProductionScheduling(@RequestBody List<String> idList, String branchCode) {
        return hotDemandService.modelProductionScheduling(idList, branchCode);
    }


    @ApiOperation(value = "自动生成工序计划", notes = "自动生成工序计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/create_plan_node")
    public CommonResult initPlanNode(@RequestBody List<String> idList, String branchCode) {
        return hotDemandService.initPlanNode(idList, branchCode);
    }


    @ApiOperation(value = "设置优先级", notes = "设置优先级")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "priority", value = "优先级 :高 ,中, 低", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/set_priority")
    public CommonResult setPriority(@RequestBody List<String> idList, String priority) {

        UpdateWrapper<HotDemand> updateWrapper = new UpdateWrapper();
        updateWrapper.set("priority", priority);//优先级: 高 ,中, 低
        updateWrapper.in("id", idList);
        boolean update = hotDemandService.update(updateWrapper);
        if (update) {
            return CommonResult.success(ResultCode.SUCCESS);
        } else {
            return CommonResult.failed();
        }
    }


}
