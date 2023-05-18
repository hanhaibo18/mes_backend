package com.richfit.mes.produce.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.wms.ApplyLineList;
import com.richfit.mes.common.model.wms.ApplyLineProductList;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.dao.TrackAssignPersonMapper;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.entity.KittingVo;
import com.richfit.mes.produce.entity.QueryProcessVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import com.richfit.mes.produce.service.quality.InspectionPowerService;
import com.richfit.mes.produce.utils.ProcessFiltrationUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 马峰
 * @Description 跟单派工Controller
 */
@Slf4j
@Api(value = "跟单派工", tags = {"跟单派工"})
@RestController
@RequestMapping("/api/produce/trackassign")
public class TrackAssignController extends BaseController {

    @Autowired
    private TrackAssignService trackAssignService;
    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    @Autowired
    private TrackAssignPersonMapper trackAssignPersonMapper;
    @Autowired
    private ActionService actionService;
    @Resource
    private TrackAssignPersonService trackAssignPersonService;
    @Resource
    public PlanService planService;
    @Autowired
    private com.richfit.mes.produce.provider.SystemServiceClient systemServiceClient;
    @Resource
    private PublicService publicService;
    @Resource
    private WmsServiceClient wmsServiceClient;
    @Resource
    private TrackHeadFlowService trackHeadFlowService;
    @Resource
    private RequestNoteService requestNoteService;
    @Resource
    private TrackItemInspectionService inspectionService;
    @Resource
    private ProduceRoleOperationService roleOperationService;
    @Resource
    private BaseServiceClient baseServiceClient;
    @Resource
    private TrackAssemblyService trackAssemblyService;
    @Resource
    private ApplicationNumberService numberService;
    @Value("${switch}")
    private String off;
    @Resource
    private TrackHeadMapper trackHeadMapper;

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "派工分页查询", notes = "派工分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    @Deprecated
    public CommonResult<IPage<Assign>> page(int page, int limit, String tiId, String state, String trackId, String trackNo, String routerNo, String startTime, String endTime, String branchCode, String order, String orderCol, String assignBy) {
        try {
            QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
            if (!StringUtils.isNullOrEmpty(tiId)) {
                queryWrapper.eq("ti_id", tiId);
            }
            if (!StringUtils.isNullOrEmpty(state)) {
                queryWrapper.in("state", state.split(","));
            }
            if (!StringUtils.isNullOrEmpty(trackId)) {
                queryWrapper.eq("track_id", trackId);
            }
            if (!StringUtils.isNullOrEmpty(trackNo)) {
                queryWrapper.eq("track_no", trackNo);
            }
            if (!StringUtils.isNullOrEmpty(assignBy)) {
                queryWrapper.eq("assign_by", assignBy);
            }
            if (!StringUtils.isNullOrEmpty(routerNo)) {
                queryWrapper.apply("track_id in (select id from produce_track_head where " + DrawingNoUtil.queryEqSql("drawing_no", routerNo) + ")");
            }
            if (!StringUtils.isNullOrEmpty(startTime)) {
                queryWrapper.apply("(UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + startTime + "') or a.modify_time is null)");

            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                queryWrapper.apply("(UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + endTime + "') or a.modify_time is null)");

            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);

            }
            if (!StringUtils.isNullOrEmpty(orderCol)) {
                if (!StringUtils.isNullOrEmpty(order)) {
                    if ("desc".equals(order)) {
                        queryWrapper.orderByDesc(new String[]{StrUtil.toUnderlineCase(orderCol), "sequence_order_by"});
                    } else if ("asc".equals(order)) {
                        queryWrapper.orderByAsc(new String[]{StrUtil.toUnderlineCase(orderCol), "sequence_order_by"});
                    }
                } else {
                    queryWrapper.orderByDesc(new String[]{StrUtil.toUnderlineCase(orderCol), "sequence_order_by"});
                }
            } else {
                queryWrapper.orderByDesc(new String[]{"modify_time", "sequence_order_by"});
            }


            IPage<Assign> assigns = trackAssignService.page(new Page<Assign>(page, limit), queryWrapper);
            for (int i = 0; i < assigns.getRecords().size(); i++) {
                assigns.getRecords().get(i).setAssignPersons(trackAssignPersonMapper.selectList(new QueryWrapper<AssignPerson>().eq("assign_id", assigns.getRecords().get(i).getId())));
            }

            return CommonResult.success(assigns);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "派工自定义分页查询(已派工,未报工)", notes = "派工自定义分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "trackNo", value = "跟单号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "routerNo", value = "图号", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "state", value = "状态", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "操作人ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "classes", value = "跟单类型", required = true, paramType = "query", dataType = "String"),
    })
    @GetMapping("/querypage")
    public CommonResult<IPage<Assign>> querypage(int page, int limit, String productNo, String trackNo, String routerNo, String startTime, String endTime, String state, String userId, String branchCode, String assignBy, String classes, String order, String orderCol) {
        try {
            IPage<Assign> assigns = trackAssignService.queryPage(new Page<Assign>(page, limit), assignBy, trackNo, routerNo, startTime, endTime, state, userId, branchCode, productNo, classes, order, orderCol);

            return CommonResult.success(assigns);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增派工", notes = "新增派工")
    @ApiImplicitParam(name = "assign", value = "派工", required = true, dataType = "Assign", paramType = "path")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign> addAssign(@RequestBody Assign assign) {
        try {

            if (StringUtils.isNullOrEmpty(assign.getTiId())) {
                return CommonResult.failed("关联工序ID不能为空！");
            } else {
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                if (null != trackItem) {
                    if (trackItem.getAssignableQty() < assign.getQty()) {
                        return CommonResult.failed(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty());
                    }

                    TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                    if (null == trackHead.getStatus() || "0".equals(trackHead.getStatus()) || "".equals(trackHead.getStatus())) {
                        //将跟单状态改为在制
                        trackHead.setStatus("1");
                        trackHeadService.updateById(trackHead);
                    }
                    //可派工数减去已派工数，当前工序为1，在制状态为0
                    trackItem.setAssignableQty(trackItem.getAssignableQty() - assign.getQty());
                    trackItem.setIsCurrent(1);
                    trackItem.setIsDoing(0);
                    trackItemService.updateById(trackItem);
                }

                if (null != SecurityUtils.getCurrentUser()) {
                    assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    assign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
                }
                assign.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                assign.setAssignTime(new Date());
                assign.setModifyTime(new Date());
                assign.setCreateTime(new Date());
                assign.setAvailQty(assign.getQty());

                boolean bool = trackAssignService.save(assign);
                for (AssignPerson person : assign.getAssignPersons()) {
                    person.setModifyTime(new Date());
                    person.setAssignId(assign.getId());
                    trackAssignPersonMapper.insert(person);
                }


            }
            return CommonResult.success(assign, "操作成功！");
        } catch (Exception e) {
            return CommonResult.failed("操作失败，请重试！" + e.getMessage());
        }
    }

    @ApiOperation(value = "批量新增派工", notes = "批量新增派工")
    @ApiImplicitParam(name = "assigns", value = "派工", required = true, dataType = "Assign[]", paramType = "path")
    @PostMapping("/batchAdd")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign[]> batchAssign(@RequestBody Assign[] assigns) {
        //获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            for (Assign assign : assigns) {
                if (StringUtils.isNullOrEmpty(assign.getTiId())) {
                    throw new GlobalException("未关联工序", ResultCode.FAILED);
                }
                if (StrUtil.isNotBlank(assign.getUserId())) {
                    assign.setUserId(assign.getUserId() + ",");
                }
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                if (null != trackItem) {
                    if (trackItem.getAssignableQty() < assign.getQty()) {
                        return CommonResult.failed(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty());
                    }
                    trackItem.setIsCurrent(1);
                    trackItem.setIsDoing(0);
                    trackItem.setAssignableQty(trackItem.getAssignableQty() - assign.getQty());
                    if (0 == trackItem.getAssignableQty()) {
                        trackItem.setIsSchedule(1);
                    }
                    trackItem.setDeviceId(assign.getDeviceId());
                    //锻造计算额定工时
                    if ("4".equals(trackHead.getClasses())) {
                        trackAssignService.calculationSinglePieceHours(trackHead, trackItem);
                    }
                    trackItemService.updateById(trackItem);
                    if (StringUtils.isNullOrEmpty(assign.getTrackNo())) {
                        assign.setTrackNo(trackHead.getTrackNo());
                    }
                    if (!StringUtils.isNullOrEmpty(trackHead.getStatus()) || "0".equals(trackHead.getStatus())) {
                        //将跟单状态改为在制
                        trackHead.setStatus("1");
                        trackHeadService.updateById(trackHead);
                        UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
                        update.set("status", "1");
                        update.eq("id", trackItem.getFlowId());
                        trackHeadFlowService.update(update);
                    }
                    if (0 == trackItem.getIsExistQualityCheck() && 0 == trackItem.getIsExistScheduleCheck()) {
                        //下工序激活
                        Map<String, String> map = new HashMap<>(3);
                        map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
                        map.put(IdEnum.TRACK_HEAD_ID.getMessage(), assign.getTrackId());
                        map.put(IdEnum.TRACK_ITEM_ID.getMessage(), assign.getTiId());
                        map.put("number", String.valueOf(assign.getQty()));
                        publicService.publicUpdateState(map, PublicCodeEnum.DISPATCHING.getCode());
                    }

                    assign.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    if (null != SecurityUtils.getCurrentUser()) {
                        assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                        assign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
                    }
                    CommonResult<TenantUserVo> user = systemServiceClient.queryByUserId(assign.getAssignBy());
                    assign.setAssignName(user.getData().getEmplName());
                    assign.setAssignTime(new Date());
                    assign.setModifyTime(new Date());
                    assign.setCreateTime(new Date());
                    assign.setAvailQty(assign.getQty());
                    assign.setFlowId(trackItem.getFlowId());
                    if (StringUtils.isNullOrEmpty(assign.getTrackId())) {
                        assign.setTrackId(trackHead.getId());
                    }
                    if (StringUtils.isNullOrEmpty(assign.getTenantId())) {
                        assign.setTenantId(trackHead.getTenantId());
                    }
                    //处理派工人员信息  (前端没有处理userId 和userName  assignPerson为派工人列表)
                    if (StringUtils.isNullOrEmpty(assign.getUserId()) && !CollectionUtil.isEmpty(assign.getAssignPersons())) {
                        StringBuilder userId = new StringBuilder();
                        StringBuilder userName = new StringBuilder();
                        for (AssignPerson assignPerson : assign.getAssignPersons()) {
                            if (!StringUtils.isNullOrEmpty(String.valueOf(userId))) {
                                userId.append(",");
                                userName.append(",");
                            }
                            userId.append(assignPerson.getUserId());
                            userName.append(assignPerson.getUserName());
                        }
                        assign.setUserId(String.valueOf(userId));
                        assign.setEmplName(String.valueOf(userName));
                    }
                    boolean isAllUser = assign.getUserId().contains("/") ? true : false;
                    if (isAllUser) {
                        assign.setUserId("/");
                        assign.setEmplName("/");
                    }
                    trackAssignService.save(assign);
                    for (AssignPerson person : assign.getAssignPersons()) {
                        person.setModifyTime(new Date());
                        person.setAssignId(assign.getId());
                        trackAssignPersonMapper.insert(person);
                    }
                    //判断是否存在BOM 没有BOM不进行齐套检查
                    boolean bom = StrUtil.isNotBlank(trackHead.getProjectBomId());
                    //判断是否是装配
                    boolean assembly = "2".equals(trackHead.getClasses());
                    //是否进行齐套并发送申请单 true = 发送 false = 不发送
                    boolean switchOff = "true".equals(off);
                    if (assembly && sendWMSA(trackItem) && switchOff && bom) {
                        //无生产订单编号不允许发送申请单
                        if (StrUtil.isBlank(trackHead.getProductionOrder())) {
                            throw new GlobalException("无生产订单编号", ResultCode.FAILED);
                        }
                        IngredientApplicationDto ingredient = assemble(trackItem, trackHead, trackHead.getBranchCode());
                        requestNoteService.saveRequestNoteNew(ingredient, trackHead, trackHead.getBranchCode());
                        ApplicationResult application = wmsServiceClient.anApplicationForm(ingredient).getData();
//                        ApplyListUpload ingredient = collect(trackItem, trackHead, trackHead.getBranchCode());
//                        requestNoteService.saveRequestNoteInfo(ingredient, trackHead, trackItem, trackHead.getBranchCode());
//                        List<ApplyListUpload> list = new ArrayList<>();
//                        list.add(ingredient);
//                        ApplicationResult application = wmsServiceClient.applyListUpload(list).getData();
                        //请勿重复上传！
                        boolean upload = !application.getRetMsg().contains("请勿重复上传");
                        if ("N".equals(application.getRetCode()) && upload) {
                            numberService.deleteApplicationNumberByItemId(trackItem.getId());
                            log.error("仓储数据:" + ingredient);
                            throw new GlobalException("仓储服务:" + application.getRetMsg(), ResultCode.FAILED);
                        }
                    }
                }
                systemServiceClient.savenote(assign.getAssignBy(),
                        "您有新的派工跟单需要报工！",
                        assign.getTrackNo(),
                        assign.getUserId().substring(0, assign.getUserId().length() - 1),
                        assign.getBranchCode(),
                        assign.getTenantId());

                //保存派工操作记录
                actionService.saveAction(ActionUtil.buildAction
                        (assign.getBranchCode(), "4", "2",
                                "跟单派工，跟单号：" + assign.getTrackNo(),
                                OperationLogAspect.getIpAddress(request)));
            }
            return CommonResult.success(assigns, "操作成功！");
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    private boolean sendWMSA(TrackItem trackItem) {
        //判断是否开工
        boolean isDoing = 0 == trackItem.getIsDoing();
        //判断当前工序
        boolean isCurrent = 1 == trackItem.getIsCurrent();
        //外协工序不允许发送申请单
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flow_id", trackItem.getFlowId());
        queryWrapper.orderByAsc("opt_sequence");
        List<TrackItem> list = trackItemService.list(queryWrapper);
        //过滤外协工序后,获取最小工序
        TrackItem minTrackItem = list.stream().filter(item -> !item.getOptType().equals("3")).min(Comparator.comparing(TrackItem::getOptSequence)).get();
        if (isDoing && isCurrent && trackItem.getOptSequence().equals(minTrackItem.getOptSequence())) {
            return true;
        }
        return false;
    }

    /**
     * @param trackItem  跟单工序
     * @param trackHead  跟单表（主表）
     * @param branchCode 车间
     * @return
     */
    private ApplyListUpload collect(TrackItem trackItem, TrackHead trackHead, String branchCode) {
        // 跟单配送
        QueryWrapper<TrackAssembly> assemblyQueryWrapper = new QueryWrapper<>();
        assemblyQueryWrapper.eq("track_head_id", trackHead.getId());
        List<TrackAssembly> assemblyList = trackAssemblyService.list(assemblyQueryWrapper);
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ti_id", trackItem.getId());
        //组装申请单信息
        ApplyListUpload applyListUpload = new ApplyListUpload();
        //id
        applyListUpload.setId(UUID.randomUUID().toString().replace("-", ""));
        //申请单号
        int applicationNumber = numberService.acquireApplicationNumber(trackItem.getId(), branchCode);
        applyListUpload.setApplyNum(applicationNumber + "@0");
        // 单据类型
        applyListUpload.setTransType(null);
        //工厂编码
        applyListUpload.setWorkCode(SecurityUtils.getCurrentUser().getTenantErpCode());
        //车间
        applyListUpload.setWorkshop(branchCode);
        //库存地点
        applyListUpload.setInvCode(null);
        //工作号
        applyListUpload.setJobNo(trackItem.getWorkNo());
        //生产订单
        applyListUpload.setProdNum(trackHead.getProductionOrder());
        //合格证 结构化
        applyListUpload.setCertificate(null);
        //创建人
        applyListUpload.setCreateBy(trackHead.getCreateBy());
        //创建日期
        applyListUpload.setCreateTime(new Date());
        //行数据
        List<ApplyLineList> lineList = new ArrayList<>();
        int num = 0;
        for (TrackAssembly trackAssembly : assemblyList) {
            ApplyLineList applyLine = new ApplyLineList();
            //MES申请单ID
            applyLine.setApplyId(applyListUpload.getId());
            //MES申请单行id
            applyLine.setId(UUID.randomUUID().toString().replace("-", ""));
            //MES申请单行项目
            applyLine.setLineNum(num + 1);
            //物料编码
            applyLine.setMaterialNum(trackAssembly.getMaterialNo());
            //物料名称
            applyLine.setMaterialDesc(trackAssembly.getName());
            //计量单位
            applyLine.setUnit(trackAssembly.getUnit());
            //申请单数量
            applyLine.setQuantity(0);
            //物料类型
            applyLine.setMaterialType(trackAssembly.getSourceType());
            //关键件
            applyLine.setCrucialFlag(trackAssembly.getIsKeyPart());
            //跟踪方式
            applyLine.setTrackingMode(trackAssembly.getTrackType());
            //产品编号明细列表
            List<ApplyLineProductList> lineProductList = new ArrayList<>();
            ApplyLineProductList product = new ApplyLineProductList();
            //MES申请单行id
            product.setApplyLineId(applyLine.getId());
            //产品编号
            product.setProductNum(trackAssembly.getProductNo());
            //数量
            product.setQuantity(trackAssembly.getNumber());

            lineProductList.add(product);
            applyLine.setLineList(lineProductList);

            lineList.add(applyLine);
        }
        applyListUpload.setLineList(lineList);
        return applyListUpload;
    }


    private IngredientApplicationDto assemble(TrackItem trackItem, TrackHead trackHead, String branchCode) {
        QueryWrapper<TrackAssembly> assemblyQueryWrapper = new QueryWrapper<>();
        assemblyQueryWrapper.eq("track_head_id", trackHead.getId());
        List<TrackAssembly> assemblyList = trackAssemblyService.list(assemblyQueryWrapper);
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ti_id", trackItem.getId());
        //查询派工工位信息
        Assign assign = trackAssignService.getOne(queryWrapper);
        //组装申请单信息
        IngredientApplicationDto ingredient = new IngredientApplicationDto();
        //申请单号
        int applicationNumber = numberService.acquireApplicationNumber(trackItem.getId(), branchCode);
        ingredient.setSqd(applicationNumber + "@0");
        //工厂编码
        ingredient.setGc(SecurityUtils.getCurrentUser().getTenantErpCode());
        //车间
        ingredient.setCj(branchCode);
        //车间名称
        CommonResult<Branch> branch = baseServiceClient.selectBranchByCodeAndTenantId(branchCode, null);
        ingredient.setCjName(branch.getData().getBranchName());
        //工位
        ingredient.setGw(assign.getSiteId());
        //工位名称
        ingredient.setGwName(assign.getSiteName());
        //工序
        ingredient.setGx(trackItem.getId());
        //工序名称
        ingredient.setGxName(trackItem.getOptName());
        //生产订单编号
        ingredient.setScdd(trackHead.getProductionOrder());
        //跟单Id
        ingredient.setGd(trackHead.getId());
        //产品编号
        ingredient.setCp(trackHead.getProductNo());
        //产品名称
        ingredient.setCpName(trackHead.getProductName());
        //优先级
        ingredient.setYxj(Integer.parseInt(trackHead.getPriority()));
        //派工时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmss");
        ingredient.setPgsj(format.format(new Date()));
        //追加物料
        List<LineList> lineLists = new ArrayList<>();
//        List<String> numberList = assemblyList.stream().map(TrackAssembly::getMaterialNo).collect(Collectors.toList());
//        List<Product> list = baseServiceClient.listByMaterialNoList(numberList);
//        Map<String, String> materialNoMap = list.stream().collect(Collectors.toMap(Product::getMaterialNo, Product::getProductName, (value1, value2) -> value2));
        for (TrackAssembly trackAssembly : assemblyList) {
            LineList lineList = new LineList();
            //物料编码
            lineList.setMaterialNum(trackAssembly.getMaterialNo());
            //物料名称
            lineList.setMaterialDesc(trackAssembly.getName());
            //单位
            lineList.setUnit("单位");
            //数量
            lineList.setQuantity(trackAssembly.getNumber());
            //实物配送标识
            lineList.setSwFlag(trackAssembly.getIsEdgeStore());
            lineLists.add(lineList);
        }
        ingredient.setLineList(lineLists);
        return ingredient;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean demo() {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
//        queryWrapper.gt("create_time", "2023-05-08 15:06:50");
//        queryWrapper.eq("classes", 2);
//        queryWrapper.gt("status", 0);
//        List<TrackHead> list = trackHeadService.list(queryWrapper);
        List<TrackHead> list = trackHeadMapper.selectFinalTrackHeads();
        list = list.stream().filter(item -> StrUtil.isNotBlank(item.getProductionOrder())).collect(Collectors.toList());
        for (TrackHead trackHead : list) {
            //查询第一道工序
            QueryWrapper<TrackItem> queryWrapperitem = new QueryWrapper<>();
            queryWrapperitem.eq("track_head_id", trackHead.getId());
            queryWrapperitem.eq("opt_sequence", 1);
            TrackItem trackItem = trackItemService.getOne(queryWrapperitem);
            if (StrUtil.isBlank(trackHead.getProductionOrder())) {
                throw new GlobalException("无生产订单编号", ResultCode.FAILED);
            }
            IngredientApplicationDto ingredient = assemble(trackItem, trackHead, trackHead.getBranchCode());
            requestNoteService.saveRequestNote(ingredient, ingredient.getLineList(), trackHead.getBranchCode());
            ApplicationResult application = wmsServiceClient.anApplicationForm(ingredient).getData();
            //请勿重复上传！
            boolean upload = !application.getRetMsg().contains("请勿重复上传");
            if ("N".equals(application.getRetCode()) && upload) {
                numberService.deleteApplicationNumberByItemId(trackItem.getId());
                log.error("仓储数据:" + ingredient);
                throw new GlobalException("仓储服务:" + application.getRetMsg(), ResultCode.FAILED);
            }
        }
        return true;
    }

    @ApiOperation(value = "修改派工", notes = "修改派工")
    @ApiImplicitParam(name = "device", value = "派工", required = true, dataType = "Assign", paramType = "path")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign> updateAssign(@RequestBody Assign assign) {
        try {
            if (StringUtils.isNullOrEmpty(assign.getTiId())) {
                return CommonResult.failed("关联工序ID编码不能为空！");
            } else {
                TrackItem trackItem = trackItemService.getById(assign.getTiId());

                if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                    return CommonResult.failed("跟单工序【" + trackItem.getOptName() + "】已质检完成，报工无法取消！");
                }
                if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                    return CommonResult.failed("跟单工序【" + trackItem.getOptName() + "】已调度完成，报工无法取消！");
                }

                if (StrUtil.isNotBlank(assign.getUserId())) {
                    assign.setUserId(assign.getUserId() + ",");
                }

                // 判断后置工序是否已派工，否则无法修改
                List<Assign> cs = this.find(null, null, null, null, null, trackItem.getFlowId()).getData();
                for (int j = 0; j < cs.size(); j++) {
                    TrackItem cstrackItem = trackItemService.getById(cs.get(j).getTiId());
                    if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                        return CommonResult.failed("无法回滚，需要先取消后序工序【" + cstrackItem.getOptName() + "】的派工");
                    }
                }
                // 判断修改的派工数量是否在合理范围
                Assign oldassign = trackAssignService.getById(assign.getId());
                if (null != trackItem) {
                    if (trackItem.getAssignableQty() < (assign.getQty() - oldassign.getQty())) {
                        return CommonResult.failed(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty());
                    }
                }
                // 设置派工时间，人员，工序可派工数
                if (null != SecurityUtils.getCurrentUser()) {

                    assign.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    assign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
                }
                assign.setAssignTime(new Date());
                assign.setModifyTime(new Date());
                assign.setAvailQty(assign.getQty());
                boolean bool = trackAssignService.updateById(assign);
                QueryWrapper<AssignPerson> queryWrapper = new QueryWrapper<AssignPerson>();
                queryWrapper.eq("assign_id", assign.getId());
                trackAssignPersonService.remove(queryWrapper);
                for (AssignPerson person : assign.getAssignPersons()) {
                    person.setModifyTime(new Date());
                    person.setAssignId(assign.getId());
                    trackAssignPersonService.save(person);
                }
                trackItem.setAssignableQty(trackItem.getAssignableQty() - (assign.getQty() - oldassign.getQty()));
                if (assign.getState() == 1) {
                    trackItem.setIsDoing(1);
                    trackItem.setStartDoingTime(new Date());
                    trackItem.setStartDoingUser(SecurityUtils.getCurrentUser().getUsername());
                } else {
                    trackItem.setIsSchedule(0);
                    trackItem.setIsDoing(0);
                }
                trackItemService.updateById(trackItem);
            }
            return CommonResult.success(assign, "操作成功！");
        } catch (Exception e) {
            return CommonResult.failed("操作失败，请重试！" + e.getMessage());
        }
    }

    @ApiOperation(value = "派工查询", notes = "派工查询")
    @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<Assign>> find(String id, String tiId, String state, String trackId, String trackNo, String flowId) {
        return CommonResult.success(trackAssignService.find(id, tiId, state, trackId, trackNo, flowId), "操作成功！");
    }

    @ApiOperation(value = "派工查询", notes = "派工查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "trackNo", value = "跟单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "routerNo", value = "图号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workNo", value = "工作号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "机构", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderCol", value = "排序字段", dataType = "String", paramType = "query")
    })
    @GetMapping("/getPageAssignsByStatus")
    public CommonResult<IPage<TrackItem>> getPageAssignsByStatus(int page, int limit, String trackNo, String
            routerNo, String workNo, String startTime, String endTime, String optType, String branchCode, String order, String orderCol, String productNo) throws ParseException {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        //增加工序过滤
        ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);
        if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.apply("(UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + startTime + "') or a.modify_time is null )");
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(sdf.parse(endTime));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.apply("(UNIX_TIMESTAMP(a.modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + "') or a.modify_time is null)");
        }


//        CommonResult<TenantUserVo> result = systemServiceClient.queryByUserId(SecurityUtils.getCurrentUser().getUserId());
//        QueryWrapper<ProduceRoleOperation> queryWrapperRole = new QueryWrapper<>();
//        List<String> roleId = result.getData().getRoleList().stream().map(BaseEntity::getId).collect(Collectors.toList());
//        queryWrapperRole.in("role_id", roleId);
//        List<ProduceRoleOperation> operationList = roleOperationService.list(queryWrapperRole);
//        Set<String> set = operationList.stream().map(ProduceRoleOperation::getOperationId).collect(Collectors.toSet());
//        if (!set.isEmpty()) {
//            queryWrapper.in("operatipon_id", set);
//        }

        // 如果工序类型不为空，则按类型获取，没有类型查询所有类型工序
        if (!StringUtils.isNullOrEmpty(optType)) {
            queryWrapper.apply("a.opt_type = '" + optType + "'");
        } else {
//            queryWrapper.apply("(a.opt_type ='0' or a.opt_type ='2')");
            queryWrapper.notIn("a.opt_type", 3);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("product_no", productNo);
        }
        if (!StringUtils.isNullOrEmpty(workNo)) {
            queryWrapper.inSql("a.id", "select id from produce_track_item where track_head_id in (select id from produce_track_head where tenant_id = '" + SecurityUtils.getCurrentUser().getTenantId() + "' and work_no = '" + workNo + "')");
        }


        queryWrapper.ne("is_schedule", 1);

        //过滤排序（list中的字段不在此处排序，后边步骤再排序）
        List<String> excludeOrderCols = new ArrayList<>();
        excludeOrderCols.add("workNo");
        excludeOrderCols.add("versions");
        excludeOrderCols.add("totalQuantity");
        excludeOrderCols.add("dispatchingNumber");

        if (!StringUtils.isNullOrEmpty(orderCol) && !excludeOrderCols.contains(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if ("desc".equals(order)) {
                    queryWrapper.orderByDesc(new String[]{StrUtil.toUnderlineCase(orderCol), "sequence_order_by"});
                } else if ("asc".equals(order)) {
                    queryWrapper.orderByAsc(new String[]{StrUtil.toUnderlineCase(orderCol), "sequence_order_by"});
                }
            } else {
                queryWrapper.orderByDesc(new String[]{StrUtil.toUnderlineCase(orderCol), "sequence_order_by"});
            }
        } else {
            queryWrapper.orderByDesc(new String[]{"modify_time", "sequence_order_by"});
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            return CommonResult.success(trackAssignService.getPageAssignsByStatusAndTrack(new Page<TrackItem>(page, limit), trackNo, queryWrapper, orderCol, order, excludeOrderCols), "操作成功！");
        } else if (!StringUtils.isNullOrEmpty(routerNo)) {
            return CommonResult.success(trackAssignService.getPageAssignsByStatusAndRouter(new Page<TrackItem>(page, limit), routerNo, queryWrapper, orderCol, order, excludeOrderCols), "操作成功！");
        } else {
            return CommonResult.success(trackAssignService.getPageAssignsByStatus(new Page<TrackItem>(page, limit), queryWrapper, orderCol, order, excludeOrderCols), "操作成功！");
        }
    }


    @ApiOperation(value = "按类型查询派工跟单", notes = "按类型查询派工跟单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "trackNo", value = "跟单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "routerNo", value = "图号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "机构", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序类型", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderCol", value = "排序字段", dataType = "String", paramType = "query")
    })
    @GetMapping("/getPageTrackHeadByType")
    public CommonResult<IPage<TrackHead>> getPageTrackHeadByType(int page, int limit, String
            routerNo, String
                                                                         trackNo, String
                                                                         productNo, String startTime, String endTime, String optType, String branchCode, String order, String orderCol) throws ParseException {

        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();

        if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.apply("(UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + startTime + "') or a.modify_time is null )");
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(sdf.parse(endTime));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.apply("(UNIX_TIMESTAMP(a.modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + "') or a.modify_time is null)");
        }

        /*if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.and(wrapper1->wrapper1.ge("date_format(a.modify_time, '%Y-%m-%d')",startTime).or(wrapper->wrapper.isNull("a.modify_time")));
        }
        if (!org.springframework.util.StringUtils.isEmpty(endTime)) {
            queryWrapper.and(wrapper1->wrapper1.ge("date_format(a.modify_time, '%Y-%m-%d')",endTime).or(wrapper->wrapper.isNull("a.modify_time")));
        }*/
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("a.branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("a.product_no", productNo);
        }
        queryWrapper.exists("select * from produce_track_item b where a.id = b.track_head_id and b.opt_type='" + optType + "' and b.is_current=1 and b.is_operation_complete = 0");

        queryWrapper.in("a.status", "0,1".split(","));
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.apply("replace(replace(replace(a.track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%'");
        }
        if (!StringUtils.isNullOrEmpty(routerNo)) {
            queryWrapper.like("a.drawing_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if ("desc".equals(order)) {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if ("asc".equals(order)) {
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("a.modify_time");
        }
        return CommonResult.success(trackAssignService.getPageTrackHeadByType(new Page<TrackItem>(page, limit), queryWrapper), "操作成功！");

    }

    @Autowired
    private InspectionPowerService inspectionPowerService;
    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;

    @ApiOperation(value = "删除派工", notes = "根据id删除派工")
    @ApiImplicitParam(name = "ids", value = "ID", required = true, dataType = "String[]", paramType = "query")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign> delete(@RequestBody String[] ids) {
        trackAssignService.deleteAssign(ids);
        return CommonResult.success(null, "删除成功！");
    }

    @GetMapping("/queryProcessList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowId", value = "分流Id", required = true, paramType = "query", dataType = "String"),
    })
    @ApiOperation(value = "根据跟单Id查询工序列表")
    public CommonResult<List<QueryProcessVo>> queryProcessList(String flowId) {
        return CommonResult.success(trackAssignService.queryProcessList(flowId));
    }

    @GetMapping("/updateProcess")
    @ApiOperation(value = "修改已派工对象")
    public CommonResult<Boolean> updateProcess(Assign assign) {
        return CommonResult.success(trackAssignService.updateProcess(assign));
    }

    @GetMapping("/kittingExamine")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "trackHeadId", value = "跟单Id", required = true, paramType = "query", dataType = "String"),
    })
    @ApiOperation(value = "根据跟单id齐套性检查")
    public CommonResult<List<KittingVo>> kittingExamine(String trackHeadId) {
        return CommonResult.success(trackAssignService.kittingExamine(trackHeadId));
    }

    @PostMapping("/startWorking")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "assignIdList", value = "派工Id", required = true, paramType = "body", dataType = "List<String>"),
    })
    @ApiOperation(value = "开工")
    public CommonResult<Boolean> startWorking(@RequestBody List<String> assignIdList) {
        return trackAssignService.startWorking(assignIdList);
    }

    @ApiOperation(value = "已派工查询")
    @PostMapping("/queryForDispatching")
    public CommonResult<IPage<Assign>> queryForDispatching(@RequestBody ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(trackAssignService.queryForDispatching(dispatchingDto));
    }

    @ApiOperation(value = "未报工查询")
    @PostMapping("/queryNotAtWork")
    public CommonResult<IPage<Assign>> queryNotAtWork(ForDispatchingDto dispatchingDto) throws ParseException {
        return CommonResult.success(trackAssignService.queryNotAtWork(dispatchingDto));
    }
}
