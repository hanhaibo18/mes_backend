package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.OperationTypeSpec;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.HotDemandParam;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.utils.DateUtils;
import com.richfit.mes.produce.utils.OrderUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Autowired
    private PlanService planService;
    @Autowired
    private HotPlanNodeService planNodeService;



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
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<HotDemand>();
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
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
            @ApiImplicitParam(name = "submitState", value = "提报状态 0 :未提报  1 :已提报  2:需求退回", required = true, paramType = "query")
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

    @ApiOperation(value = "检查长周期", notes = "检查长周期")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/check_long_product")
    public CommonResult checkLongProduct(@RequestBody List<String> idList,String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        //长周期字段为空的毛坯需求数据
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("id",idList);
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.eq("branch_code",branchCode);
        queryWrapper.apply("is_long_period is null");
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        List<String> drawNos = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(drawNos))   return CommonResult.success("所有均已校验完成");
        //根据需求图号查询长周期产品库
        QueryWrapper<HotLongProduct> longWrapper=new QueryWrapper();
        longWrapper.eq("tenant_id",currentUser.getTenantId());
        longWrapper.in("product_drawing_no",drawNos);
        List<HotLongProduct> list = hotLongProductService.list(longWrapper);
        //长周期产品数据
        Map<String, HotLongProduct> longMap = list.stream().collect(Collectors.toMap(x -> x.getProductDrawingNo(), x -> x));

        List<String> ids=new ArrayList<>();
        //遍历毛坯需求数据,根据图号在长周期产品map中获取,不为空则为长周期产品
        for (HotDemand hotDemand : hotDemands) {
            HotLongProduct hotLongProduct = longMap.get(hotDemand.getDrawNo());
            if (ObjectUtils.isNotEmpty(hotLongProduct)){
                //收集长周期产品的毛坯需求id
                ids.add(hotDemand.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(ids)){
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.set("is_long_period",1);//设置为长周期
            updateWrapper.in("id",ids);
            boolean update = hotDemandService.update(updateWrapper);
            if (update) return CommonResult.success(ResultCode.SUCCESS);
            return CommonResult.failed();
        }else {
            return CommonResult.success("操作成功");
        }
    }


    @ApiOperation(value = "检查模型", notes = "检查模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/check_model")
    public CommonResult checkModel(@RequestBody List<String> idList,String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        //无模型或者模型字段为空的毛坯需求数据
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("id",idList);
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.eq("branch_code",branchCode);
        queryWrapper.apply("is_exist_model is null");
        queryWrapper.apply("(is_exist_model=0 or is_exist_model is null)");
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        List<String> drawNos = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(drawNos))  return CommonResult.success("所有均已校验完成");
        //根据需求数据中的图号查询模型库
        QueryWrapper<HotModelStore> modelWrapper=new QueryWrapper();
        modelWrapper.eq("tenant_id",currentUser.getTenantId());
        modelWrapper.in("model_drawing_no",drawNos);
        List<HotModelStore> list = hotModelStoreService.list(modelWrapper);
        //模型
        Map<String, HotModelStore> ModelMap = list.stream().collect(Collectors.toMap(x -> x.getModelDrawingNo(), x -> x));

        List<String> ids=new ArrayList<>();
        //遍历毛坯需求数据,根据图号在模型map中获取,不为空则有模型
        for (HotDemand hotDemand : hotDemands) {
            HotModelStore hotModelStore = ModelMap.get(hotDemand.getDrawNo());
            if (ObjectUtils.isNotEmpty(hotModelStore)){
                //收集有模型的毛坯需求id
                ids.add(hotDemand.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(ids)){
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.set("is_exist_model",1);//设置为有模型
            updateWrapper.in("id",ids);
            boolean update = hotDemandService.update(updateWrapper);
            if (update) return CommonResult.success(ResultCode.SUCCESS);
            return CommonResult.failed();
        }else {
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
            UpdateWrapper<HotDemand> updateWrapper=new UpdateWrapper();
            updateWrapper.set("is_outsource",1);//设置外协件
            updateWrapper.eq("tenant_id",currentUser.getTenantId());
            updateWrapper.and(wrapper -> wrapper.likeRight("texture","ZCU").or().likeRight("texture","HT").or().likeRight("texture","精ZG"));
            hotDemandService.update(updateWrapper);
            return CommonResult.success(ResultCode.SUCCESS);
        }catch (Exception e){
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
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("id",idList);
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.and(wapper-> wapper.eq("is_exist_repertory",0).or().eq("is_exist_repertory",null));
        //查询出没有库存的数据
        List<HotDemand> list = hotDemandService.list(queryWrapper);
        for (HotDemand hotDemand : list) {
            if(StringUtils.isNotEmpty(hotDemand.getErpProductCode())){
                //库存数量
                Integer count = wmsServiceClient.queryMaterialCount(hotDemand.getErpProductCode()).getData();
                if (count>0){
                    UpdateWrapper<HotDemand> updateWrapper=new UpdateWrapper();
                    updateWrapper.set("repertory_num",count);//设置库存数量
                    updateWrapper.set("is_exist_repertory",1);//设置为已有库存
                    updateWrapper.in("id",hotDemand.getId());
                    boolean update = hotDemandService.update(updateWrapper);
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
    public CommonResult checkRouter(@RequestBody List<String> idList,String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        //是否有工艺字段为空的毛坯需求数据
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
            queryWrapper.in("id",idList);
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.eq("branch_code",branchCode);
        queryWrapper.apply("(is_exist_process=0 or is_exist_process is null)");
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        List<String> drawNos = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(drawNos))   return CommonResult.success("所有均已校验完成");
        //根据需求图号查询工艺库
        CommonResult<List<Router>> byDrawNo = baseServiceClient.getByDrawNo(drawNos, branchCode);
        //工艺库数据
        Map<String, Router> routerMap = byDrawNo.getData().stream().collect(Collectors.toMap(x -> x.getDrawNo(), x -> x));

        List<String> ids=new ArrayList<>();
        //遍历毛坯需求数据,根据图号在工艺map中获取,不为空则有工艺
        for (HotDemand hotDemand : hotDemands) {
            Router router = routerMap.get(hotDemand.getDrawNo());
            if (ObjectUtils.isNotEmpty(router)){
                //收集有模型的毛坯需求id
                ids.add(hotDemand.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(ids)){
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.set("is_exist_process",1);//设置为有工艺
            updateWrapper.in("id",ids);
            boolean update = hotDemandService.update(updateWrapper);
            if (update) return CommonResult.success(ResultCode.SUCCESS);
            return CommonResult.failed();
        }else {
            return CommonResult.success("操作成功");
        }
    }


    @ApiOperation(value = "一键检查", notes = "一键检查")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/one_check")
    public CommonResult oneCheck(@RequestBody List<String> idList,String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        try {
            this.checkLongProduct(idList,branchCode);//校验长周期
            this.checkModel(idList,branchCode);//检查模型
            this.checkOutsource(idList);//检查外协件
            this.checkRepertory(idList);//核对库存
            this.checkRouter(idList,branchCode);//工艺检查
        }catch (Exception e){
            log.error(e.getMessage());
            throw  new GlobalException("一键检查异常",ResultCode.FAILED);
        }
        return CommonResult.success("操作成功");

    }


    @ApiOperation(value = "生产批准与撤销批准", notes = "生产批准与撤销批准")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "ratifyState", value = "生产批准状态 0 :未批准 ,1 已批准", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/ratify")
    public CommonResult ratify(@RequestBody List<String> idList,Integer ratifyState,String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //检查无模型数据
        List<String> ids = hotDemandService.checkModel(idList, branchCode);
        if(CollectionUtils.isNotEmpty(ids)) return CommonResult.failed("存在无模型需求");

        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("branch_code",branchCode);
        queryWrapper.in("id",idList);
        //无模型"且“未排产”产品
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        //将需求数据转换为生产计划并入库
        this.convertAndSave(currentUser, hotDemands);
        UpdateWrapper updateWrapper=new UpdateWrapper();
        updateWrapper.set("produce_ratify_state",ratifyState);//设置提报状态
        updateWrapper.in("id",idList);
        boolean update = hotDemandService.update(updateWrapper);
        if (update) return CommonResult.success(ResultCode.SUCCESS);
        return CommonResult.failed();
    }


    @ApiOperation(value = "模型排产", notes = "模型排产")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/model_production_scheduling")
    public CommonResult ratify(@RequestBody List<String> idList,String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.eq("branch_code",branchCode);
        queryWrapper.apply("(is_exist_model=0 or is_exist_process is null)");
        queryWrapper.in("id",idList);
        //无模型"且“未排产”产品
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        //将需求数据转换为生产计划并入库
        this.convertAndSave(currentUser, hotDemands);
        UpdateWrapper updateWrapper=new UpdateWrapper();
        updateWrapper.set("produce_state",1);//设置排产状态 0: 未排产   1 :已排产',
        updateWrapper.in("id",idList);
        boolean update = hotDemandService.update(updateWrapper);
        if (update) return CommonResult.success(ResultCode.SUCCESS);

        return CommonResult.failed();
    }

    /**
     * 将需求数据转换为生产计划并入库
     * @param currentUser
     * @param hotDemands
     */

    private void convertAndSave(TenantUserDetails currentUser, List<HotDemand> hotDemands) {
        ArrayList<Plan> plans = new ArrayList<>();
        //根据需求信息自动生成生产计划数据
        for (HotDemand hotDemand : hotDemands) {
            Plan plan = new Plan();
            plan.setProjCode(DateUtils.formatDate(new Date(),"yyyy-MM"));
            plan.setWorkNo(hotDemand.getWorkNo());//工作号
            plan.setDrawNo(hotDemand.getDrawNo());//图号
            plan.setProjNum(hotDemand.getPlanNum());//计划数量
            plan.setStoreNumber(hotDemand.getRepertoryNum());//库存数量
            plan.setBranchCode(hotDemand.getBranchCode());//车间码
            plan.setTenantId(hotDemand.getTenantId());//租户id
            plan.setInchargeOrg(hotDemand.getInchargeOrg());//加工车间
            plan.setStatus(0);//状态 0未开始 1进行中 2关闭 3已完成
            plan.setTexture(hotDemand.getTexture());//材质
            plan.setBlank(hotDemand.getWorkblankType());//毛坯
            plan.setStartTime(new Date());//开始时间
            plan.setEndTime(hotDemand.getPlanEndTime());//结束时间
            plan.setAlarmStatus(0);//预警状态 0正常  1提前 2警告 3延期
            plan.setCreateBy(currentUser.getUserId());//创建人
            plan.setCreateTime(new Date());
            plan.setModifyBy(currentUser.getUserId());
            plan.setModifyTime(new Date());
            plan.setDrawNoName("");//图号名称
            plans.add(plan);
        }
        planService.saveBatch(plans);
    }
    @ApiOperation(value = "自动生成工序计划", notes = "自动生成工序计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "需求提报IdList", required = true, paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构编码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/create_plan_node")
    public CommonResult initPlanNode(@RequestBody List<String> idList,String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.eq("branch_code",branchCode);
        queryWrapper.in("id",idList);
        //查出需求信息
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        List<String> drawNoList = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());

        //根据图号查出工艺信息
        List<Router> byDrawNo = baseServiceClient.getByDrawNo(drawNoList, branchCode).getData();
        if(CollectionUtils.isEmpty(byDrawNo)) return CommonResult.failed("没有工艺信息");
        List<String> routerIdList = byDrawNo.stream().map(x -> x.getId()).collect(Collectors.toList());
        Map<String, String> routerIdMap = byDrawNo.stream().collect(Collectors.toMap(x -> x.getDrawNo(), x -> x.getId()));
        //根据工艺id查出工序信息
        List<Sequence> sequences = baseServiceClient.querySequenceByRouterIds(routerIdList);
        if(CollectionUtils.isEmpty(sequences)) return CommonResult.failed("工艺没有工序信息");
        //根据工艺id分组
        Map<String, List<Sequence>> sequencesMap = sequences.stream().collect(Collectors.groupingBy(Sequence::getRouterId));
        ArrayList<HotPlanNode> planNodes = new ArrayList<>();
        //根据需求信息自动生成生产计划数据
        for (HotDemand hotDemand : hotDemands) {
            String s = routerIdMap.get(hotDemand.getDrawNo());
            if (StringUtils.isNotEmpty(s)){
                //有工艺的情况下
                List<Sequence> sequencesList = sequencesMap.get(s);
                if(CollectionUtils.isNotEmpty(sequencesList)){
                    //工艺有工序的情况
                    for (Sequence sequence : sequencesList) {
                        HotPlanNode planNode = new HotPlanNode();
                        planNode.setDemandId(hotDemand.getId());//毛坯需求id
                        planNode.setOptName(sequence.getOptName());//工序名称
                        planNode.setDemandNum(hotDemand.getNum());//需求数量
                        planNode.setOptStatus("0");//工序状态 0:未开始,1: 进行中,2:已结束
                        planNode.setBranchCode(hotDemand.getBranchCode());//车间码
                        planNode.setTenantId(hotDemand.getTenantId());//租户id
                        planNodes.add(planNode);
                    }

                }
            }
        }
        planNodeService.saveBatch(planNodes);
        return CommonResult.success("操作成功");
    }

}
