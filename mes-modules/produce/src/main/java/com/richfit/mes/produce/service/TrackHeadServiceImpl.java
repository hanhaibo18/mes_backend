package com.richfit.mes.produce.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.IErrorCode;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.store.StoreAttachRel;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.controller.CodeRuleController;
import com.richfit.mes.produce.dao.*;
import com.richfit.mes.produce.entity.*;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 跟单服务
 */
@Service
@Transactional
public class TrackHeadServiceImpl extends ServiceImpl<TrackHeadMapper, TrackHead> implements TrackHeadService {


    @Autowired
    private LineStoreMapper lineStoreMapper;

    @Autowired
    private TrackHeadMapper trackHeadMapper;

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Autowired
    private LineStoreService lineStoreService;

    @Autowired
    private TrackHeadRelationMapper trackHeadRelationMapper;

    @Autowired
    private PlanService planService;

    @Autowired
    private CodeRuleController codeRuleController;

    @Autowired
    private ActionService actionService;

    @Resource
    private SystemServiceClient systemServiceClient;


    @Autowired
    public StoreAttachRelMapper storeAttachRelMapper;

    /**
     * 描述: 生成完工资料
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/22 10:25
     **/
    @Override
    public String completionData(String id) throws Exception {
        try {
            String path = "C:/temp";
            if (File.separator.equals("/")) {
                path = "/temp";
            }
            FileUtil.del(path);
            path = path + "/" + id;
            QueryWrapper<TrackHeadRelation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("th_id", id);
            queryWrapper.eq("type", "0");
            List<TrackHeadRelation> trackHeadRelations = trackHeadRelationMapper.selectList(queryWrapper);
            for (TrackHeadRelation thr : trackHeadRelations) {
                LineStore lineStore = lineStoreMapper.selectById(thr.getLsId());
                QueryWrapper<StoreAttachRel> queryWrapperStoreAttachRel = new QueryWrapper<>();
                queryWrapper.eq("line_store_id", thr.getLsId());
                List<StoreAttachRel> storeAttachRels = storeAttachRelMapper.selectList(queryWrapperStoreAttachRel);
                for (StoreAttachRel sar : storeAttachRels) {
                    downloads(sar.getAttachmentId(), path + "/" + lineStore.getDrawingNo() + " " + lineStore.getMaterialNo());
                }
            }
            ZipUtil.zip(path);
            return path + ".zip";
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("下载出现异常，请联系管理员");
        }
    }

