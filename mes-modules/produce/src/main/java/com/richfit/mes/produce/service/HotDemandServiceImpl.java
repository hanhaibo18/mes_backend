package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.Operatipon;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.store.PlanExtend;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.HotDemandMapper;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.entity.DemandExcel;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class HotDemandServiceImpl extends ServiceImpl<HotDemandMapper, HotDemand> implements HotDemandService {
    @Resource
    private HotDemandService hotDemandService;
    @Resource
    public HotModelStoreService hotModelStoreService;
    @Autowired
    private PlanService planService;
    @Resource
    private PlanExtendService planExtendService;
    @Autowired
    private TrackHeadMapper trackHeadMapper;
    @Resource
    private BaseServiceClient baseServiceClient;

    @Autowired
    private HotPlanNodeService planNodeService;

    @Resource
    private SystemServiceClient systemServiceClient;
    /**
     * 导入需求提报数据
     * @param file
     * @param branchCode
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult importDemand(MultipartFile file, String branchCode) {
        CommonResult result = null;
        String submitOrderOrg = hotDemandService.getSubmitOrderOrg(branchCode, SecurityUtils.getCurrentUser());
        java.lang.reflect.Field[] fields = DemandExcel.class.getDeclaredFields();

        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));

        excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());

        //将导入的excel数据生成实体类list
        List<DemandExcel> list = null;
        try {
            file.transferTo(excelFile);
            list = ExcelUtils.importExcel(excelFile, DemandExcel.class, fieldNames, 1, 0, 0, tempName.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        ArrayList<HotDemand> demandList = new ArrayList<>();
        for (DemandExcel hemandExcel : list) {
            //丰富基础数据
            HotDemand hotDemand = new HotDemand();
            //属性拷贝
            BeanUtils.copyProperties(hemandExcel,hotDemand);
            hotDemand.setTenantId(currentUser.getTenantId());
            hotDemand.setCreateBy(currentUser.getUsername());
            hotDemand.setSubmitOrderTime(new Date());
            hotDemand.setSubmitBy(currentUser.getUsername());
            hotDemand.setSubmitById(currentUser.getUserId());
            hotDemand.setBranchCode(branchCode);
            hotDemand.setCreateTime(new Date());
            hotDemand.setSubmitState(0);
            hotDemand.setSubmitOrderOrg(submitOrderOrg);
            hotDemand.setSubmitOrderOrgId(currentUser.getBelongOrgId());
            hotDemand.setPlanNum(hotDemand.getNum());
            //0锻件,1铸件,2钢锭
            if(StringUtils.isNotEmpty(hotDemand.getWorkblankType())){
                switch (hotDemand.getWorkblankType()){
                    case "锻件":  hotDemand.setWorkblankType("0");//锻造车间
                        break;
                    case "铸件":  hotDemand.setWorkblankType("1");//铸造
                        break;
                    case "钢锭":  hotDemand.setWorkblankType("2");//冶炼
                        break;
                    default: throw new GlobalException("导入失败毛坯类型: "+hotDemand.getWorkblankType()+"超出范围(锻件 ,铸件 , 钢锭)", ResultCode.FAILED);
                }
            }
            //查重
            //hotDemandService.checkDemand(hotDemand.getWorkNo(),hotDemand.getDrawNo(),hotDemand.getVersionNum());
            this.save(hotDemand);
            //demandList.add(hotDemand);
        }

        //this.saveBatch(demandList);
        return CommonResult.success("");
    }

    /**
     * 导入需求提报数据(冶炼车间)
     * @param file
     * @param branchCode
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult importDemandYL(MultipartFile file, String branchCode) {
        String submitOrderOrg = hotDemandService.getSubmitOrderOrg(branchCode, SecurityUtils.getCurrentUser());
        //sheet计划列表
        String[] fieldNames = {"materialName","erpProductCode","texture","num","ingotCase","planEndTime","inchargeOrg","demandTime","projectName","workNo","drawNo","demandName","priority","versionNum","remark"};
        File excelFile = null;

        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
        //将导入的excel数据生成实体类list
        List<HotDemand> list = null;
        try {
            file.transferTo(excelFile);
            list = ExcelUtils.importExcel(excelFile, HotDemand.class, fieldNames, 1, 0, 0, tempName.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        ArrayList<HotDemand> demandList = new ArrayList<>();
        for (HotDemand hotDemand : list) {
            //丰富基础数据
            hotDemand.setTenantId(currentUser.getTenantId());
            hotDemand.setCreateBy(currentUser.getUsername());
            hotDemand.setSubmitOrderTime(new Date());
            hotDemand.setSubmitBy(currentUser.getUsername());
            hotDemand.setSubmitById(currentUser.getUserId());
            hotDemand.setBranchCode(branchCode);
            hotDemand.setCreateTime(new Date());
            hotDemand.setSubmitState(0);
            hotDemand.setSubmitOrderOrg(submitOrderOrg);
            hotDemand.setSubmitOrderOrgId(currentUser.getBelongOrgId());
            hotDemand.setPlanNum(hotDemand.getNum());
            //0锻件,1铸件,2钢锭
            hotDemand.setWorkblankType("2");//冶炼
            //查重
            //hotDemandService.checkDemand(hotDemand.getWorkNo(),hotDemand.getDrawNo(),hotDemand.getVersionNum());
            demandList.add(hotDemand);
        }
        this.saveBatch(demandList);
        return CommonResult.success("");
    }


    /**
     * 检查模型
      * @param idList
     * @param branchCode
     * @return
     */
    @Override
    public List<String> checkModel(List<String> idList, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        //无模型或者模型字段为空的毛坯需求数据
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("id",idList);
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.apply("(is_exist_model=0 or is_exist_model is null)");
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        List<String> drawNos = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(drawNos)) {
            return new ArrayList<>();
        }
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
            if (ObjectUtils.isEmpty(hotModelStore)){
                //收集无模型的毛坯需求id
                ids.add(hotDemand.getId());
            }
        }
        return ids;
    }

    /**
     * 生产批准
     * @param idList
     * @param ratifyState
     * @param branchCode
     * @return
     */
    @Override
    public CommonResult<?> ratify(List<String> idList, Integer ratifyState, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //查出所有需求信息
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        queryWrapper.apply("(produce_ratify_state=0 or produce_ratify_state is null)");
        //查出需求提报数据
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        if (CollectionUtils.isEmpty(hotDemands)) {
            return CommonResult.success(ResultCode.SUCCESS, "不可重复批准生产");
        } else {
            for (HotDemand hotDemand : hotDemands) {
                if(hotDemand.getPlanNum()==null || hotDemand.getPlanNum()<=0){
                    return CommonResult.failed(ResultCode.FAILED, hotDemand.getDemandName()+": 请编辑计划数量不能为0");
                }
            }
        }
        //检查无模型数据
//        List<String> ids = hotDemandService.checkModel(idList, branchCode);
//        if (CollectionUtils.isNotEmpty(ids)) {
//            return CommonResult.failed("存在无模型需求");
//        }
        for (HotDemand hotDemand : hotDemands) {
            //无工艺不可批准生产
            if(hotDemand.getIsExistProcess()==null||hotDemand.getIsExistProcess()==0){
                throw new GlobalException(hotDemand.getDemandName()+" 无工艺",ResultCode.FAILED);
           }
            //毛坯类型 0锻件,1铸件,2钢锭
            //为铸件产品时需要有模型才能批准生产
            if(hotDemand.getWorkblankType()=="1"){
                if(hotDemand.getIsExistModel()==null||hotDemand.getIsExistModel()==0){
                    throw new GlobalException(hotDemand.getDemandName()+" 无模型",ResultCode.FAILED);
                }
            }
        }
        //将需求数据转换为生产计划并入库
        Map map = this.convertAndSave(currentUser, hotDemands, 0);
        //设置需求中的计划id和批准状态
        for (HotDemand hotDemand : hotDemands) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("produce_ratify_state", ratifyState);//设置批准状态
            updateWrapper.set("issue_time", new Date());//设置下发时间
            updateWrapper.set("plan_id", map.get(hotDemand.getId()));//设置计划id
            updateWrapper.set("ratify_by", currentUser.getUsername());//批准人
            updateWrapper.set("ratify_time",  new Date());//批准时间
            updateWrapper.eq("id", hotDemand.getId());
            boolean update = hotDemandService.update(updateWrapper);
        }
        return CommonResult.success(ResultCode.SUCCESS);
    }
    /**
     * 冶炼生产批准
     * @param idList
     * @param ratifyState
     * @param branchCode
     * @return
     */
    @Override
    public CommonResult<?> ratifyYL(List<String> idList, Integer ratifyState, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //通过模型检查后查出所有需求信息
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        queryWrapper.apply("(produce_ratify_state=0 or produce_ratify_state is null)");
        //查出需求提报数据
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        if (CollectionUtils.isEmpty(hotDemands)) {
            return CommonResult.success(ResultCode.SUCCESS, "不可重复批准生产");
        } else {
            //计划数量必须大于零的校验(防止生成的计划数量为0导致通过计划添加跟单不显示的问题)
            for (HotDemand hotDemand : hotDemands) {
                if(hotDemand.getPlanNum()==null || hotDemand.getPlanNum()<=0){
                    return CommonResult.failed(ResultCode.FAILED, hotDemand.getDemandName()+": 请编辑计划数量为大于0的数字");
                }
            }
        }
        //将需求数据转换为生产计划并入库
        Map map = this.convertAndSave(currentUser, hotDemands, 0);
        //设置需求中的计划id和批准状态
        for (HotDemand hotDemand : hotDemands) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("produce_ratify_state", ratifyState);//设置批准状态
            updateWrapper.set("issue_time", new Date());//设置下发时间
            updateWrapper.set("plan_id", map.get(hotDemand.getId()));//设置计划id
            updateWrapper.set("modify_by",currentUser.getUsername());
            updateWrapper.set("ratify_by", currentUser.getUsername());//批准人
            updateWrapper.set("ratify_time",  new Date());//批准时间
            updateWrapper.eq("id", hotDemand.getId());
            boolean update = hotDemandService.update(updateWrapper);
        }
        return CommonResult.success(ResultCode.SUCCESS);
    }
    /**
     *撤销批准
     * @param idList
     */
    @Override
    public CommonResult revocation(List<String> idList) {
        //通过模型检查后查出所有需求信息
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", idList);
        //查出需求提报数据
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);

        Map<String, HotDemand> DemandMap = hotDemands.stream().collect(Collectors.toMap(x -> x.getPlanId(), x -> x));
        List<String> planIdList = hotDemands.stream().map(x -> x.getPlanId()).collect(Collectors.toList());
        //检查计划有没有跟单,跟单是否开工,开工不可回滚
        List<TrackHead> trackHeads = trackHeadMapper.selectBatchIds(planIdList);
        for (TrackHead trackHead : trackHeads) {
            if(!"0".equals(trackHead.getStatus())){
                HotDemand hotDemand = DemandMap.get(trackHead.getWorkPlanId());
                throw new GlobalException("已生成跟单并开工,名称 : "+hotDemand.getDemandName()+"不可撤销",ResultCode.FAILED);
            }
        }
        //删除对应的生产计划
        this.removPlane(hotDemands);
        //修改批准状态
        for (HotDemand hotDemand : hotDemands) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("produce_ratify_state", 0);//设置提报状态为未提报
            updateWrapper.set("issue_time",null);//设置下发时间
            updateWrapper.set("plan_id","");//设置计划id
            updateWrapper.eq("id", hotDemand.getId());
            boolean update = hotDemandService.update(updateWrapper);
        }
        return CommonResult.success(ResultCode.SUCCESS);
    }

    /**
     * 删除对应的生产计划
     * @param hotDemands
     */
    private void removPlane(List<HotDemand> hotDemands) {
        List<String> planIdList = hotDemands.stream().map(x -> x.getPlanId()).collect(Collectors.toList());
        planService.removeByIds(planIdList);
        //删除扩展字段
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.in("plan_id",planIdList);
        planExtendService.remove(queryWrapper);
    }


    /**
     *模型排产
     * @param idList
     * @param branchCode
     * @return
     */
    @Override
    public CommonResult modelProductionScheduling(List<String> idList, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", currentUser.getTenantId());
        // queryWrapper.eq("branch_code", branchCode);
        queryWrapper.apply("(produce_state=0 or produce_state is null)");
        //毛坯类型 0锻件,1铸件,2钢锭
        queryWrapper.eq("workblank_type",1);
        queryWrapper.in("id", idList);
        //无模型"且“未排产” 的铸件产品
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        if(CollectionUtils.isEmpty(hotDemands)){
            return CommonResult.success(ResultCode.SUCCESS,"所选需求均已排产");
        }
        for (HotDemand hotDemand : hotDemands) {
            if(hotDemand.getPlanNum()==null || hotDemand.getPlanNum()<=0){
                return CommonResult.failed(ResultCode.FAILED, hotDemand.getDemandName()+": 请编辑计划数量不能为0");
            }
        }
        //将需求数据转换为生产计划并入库
        Map map = this.convertAndSave(currentUser, hotDemands, 1);
        for (HotDemand hotDemand : hotDemands) {
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("produce_state", 1);//设置排产状态 0: 未排产   1 :已排产',
            updateWrapper.set("plan_id_model", map.get(hotDemand.getId()));//设置模型计划id
            updateWrapper.eq("id", hotDemand.getId());
            boolean update = hotDemandService.update(updateWrapper);
        }
        return CommonResult.success(ResultCode.SUCCESS);

    }
    /**
     * 将需求数据转换为生产计划并入库
     *
     * @param currentUser
     * @param hotDemands
     * @param branchType  1 模型车间
     */

    private Map convertAndSave(TenantUserDetails currentUser, List<HotDemand> hotDemands ,int branchType) {
        //根据需求信息自动生成生产计划数据
        Map<String,String> planIdMap=new HashMap<>();
        for (HotDemand hotDemand : hotDemands) {
            Plan plan = new Plan();
                plan.setStatus(0);//状态 0未开始 1进行中 2关闭 3已完成
                plan.setProjCode(DateUtils.formatDate(new Date(), "yyyy-MM"));//计划编号
                plan.setWorkNo(hotDemand.getWorkNo());//工作号
                plan.setDrawNo(hotDemand.getDrawNo());//图号
                plan.setProjNum(hotDemand.getPlanNum());//计划数量
                plan.setStartTime(new Date());//开始时间
                plan.setPriority("0");//优先级 0低 1中 2高
                plan.setCreateBy(currentUser.getUsername());//创建人
                plan.setCreateTime(new Date());//创建时间
                plan.setTenantId(hotDemand.getTenantId());//租户id
                plan.setSource(1);//来源  1 分公司计划  2车间计划
                //车间码处理
                this.disposeBranchCode(branchType, hotDemand, plan);

                plan.setTexture(hotDemand.getTexture());//材质
                plan.setStoreNumber(hotDemand.getRepertoryNum());//库存数量
    //            plan.setApprovalBy(currentUser.getUsername());//审批人
    //            plan.setApprovalTime(new Date());//审批时间
                plan.setInchargeOrg(hotDemand.getInchargeOrg());//加工单位
                plan.setMissingNum(hotDemand.getPlanNum());//缺件数量等于计划数量
                //--------------------------
                plan.setTotalNumber(hotDemand.getPlanNum());//计划数量
                plan.setInchargeOrg(hotDemand.getInchargeOrg());//加工车间
                plan.setBlank(hotDemand.getWorkblankType());//毛坯
                plan.setEndTime(hotDemand.getPlanEndTime());//结束时间
                plan.setAlarmStatus(0);//预警状态 0正常  1提前 2警告 3延期
                plan.setModifyBy(currentUser.getUsername());
                plan.setModifyTime(new Date());
                plan.setDrawNoName("");//图号名称
                planService.save(plan);
                //扩展字段保存
                this.saveExtend(hotDemand, plan);
            planIdMap.put(hotDemand.getId(),plan.getId());
        }
        return planIdMap;
    }

    /**
     * 计划扩展字段保存
     * @param hotDemand
     * @param plan
     */
    private void saveExtend(HotDemand hotDemand, Plan plan) {
        //扩展字段保存
        PlanExtend planExtend = new PlanExtend();
            planExtend.setProjectName(hotDemand.getProjectName());//项目名称
            planExtend.setProductName(hotDemand.getDemandName());//产品名称
            planExtend.setSampleNum(0);//实样数量
            planExtend.setDemandId(hotDemand.getId());//需求表id
            planExtend.setPlanId(plan.getId());//生产计划id
            planExtend.setWeight(hotDemand.getWeight());//重量
            planExtend.setPieceWeight(hotDemand.getPieceWeight());//单重
            planExtend.setSteelWaterWeight(hotDemand.getSteelWaterWeight());//钢水重
            planExtend.setDemandTime(hotDemand.getDemandTime());//需求日期
            planExtend.setSubmitBy(hotDemand.getSubmitBy());//提单人
            planExtend.setSubmitOrderOrg(hotDemand.getSubmitOrderOrg());//提单单位
            planExtend.setSubmitOrderTime(hotDemand.getSubmitOrderTime());//提单日期
            planExtend.setIngotCase(hotDemand.getIngotCase());//锭 型
            planExtend.setWorkblankType(hotDemand.getWorkblankType());//毛坯类型 0锻件,1铸件,2钢锭
        planExtendService.save(planExtend);
    }

    /**
     * 车间码处理
     * @param branchType
     * @param hotDemand
     * @param plan
     */
    private void disposeBranchCode(int branchType, HotDemand hotDemand, Plan plan) {
        if(branchType ==1){//模型排产
            plan.setProjType(1);//计划类型 1新制  2 返修(模型排产默认为新制)
            plan.setBranchCode("BOMCO_RG_MX");//车间码(模型排产自动派发到模型车间)
        }else {
            //0锻件,1铸件,2钢锭
            switch (hotDemand.getWorkblankType()){
                case "0":  plan.setBranchCode("BOMCO_RG_DZ");//锻造车间
                    break;
                case "1":  plan.setBranchCode("BOMCO_RG_ZG");//铸造
                    break;
                case "2":  plan.setBranchCode("BOMCO_RG_YL");//冶炼
                    break;
                default: throw new GlobalException("毛坯类型超出范围", ResultCode.FAILED);
            }
        }
    }

    /**
     * 自动生成工序计划
     * @param idList
     * @param branchCode
     * @return
     */
    @Override
    public CommonResult<?> initPlanNode(List<String> idList, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<HotDemand> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", currentUser.getTenantId());
        queryWrapper.apply("(is_exist_plan_node=0 or is_exist_plan_node is null)");
        queryWrapper.in("id", idList);
        //查出需求信息
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        if (CollectionUtils.isEmpty(hotDemands)){
            return CommonResult.failed("工序计划已生成");
        }
        List<String> drawNoList = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());
        //根据图号查出工艺信息
        List<Router> byDrawNo = baseServiceClient.getByDrawNo(drawNoList, branchCode).getData();
        if (CollectionUtils.isEmpty(byDrawNo)) {
            return CommonResult.failed("没有工艺信息");
        }
        List<String> routerIdList = byDrawNo.stream().map(x -> x.getId()).collect(Collectors.toList());
        Map<String, String> routerIdMap = byDrawNo.stream().collect(Collectors.toMap(x -> x.getDrawNo(), x -> x.getId()));
        //根据工艺id查出工序信息
        List<Sequence> sequences = baseServiceClient.querySequenceByRouterIds(routerIdList);
        if (CollectionUtils.isEmpty(sequences)) {
            return CommonResult.failed("工艺没有工序信息");
        }
        //根据工艺id分组
        Map<String, List<Sequence>> sequencesMap = sequences.stream().collect(Collectors.groupingBy(Sequence::getRouterId));
        //根据工序id查询工序字典(拿到关键工序字段)
        List<String> optIdList = sequences.stream().map(x -> x.getOptId()).collect(Collectors.toList());//工序字典id
        List<Operatipon> operatipons = baseServiceClient.queryOptByIds(optIdList);
        Map<String, Operatipon> optMap = operatipons.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));

        ArrayList<HotPlanNode> planNodes = new ArrayList<>();
        ArrayList<String> demandIdList = new ArrayList<>();
        //根据需求信息自动生成生产计划数据
        for (HotDemand hotDemand : hotDemands) {
            String s = routerIdMap.get(hotDemand.getDrawNo());
            //有工艺的情况下
            if (StringUtils.isNotEmpty(s)) {
                List<Sequence> sequencesList = sequencesMap.get(s);
                //工艺有工序的情况
                if (CollectionUtils.isNotEmpty(sequencesList)) {
                    for (Sequence sequence : sequencesList) {
                        //工序为关键工序时才产生计划节点
                        if(optMap.get(sequence.getOptId()).getIsKey()==1){
                            HotPlanNode planNode = new HotPlanNode();
                            planNode.setDemandId(hotDemand.getId());//毛坯需求id
                            planNode.setOptName(sequence.getOptName());//工序名称
                            planNode.setDemandNum(hotDemand.getNum());//需求数量
                            planNode.setOptStatus("0");//工序状态 0:未开始,1: 进行中,2:已结束
                            planNode.setBranchCode(hotDemand.getBranchCode());//车间码
                            planNode.setTenantId(hotDemand.getTenantId());//租户id
                            planNode.setOpNo(sequence.getOpNo());//工序序号
                            planNode.setOptId(sequence.getOptId());//工序字典id
                            planNode.setSequenceId(sequence.getId());//工序id
                            planNodes.add(planNode);
                            //收集已生成关键计划节点的需求id
                            demandIdList.add(hotDemand.getId());
                        }
                    }

                }
            }
        }
        planNodeService.saveBatch(planNodes);
        //更新关键计划节点生成状态
        this.updateDemand(demandIdList);
        return CommonResult.success("操作成功");
    }

    /**
     * 更新关键计划节点生成状态
     * @param demandIdList
     */
    private void updateDemand(ArrayList<String> demandIdList) {
        UpdateWrapper<HotDemand> updateWrapper =new UpdateWrapper<>();
        updateWrapper.set("is_exist_plan_node",1);//是否生成过关键计划节点0 未生成  1已生成'
        updateWrapper.in("id", demandIdList);
        hotDemandService.update(updateWrapper);
    }

    /**
     * 设置提单单位名称
     * @param branchCode
     * @param currentUser
     */
    @Override
    public String getSubmitOrderOrg(String branchCode, TenantUserDetails currentUser) {
        Branch branchInfo = baseServiceClient.getBranchInfoByBranchCode(branchCode);
        Tenant tenant = systemServiceClient.getTenantById(currentUser.getTenantId()).getData();
        if(ObjectUtils.isNotEmpty(branchInfo)&&ObjectUtils.isNotEmpty(tenant)){
           return tenant.getTenantName()+"-"+branchInfo.getBranchName();
        }else {
            return null;
        }
    }


    /**
     * 根据  图号 工作号  版本号  查重
     * @param workNo
     * @param drawNo
     * @param versionNum
     * @return
     */
    @Override
    public void checkDemand(String workNo, String drawNo,String versionNum) {
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();

        if (StringUtils.isEmpty(workNo)) {
            throw new GlobalException("工作号不能为空",ResultCode.FAILED);
        }
        if (StringUtils.isEmpty(drawNo)) {
            throw new GlobalException("图号不能为空",ResultCode.FAILED);
        }
        if (StringUtils.isEmpty(versionNum)) {
            throw new GlobalException("版本号不能为空",ResultCode.FAILED);
        }
        queryWrapper.eq("work_no",workNo);
        queryWrapper.eq("draw_no",drawNo);
        queryWrapper.eq("version_num",versionNum);
        List<HotDemand> list = hotDemandService.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(list)){
            throw new GlobalException("存在重复数据 "+list.get(0).getDemandName()+" 请合并相同项目产品",ResultCode.FAILED);
        }

    }


}