    public void downloads(String id, String path) throws Exception {
        try {
            CommonResult<Attachment> atta = systemServiceClient.attachment(id);
            CommonResult<byte[]> data = systemServiceClient.getAttachmentInputStream(id);
            if (data.getStatus() == 200) {
                File file = new File(path + "/" + (StringUtils.isNullOrEmpty(atta.getData().getAttachName()) ? UUID.randomUUID().toString() + "." + atta.getData().getAttachType() : atta.getData().getAttachName()));
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                FileUtil.writeBytes(data.getData(), file);
            } else {
                throw new Exception("从文件服务器下载文件失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("下载出现异常，请联系管理员");
        }
    }

    /**
     * 描述: 根据跟单编码查询唯一跟单
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    @Override
    public TrackHead selectByTrackNo(String trackNo, String branchCode) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_no", trackNo);
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        //当重复跟单号数据已经出现异常，这时候前端并不返回错误，为了方便数据维护，添加更新时间排序，返回最新的跟单
        queryWrapper.orderByDesc("modify_time");
        List<TrackHead> l = trackHeadMapper.selectList(queryWrapper);
        return l.size() > 0 ? l.get(0) : null;
    }

    /**
     * 描述: 跟单新增
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    @Transactional
    @Override
    public boolean saveTrackHead(TrackHead trackHead) {
        //单件跟单处理
        try {
            if ("0".equals(trackHead.getTrackType())) { //单件
                System.out.println(JSON.toJSONString(trackHead.getStoreList()));
                if (trackHead.getStoreList() != null && trackHead.getStoreList().size() > 0) {
                    for (Map m : trackHead.getStoreList()) {
                        trackHeadSingleton(trackHead, trackHead.getTrackItems(), (String) m.get("workblankNo"), (Integer) m.get("num"));
                    }
                } else {
                    trackHeadSingleton(trackHead, trackHead.getTrackItems(), trackHead.getProductNo(), trackHead.getNumber());
                }
            }
//            else if (trackHead.getTrackType().equals("1")) { //批次
//                for (int i = trackHead.getStartNo(); i <= trackHead.getEndNo(); i++) {
//                    LineStore lineStore = new LineStore();
//                    lineStore.setTenantId(trackHead.getTenantId());
//                    lineStore.setDrawingNo(trackHead.getDrawingNo());
//                    lineStore.setMaterialNo(trackHead.getMaterialNo());
//
//                    String productNo = trackHead.getProductNo() + " " + i;
//                    if (!StringUtils.isNullOrEmpty(trackHead.getSuffixNo())) {
//                        productNo += " " + trackHead.getSuffixNo();
//                    }
//                    lineStore.setWorkblankNo(productNo);
//                    lineStore.setNumber(1);
//                    lineStore.setUseNum(0);
//                    lineStore.setStatus("0");
//                    lineStore.setTrackNo(trackHead.getTrackNo());
//                    lineStore.setMaterialType("1");
//                    lineStore.setTrackType("0");
//                    lineStore.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
//                    lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
//                    lineStore.setCreateTime(new Date());
//                    list.add(lineStore);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }

    /**
     * 描述: 跟单更新
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    @Transactional
    @Override
    public boolean updataTrackHead(TrackHead trackHead, List<TrackItem> trackItems) {
        try {
            TrackHead trackHeadOld = trackHeadMapper.selectById(trackHead.getId());
            //更新跟单时处理关联计划
//            if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanNo()) && !trackHead.getWorkPlanNo().equals(trackHeadOld.getWorkPlanNo())) {
//                //原计划跟单还原
//                if (!StringUtils.isNullOrEmpty(trackHeadOld.getWorkPlanNo())) {
//                    planService.setPlanStatusNew(trackHeadOld.getWorkPlanNo(), trackHead.getTenantId());
//                }
//                //新计划跟单关联
//                if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanNo())) {
//                    planService.setPlanStatusStart(trackHead.getWorkPlanNo(), trackHead.getTenantId());
//                }
//            }
            trackHead.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            trackHead.setModifyTime(new Date());
            int bool = trackHeadMapper.updateById(trackHead);
            //删除所有跟单工序
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
            queryWrapper.eq("track_head_id", trackHead.getId());
            trackItemMapper.delete(queryWrapper);
            //跟单工序添加
            if (trackItems != null && trackItems.size() > 0) {
                for (TrackItem item : trackItems) {
                    item.setTrackHeadId(trackHead.getId());
                    item.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    item.setModifyTime(new Date());
                    item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                    trackItemMapper.insert(item);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 描述: 跟单单个添加方法
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    @Transactional
    public boolean trackHeadSingleton(TrackHead trackHead, List<TrackItem> trackItems, String productsNo, int number) {
        try {
            CommonResult<CodeRule> commonResult = codeRuleController.gerCode("track_no", "跟单号", new String[]{"流水号"}, SecurityUtils.getCurrentUser().getTenantId(), "");
            //封装跟单信息数据
            trackHead.setId(UUID.randomUUID().toString().replace("-", ""));
            trackHead.setTrackNo(commonResult.getData().getCurValue());
            trackHead.setProductNo(productsNo);
            trackHead.setNumber(number);


            //只有机加进行跟单编码校验
            if ("1".equals(trackHead.getClasses())) {
                //查询跟单号码是否存在
                QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("track_no", trackHead.getTrackNo());
                queryWrapper.eq("branch_code", trackHead.getBranchCode());
                queryWrapper.eq("tenant_id", trackHead.getTenantId());
                List trackHeads = trackHeadMapper.selectList(queryWrapper);
                if (trackHeads.size() > 0) {
                    throw new RuntimeException("跟单号码已存在！请联系管理员处理流程码问题！");
                }
            }

            //仅带派工状态，也就是普通跟单新建的时候才进行库存的变更处理
            //只有机加创建跟单时才会进行库存料单关联
            if ("0".equals(trackHead.getStatus()) && "1".equals(trackHead.getClasses())) {

                //计划跟单关联
//                if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanNo())) {
//                    planService.setPlanStatusStart(trackHead.getWorkPlanNo(), trackHead.getTenantId());
//                }

                //修改库存状态  本次查到的料单能否匹配生产数量完成
                //如果一个料单就能匹配数量，就1个料单匹配；否则执行多次，查询多个料单分别出库
                Map retMap = lineStoreService.useItem(number, trackHead.getDrawingNo(), productsNo);
                LineStore lineStore = (LineStore) retMap.get("lineStore");
                if (lineStore == null) {
                    //无库存料单，默认新增库存料单，然后出库
                    lineStore = lineStoreService.autoInAndOutStoreByTrackHead(trackHead, productsNo);
                }
                TrackHeadRelation relation = new TrackHeadRelation();
                relation.setThId(trackHead.getId());
                relation.setLsId(lineStore.getId());
                relation.setType("0");
                relation.setNumber(number);
                trackHeadRelationMapper.insert(relation);

                //新增一条半成品/成品信息
                QueryWrapper<LineStore> queryWrapperStore = new QueryWrapper<LineStore>();
                queryWrapperStore.eq("workblank_no", trackHead.getDrawingNo() + " " + trackHead.getProductNo());
                queryWrapperStore.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                List<LineStore> lineStores = lineStoreService.list(queryWrapperStore);
                if (lineStores != null && lineStores.size() > 0) {
                    throw new RuntimeException("产品编号已存在！");
                } else {
                    LineStore lineStoreCp = new LineStore();
                    lineStoreCp.setId(UUID.randomUUID().toString().replace("-", ""));
                    lineStoreCp.setTenantId(trackHead.getTenantId());
                    lineStoreCp.setDrawingNo(trackHead.getDrawingNo());
                    lineStoreCp.setMaterialNo(trackHead.getMaterialNo());
                    lineStoreCp.setWorkblankNo(trackHead.getDrawingNo() + " " + trackHead.getProductNo());
                    lineStoreCp.setNumber(trackHead.getNumber());//添加单件多个产品
                    lineStoreCp.setUseNum(0);
                    lineStoreCp.setStatus("1");//在制状态
                    lineStoreCp.setTrackNo(trackHead.getTrackNo());
                    lineStoreCp.setMaterialType("1");
                    lineStoreCp.setTrackType(trackHead.getTrackType());
                    lineStoreCp.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    lineStoreCp.setCreateTime(new Date());
                    lineStoreCp.setInTime(new Date());
                    lineStoreCp.setBranchCode(trackHead.getBranchCode());
                    lineStoreCp.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                    lineStoreMapper.insert(lineStoreCp);
                    TrackHeadRelation relationCp = new TrackHeadRelation();
                    relationCp.setThId(trackHead.getId());
                    relationCp.setLsId(lineStoreCp.getId());
                    relationCp.setType("1");
                    relationCp.setNumber(number);
                    trackHeadRelationMapper.insert(relationCp);
                }
            }

            //跟单工序添加
            if (trackItems != null && trackItems.size() > 0) {
                for (TrackItem item : trackItems) {
                    item.setId(UUID.randomUUID().toString().replace("-", ""));
                    item.setTrackHeadId(trackHead.getId());
                    item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    item.setCreateTime(new Date());
                    item.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    item.setModifyTime(new Date());
                    item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                    trackItemMapper.insert(item);
                }
            }
            //添加跟单
            trackHeadMapper.insert(trackHead);

            //添加日志
            Action action = new Action();
            action.setActionType("0");
            action.setActionItem("2");
            action.setRemark("跟单号：" + trackHead.getTrackNo());
            actionService.saveAction(action);
            codeRuleController.updateCode("track_no", "跟单号", trackHead.getTrackNo(), "", SecurityUtils.getCurrentUser().getTenantId(), "");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean deleteTrackHead(List<TrackHead> trackHeads) {
        try {
            List<String> ids = trackHeads.stream().filter(trackHead -> {
                if (trackHead.getStatus().equals("0")) {
                    return true;
                } else {
                    return false;
                }
            }).map(trackHead -> trackHead.getId()).collect(Collectors.toList());
            int result = trackHeadMapper.deleteBatchIds(ids);
            if (result > 0) {
                for (String id : ids) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("track_head_id", id);
                    trackItemMapper.deleteByMap(map);
                    List<TrackHeadRelation> relations = trackHeadRelationMapper.selectList(new QueryWrapper<TrackHeadRelation>().eq("th_id", id));
                    for (TrackHeadRelation relation : relations) {
                        if (relation.getType().equals("0")) { //输入物料
                            lineStoreService.rollBackItem(relation.getNumber(), relation.getLsId());
                        } else if (relation.getType().equals("1")) { //输出物料
                            lineStoreService.removeById(relation.getLsId());
                        }
                        trackHeadRelationMapper.deleteById(relation.getId());
                    }
                }

                for (TrackHead head : trackHeads) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("work_plan_no", head.getWorkPlanNo());
                    map.put("tenant_id", head.getTenantId());
                    List<TrackHead> list = trackHeadMapper.selectByMap(map);
                    if (list.size() == 0) {
                        planService.setPlanStatusNew(head.getWorkPlanNo(), head.getTenantId());
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public IPage<TrackHead> selectTrackHeadRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query) {
        return trackHeadMapper.selectTrackHeadRouter(page, query);
    }

    @Override
    public IPage<TrackHead> selectTrackHeadCurrentRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query) {
        return trackHeadMapper.selectTrackHeadCurrentRouter(page, query);
    }


    /**
     * 功能描述: 对当前跟单增加计划
     *
     * @param trackHeads 跟单列表
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 18:07
     * @return: boolean
     **/
    @Override
    @Transactional
    public boolean updateTrackHeadPlan(List<TrackHead> trackHeads) {
        try {
            for (TrackHead t : trackHeads) {
                //修改跟单状态改为1
                Plan plan = planService.getById(t.getWorkPlanId());
                plan.setStatus(1);
                planService.updatePlan(plan);
                //修改跟单管理计划id
                t.setWorkPlanNo(plan.getProjCode());
                t.setProductionOrder(plan.getOrderNo());
                t.setWorkNo(plan.getWorkNo());
                t.setProjectBomId(plan.getProjectBom());
                trackHeadMapper.updateById(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }

    @Override
    public Integer queryTrackHeadList(String workPlanId) {
        return trackHeadMapper.selectTrackHeadNumber(workPlanId);
    }

    @Override
    public IPage<IncomingMaterialVO> queryMaterialList(Integer page, Integer size, String certificateNo, String drawingNo, String branchCode, String tenantId) {
        QueryWrapper<IncomingMaterialVO> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.eq("head.material_certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.eq("head.drawing_no", drawingNo);
        }
        queryWrapper.eq("head.branch_code", branchCode);
        queryWrapper.eq("head.tenant_id", tenantId);
        return trackHeadMapper.queryIncomingMaterialPage(new Page<>(page, size), queryWrapper);
    }

    /**
     * 功能描述: 分页查询跟单台账
     *
     * @param standingBookDto
     * @Author: xinYu.hou
     * @Date: 2022/4/27 23:06
     * @return: IPage<TrackHead>
     **/
    @Override
    public IPage<TrackHead> queryTrackHeadPage(QueryDto<StandingBookDto> standingBookDto) {
        StandingBookDto standing = standingBookDto.getParam();
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", standing.getStartTime());
        //处理结束时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(standing.getEndTime());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        queryWrapper.le("create_time", calendar.getTime());
        if (!StringUtils.isNullOrEmpty(standing.getDrawingNo())) {
            queryWrapper.eq("drawing_no", standing.getDrawingNo());
        }
        if (!StringUtils.isNullOrEmpty(standing.getDocumentaryId())) {
            queryWrapper.eq("track_no", standing.getDocumentaryId());
        }
        if (!StringUtils.isNullOrEmpty(standing.getProductNo())) {
            queryWrapper.eq("product_no", standing.getProductNo());
        }
        if (!StringUtils.isNullOrEmpty(standing.getWorkNo())) {
            queryWrapper.eq("work_no", standing.getWorkNo());
        }
        queryWrapper.eq("branch_code", standingBookDto.getBranchCode());
        queryWrapper.eq("tenant_id", standingBookDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return this.page(new Page<>(standingBookDto.getPage(), standingBookDto.getSize()), queryWrapper);
    }

    /**
     * 功能描述: 工作清单
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/5/8 6:41
     * @return: IPage<WorkDetailedListVo>
     **/
    @Override
    public IPage<WorkDetailedListVo> queryWorkDetailedList(QueryDto<QueryWork> queryDto) {
        QueryWork queryWork = queryDto.getParam();
        QueryWrapper<WorkDetailedListVo> queryWrapper = new QueryWrapper<>();
        if (null != queryWork.getStartDate() && null != queryWork.getEndDate()) {
            queryWrapper.ge("create_time", queryWork.getStartDate());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(queryWork.getEndDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        if (null != queryWork.getWorkId()) {
            queryWrapper.eq("head.work_no", queryWork.getWorkId());
        }
        if (null != queryWork.getDrawingNo()) {
            queryWrapper.eq("head.drawing_no", queryWork.getWorkId());
        }
        if (null != queryWork.getTrackNo()) {
            queryWrapper.eq("head.track_no", queryWork.getTrackNo());
        }
        queryWrapper.eq("branch_code", queryDto.getBranchCode());
        queryWrapper.eq("tenant_id", queryDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return trackHeadMapper.queryWorkDetailedList(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
    }

    /**
     * 功能描述: 修改优先级
     *
     * @param trackNo
     * @param priority
     * @Author: xinYu.hou
     * @Date: 2022/5/8 6:49
     * @return: Boolean
     **/
    @Override
    public Boolean updateWorkDetailed(String trackNo, String priority) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.eq("track_no", trackNo);
        }
        TrackHead trackHead = this.getOne(queryWrapper);
        trackHead.setPriority(priority);
        return this.updateById(trackHead);
    }

    @Override
    public IPage<TailAfterVo> queryTailAfterList(QueryDto<QueryTailAfterDto> afterDto) {
        QueryTailAfterDto tailAfter = afterDto.getParam();
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        if (null != tailAfter.getEndDate() && null != tailAfter.getStartDate()) {
            queryWrapper.ge("create_time", tailAfter.getStartDate());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(tailAfter.getEndDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        if (!StringUtils.isNullOrEmpty(tailAfter.getDrawingNo())) {
            queryWrapper.eq("drawing_no", tailAfter.getDrawingNo());
        }
        if (!StringUtils.isNullOrEmpty(tailAfter.getTrackNo())) {
            queryWrapper.ge("track_no", tailAfter.getTrackNo());
        }
        queryWrapper.eq("branch_code", afterDto.getBranchCode());
        queryWrapper.eq("tenant_id", afterDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return trackHeadMapper.queryTailAfterList(new Page<>(afterDto.getPage(), afterDto.getSize()), queryWrapper);
    }

    @Override
    public IPage<TrackHead> querySplitPage(QueryDto<QuerySplitDto> queryDto) {
        QuerySplitDto querySplitDto = queryDto.getParam();
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        if (null != querySplitDto.getEndTime() && null != querySplitDto.getStartTime()) {
            queryWrapper.ge("create_time", querySplitDto.getStartTime());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(querySplitDto.getEndTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        if (null != querySplitDto.getStatus()) {
            queryWrapper.eq("status", querySplitDto.getStatus());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getTrackNo())) {
            queryWrapper.like("track_no", querySplitDto.getTrackNo());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getDrawingNo())) {
            queryWrapper.like("drawing_no", querySplitDto.getDrawingNo());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getProductNo())) {
            queryWrapper.like("product_no", querySplitDto.getProductNo());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getTemplateCode())) {
            queryWrapper.eq("template_code", querySplitDto.getTemplateCode());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getWorkPlanId())) {
            queryWrapper.eq("work_plan_id", querySplitDto.getWorkPlanId());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getBatchNo())) {
            queryWrapper.eq("batch_no", querySplitDto.getBatchNo());
        }
        if (!StringUtils.isNullOrEmpty(querySplitDto.getProductionOrder())) {
            queryWrapper.eq("production_order", querySplitDto.getProductionOrder());
        }
        return this.page(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
    }

    @Override
    public CommonResult<Boolean> saveTrackHeader(SaveTrackHeadDto saveTrackHeadDto) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_no", saveTrackHeadDto.getTrackHead());
        TrackHead trackHead = this.getOne(queryWrapper);
        if (trackHead.getNumber() < saveTrackHeadDto.getNumber()) {
            return CommonResult.failed(new IErrorCode() {
                @Override
                public long getCode() {
                    return 500;
                }

                @Override
                public String getMessage() {
                    return "请输入正确拆分数量";
                }
            });
        }
        trackHead.setNumber(trackHead.getNumber() - saveTrackHeadDto.getNumber());
        this.updateById(trackHead);
        trackHead.setTrackNo(saveTrackHeadDto.getNewTrackHead());
        trackHead.setNumber(saveTrackHeadDto.getNumber());
        return CommonResult.success(this.save(trackHead));
    }

    @Override
    public List<TrackHead> queryListByCertId(String certificateId) {
        return trackHeadMapper.queryListByCertId(certificateId);
    }

    @Override
    public Boolean linkToCert(String thId, String certNo) {
        TrackHead trackHead = new TrackHead();
        trackHead.setId(thId);
        trackHead.setCertificateNo(certNo);
        return this.updateById(trackHead);
    }

    @Override
    public Boolean unLinkFromCert(String thId) {
        TrackHead trackHead = new TrackHead();
        trackHead.setId(thId);
        trackHead.setCertificateNo(null);
        return this.updateById(trackHead);
    }
}
