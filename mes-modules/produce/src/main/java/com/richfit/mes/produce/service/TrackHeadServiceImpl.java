package com.richfit.mes.produce.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.IErrorCode;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.store.StoreAttachRel;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.controller.CodeRuleController;
import com.richfit.mes.produce.dao.*;
import com.richfit.mes.produce.entity.*;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.utils.FilesUtil;
import com.richfit.mes.produce.utils.Utils;
import io.netty.util.internal.StringUtil;
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


    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private TrackAssemblyService trackAssemblyService;

    @Autowired
    private LineStoreMapper lineStoreMapper;

    @Autowired
    private TrackHeadMapper trackHeadMapper;

    @Autowired
    private TrackItemService trackItemService;

    @Autowired
    private LineStoreService lineStoreService;

    @Autowired
    private TrackHeadRelationMapper trackHeadRelationMapper;

    @Autowired
    private PlanService planService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CodeRuleController codeRuleController;

    @Autowired
    private ActionService actionService;

    @Resource
    private SystemServiceClient systemServiceClient;


    @Autowired
    public StoreAttachRelMapper storeAttachRelMapper;

    @Autowired
    public TrackCheckDetailService trackCheckDetailService;

    @Autowired
    public TrackFlowMapper trackFlowMapper;

    @Resource
    private PublicService publicService;

    /**
     * 功能描述: 工序资料下载指定位置
     *
     * @param flowId 跟单分流id
     * @param path   保存路径
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     **/
    @Override
    public void downloadTrackItem(String flowId, String path) throws Exception {
        //工序资料下载指定位置
        QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<TrackItem>();
        queryWrapperTrackItem.eq("flow_id", flowId);
        List<TrackItem> trackItemList = trackItemService.list(queryWrapperTrackItem);
        for (TrackItem trackItem : trackItemList) {
            List<Attachment> attachments = trackCheckDetailService.getAttachmentListByTiId(trackItem.getId());
            for (Attachment sar : attachments) {
                downloads(sar.getId(), path + "/" + trackItem.getOptName() + " " + trackItem.getSequenceOrderBy());
            }
        }
    }

    /**
     * 功能描述: 下载料单文件
     *
     * @param id   料单id
     * @param path 保存路径
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     **/
    @Override
    public void downloadStoreFile(String id, String path) throws Exception {
        QueryWrapper<StoreAttachRel> queryWrapperStoreAttachRel = new QueryWrapper<>();
        queryWrapperStoreAttachRel.eq("line_store_id", id);
        List<StoreAttachRel> storeAttachRels = storeAttachRelMapper.selectList(queryWrapperStoreAttachRel);
        for (StoreAttachRel sar : storeAttachRels) {
            downloads(sar.getAttachmentId(), path);
        }
    }

    @Override
    public List<TrackHead> selectTrackFlowList(Map<String, String> map) throws Exception {
        return trackFlowMapper.selectTrackFlowList(map);
    }

    /**
     * 描述: 其他资料列表查询
     *
     * @param flowId 分流id
     * @Author: zhiqiang.lu
     * @Date: 2022/6/22 10:25
     **/
    @Override
    public List<LineStore> otherData(String flowId) throws Exception {
        try {
            List<LineStore> lineStores = new ArrayList<>();
            QueryWrapper<TrackHeadRelation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("flow_id", flowId);
            queryWrapper.eq("type", "0");
            List<TrackHeadRelation> trackHeadRelations = trackHeadRelationMapper.selectList(queryWrapper);
            for (TrackHeadRelation thr : trackHeadRelations) {
                LineStore lineStore = lineStoreMapper.selectById(thr.getLsId());
                QueryWrapper<StoreAttachRel> queryWrapperStoreAttachRel = new QueryWrapper<>();
                queryWrapperStoreAttachRel.eq("line_store_id", thr.getLsId());
                List<Attachment> attachments = new ArrayList<>();
                for (StoreAttachRel sar : storeAttachRelMapper.selectList(queryWrapperStoreAttachRel)) {
                    CommonResult<Attachment> atta = systemServiceClient.attachment(sar.getAttachmentId());
                    attachments.add(atta.getData());
                }
                lineStore.setStoreAttachRel(attachments);
                lineStores.add(lineStore);
            }
            return lineStores;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("下载出现异常，请联系管理员");
        }
    }

    /**
     * 描述: 生成完工资料
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/22 10:25
     **/
    @Override
    public String completionData(String flowId) throws Exception {
        try {
            String path = FilesUtil.tempPath();
            path = path + "/" + SecurityUtils.getCurrentUser().getUsername();
            FileUtil.del(path);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            //查询料单
            QueryWrapper<TrackHeadRelation> queryWrapperTrackHeadRelation = new QueryWrapper<>();
            queryWrapperTrackHeadRelation.eq("flow_id", flowId);
            queryWrapperTrackHeadRelation.eq("type", "0");
            List<TrackHeadRelation> trackHeadRelations = trackHeadRelationMapper.selectList(queryWrapperTrackHeadRelation);
            for (TrackHeadRelation thr : trackHeadRelations) {
                LineStore lineStore = lineStoreMapper.selectById(thr.getLsId());
                //料单资料下载
                downloadStoreFile(thr.getLsId(), path + "/" + lineStore.getDrawingNo() + " " + lineStore.getMaterialNo());
            }
            //工序资料下载
            downloadTrackItem(flowId, path);
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
                File file = new File(path + "/" + (StringUtils.isNullOrEmpty(atta.getData().getAttachName()) ? atta.getData().getId() + "." + atta.getData().getAttachType() : atta.getData().getAttachName()));
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
            if ("Y".equals(trackHead.getIsBatch())) {
                if (trackHead.getStoreList() != null && trackHead.getStoreList().size() > 0) {
                    for (Map m : trackHead.getStoreList()) {
                        trackHeadAdd(trackHead, trackHead.getTrackItems(), (String) m.get("workblankNo"), (Integer) m.get("num"));
                    }
                } else {
                    throw new Exception("请添加产品编码");
                }
            } else {
                trackHeadAdd(trackHead, trackHead.getTrackItems(), trackHead.getProductNo(), trackHead.getNumber());
            }
            //当匹配计划时更新计划状态
            if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                planService.planData(trackHead.getWorkPlanId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }

    /**
     * 描述: 跟单添加方法
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    @Transactional
    public boolean trackHeadAdd(TrackHead trackHead, List<TrackItem> trackItems, String productsNo, int number) {
        try {
            CommonResult<CodeRule> commonResult = codeRuleController.gerCode("track_no", "跟单编号", new String[]{"跟单编号"}, SecurityUtils.getCurrentUser().getTenantId(), trackHead.getBranchCode());
            //封装跟单信息数据
            trackHead.setId(UUID.randomUUID().toString().replace("-", ""));
            trackHead.setTrackNo(commonResult.getData().getCurValue());
            trackHead.setNumberComplete(0);
            trackHead.setNumber(number);
            List<TrackFlow> trackFlowList = new ArrayList<>();
            //添加跟单分流
            if ("N".equals(trackHead.getIsBatch()) && trackHead.getStoreList() != null && trackHead.getStoreList().size() > 0) {
                //多生产线
                for (Map m : trackHead.getStoreList()) {
                    TrackFlow trackFlow = trackHeadFlow(trackHead, trackItems, (String) m.get("workblankNo"), (Integer) m.get("num"));
                    trackFlowList.add(trackFlow);
                }
            } else {
                //正常生产线
                TrackFlow trackFlow = trackHeadFlow(trackHead, trackItems, productsNo, number);
                trackFlowList.add(trackFlow);
            }

            //当跟单中存在bom(装配)
            if (!StringUtils.isNullOrEmpty(trackHead.getProjectBomId())) {
                List<TrackAssembly> trackAssemblyList = pojectBomList(trackHead);
                for (TrackAssembly trackAssembly : trackAssemblyList) {
                    trackAssembly.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    trackAssembly.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    trackAssembly.setCreateTime(new Date());
                    trackAssembly.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    trackAssembly.setModifyTime(new Date());
                    trackAssembly.setBranchCode(trackHead.getBranchCode());
                    trackAssembly.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                    trackAssemblyService.save(trackAssembly);
                }
            }

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
            //添加跟单
            trackHead = trackHeadData(trackHead, trackFlowList);
            trackHeadMapper.insert(trackHead);

            //添加日志
            Action action = new Action();
            action.setActionType("0");
            action.setActionItem("2");
            action.setRemark("跟单号：" + trackHead.getTrackNo());
            actionService.saveAction(action);
            codeRuleController.updateCode("track_no", "跟单编号", trackHead.getTrackNo(), Calendar.getInstance().get(Calendar.YEAR) + "", SecurityUtils.getCurrentUser().getTenantId(), trackHead.getBranchCode());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }

    /**
     * 描述: 跟单分流（生产线）单个添加方法
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    @Transactional
    public TrackFlow trackHeadFlow(TrackHead trackHead, List<TrackItem> trackItems, String productsNo, int number) {
        try {
            String flowId = UUID.randomUUID().toString().replaceAll("-", "");
            //仅带派工状态，也就是普通跟单新建的时候才进行库存的变更处理
            //只有机加、非试棒、状态为0时，创建跟单时才会进行库存料单关联
            if ("0".equals(trackHead.getStatus()) && "0".equals(trackHead.getIsTestBar()) && "1".equals(trackHead.getClasses())) {
                //修改库存状态  本次查到的料单能否匹配生产数量完成
                //如果一个料单就能匹配数量，就1个料单匹配；否则执行多次，查询多个料单分别出库
                Map retMap = lineStoreService.useItem(number, trackHead, productsNo);
                LineStore lineStore = (LineStore) retMap.get("lineStore");
                if (lineStore == null) {
                    //无库存料单，默认新增库存料单，然后出库
                    lineStore = lineStoreService.autoInAndOutStoreByTrackHead(number, trackHead, productsNo);
                }

                //添加跟单-分流-料单的关联信息
                TrackHeadRelation relation = new TrackHeadRelation();
                relation.setThId(trackHead.getId());
                relation.setFlowId(flowId);
                relation.setLsId(lineStore.getId());
                relation.setType("0");
                relation.setNumber(number);
                trackHeadRelationMapper.insert(relation);

                //新增一条半成品/成品信息
                QueryWrapper<LineStore> queryWrapperStore = new QueryWrapper<>();
                queryWrapperStore.eq("workblank_no", trackHead.getDrawingNo() + " " + productsNo);
                queryWrapperStore.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                List<LineStore> lineStores = lineStoreService.list(queryWrapperStore);
                if (lineStores != null && lineStores.size() > 0) {
                    throw new RuntimeException("产品编号已存在！");
                } else {
                    //TODO 这块代码应该放到linestoreService中去
                    //新增一条半成品/成品信息
                    LineStore lineStoreCp = new LineStore();
                    lineStoreCp.setId(UUID.randomUUID().toString().replace("-", ""));
                    lineStoreCp.setTenantId(trackHead.getTenantId());
                    lineStoreCp.setDrawingNo(trackHead.getDrawingNo());
                    lineStoreCp.setMaterialNo(trackHead.getMaterialNo());
                    lineStoreCp.setWorkblankNo(trackHead.getDrawingNo() + " " + productsNo);
                    lineStoreCp.setNumber(number);//添加单件多个产品
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
                    lineStoreCp.setInputType("2");  //录入类型 系统自动生成
                    lineStoreMapper.insert(lineStoreCp);
                    //添加跟单-分流-料单的关联信息
                    TrackHeadRelation relationCp = new TrackHeadRelation();
                    relationCp.setThId(trackHead.getId());
                    relation.setFlowId(flowId);
                    relationCp.setLsId(lineStoreCp.getId());
                    relationCp.setType("1");
                    relationCp.setNumber(number);
                    trackHeadRelationMapper.insert(relationCp);
                }
            }
            //添加跟单分流
            TrackFlow trackFlow = JSON.parseObject(JSON.toJSONString(trackHead), TrackFlow.class);
            trackFlow.setId(flowId);
            trackFlow.setNumber(number);
            trackFlow.setTrackHeadId(trackHead.getId());
            trackFlow.setProductNo(trackHead.getDrawingNo() + " " + productsNo);
            trackFlowMapper.insert(trackFlow);
            //跟单工序添加
            if (trackItems != null && trackItems.size() > 0) {
                for (TrackItem item : trackItems) {
                    item.setId(UUID.randomUUID().toString().replace("-", ""));
                    item.setTrackHeadId(trackHead.getId());
                    item.setFlowId(flowId);
                    item.setProductNo(trackHead.getDrawingNo() + " " + productsNo);
                    item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    item.setCreateTime(new Date());
                    item.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    item.setModifyTime(new Date());
                    item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                    //可分配数量
                    item.setAssignableQty(number);
                    trackItemService.save(item);
                    if (1 == item.getIsAutoSchedule()) {
                        Map<String, String> map = new HashMap<>(2);
                        map.put("trackHeadId", item.getTrackHeadId());
                        map.put("trackItemId", item.getId());
                        publicService.automaticProcess(map);
                    }
                }
            }
            return trackFlow;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
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

            //当跟单中存在bom
            if (!StringUtils.isNullOrEmpty(trackHead.getProjectBomId()) && !trackHead.getProjectBomId().equals(trackHeadOld.getProjectBomId())) {
                //删除历史数据
                QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("track_head_id", trackHead.getId());
                queryWrapper.eq("branch_code", trackHead.getBranchCode());
                queryWrapper.eq("tenant_id", trackHead.getTenantId());
                trackAssemblyService.remove(queryWrapper);
                //添加新的bom
                List<TrackAssembly> trackAssemblyList = pojectBomList(trackHead);
                for (TrackAssembly trackAssembly : trackAssemblyList) {
                    trackAssembly.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    trackAssembly.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    trackAssembly.setCreateTime(new Date());
                    trackAssembly.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    trackAssembly.setModifyTime(new Date());
                    trackAssembly.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                    trackAssemblyService.save(trackAssembly);
                }
            }
            trackHead.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            trackHead.setModifyTime(new Date());
            int bool = trackHeadMapper.updateById(trackHead);

            //工序批量修改
            if ("N".equals(trackHead.getIsBatch()) && trackHead.getFlowNumber() > 1) {
                //多生产线工序修改
                //删除所有为派工的跟单工序
                QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<>();
                queryWrapperTrackItem.eq("track_head_id", trackHead.getId());
                queryWrapperTrackItem.eq("is_schedule", "0");
                trackItemService.remove(queryWrapperTrackItem);
                //跟单工序添加
                if (trackItems != null && trackItems.size() > 0) {
                    for (TrackItem item : trackItems) {
                        //批量添加未派工的工序
                        if (item.getIsSchedule() == 0) {
                            QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
                            queryWrapperTrackFlow.eq("track_head_id", trackHead.getId());
                            List<TrackFlow> trackFlows = trackFlowMapper.selectList(queryWrapperTrackFlow);
                            for (TrackFlow trackFlow : trackFlows) {
                                item.setId(UUID.randomUUID().toString().replace("-", ""));
                                item.setFlowId(trackFlow.getId());
                                item.setTrackHeadId(trackHead.getId());
                                item.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                                item.setModifyTime(new Date());
                                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                                trackItemService.saveOrUpdate(item);
                            }
                        }
                    }
                }
            } else {
                //跟单工序添加
                if (trackItems != null && trackItems.size() > 0) {
                    for (TrackItem item : trackItems) {
                        if (StringUtils.isNullOrEmpty(item.getId())) {
                            item.setId(UUID.randomUUID().toString().replace("-", ""));
                        }
                        item.setTrackHeadId(trackHead.getId());
                        item.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                        item.setModifyTime(new Date());
                        item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                        trackItemService.saveOrUpdate(item);
                    }
                }
                QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<>();
                queryWrapperTrackItem.eq("track_head_id", trackHead.getId());
                List<TrackItem> trackItemList = trackItemService.list(queryWrapperTrackItem);
                for (TrackItem trackItem : trackItemList) {
                    boolean flag = true;
                    for (TrackItem item : trackItems) {
                        if (trackItem.getId().equals(item.getId())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        trackItemService.removeById(trackItem);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    List<TrackAssembly> pojectBomList(TrackHead trackHead) {
        List<TrackAssembly> trackAssemblyList = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(trackHead.getProjectBomId())) {
            List<ProjectBom> projectBomList = baseServiceClient.getProjectBomPartByIdList(trackHead.getProjectBomId());
            Map<String, String> group = new HashMap<>();
            if (!StringUtil.isNullOrEmpty(trackHead.getProjectBomGroup())) {
                group = JSON.parseObject(trackHead.getProjectBomGroup(), Map.class);
            }
            for (ProjectBom pb : projectBomList) {
                TrackAssembly trackAssembly = new TrackAssembly();
                trackAssembly.setGrade(pb.getGrade());
                trackAssembly.setName(pb.getProdDesc());
                trackAssembly.setDrawingNo(pb.getDrawingNo());
                trackAssembly.setMaterialNo(pb.getMaterialNo());
                trackAssembly.setTrackHeadId(trackHead.getId());
                trackAssembly.setNumber(trackHead.getNumber() * pb.getNumber());
                trackAssembly.setIsKeyPart(pb.getIsKeyPart());
                trackAssembly.setTrackType(pb.getTrackType());
                trackAssembly.setWeight(Double.valueOf(pb.getWeight()));
                trackAssembly.setIsCheck(pb.getIsCheck());
                trackAssembly.setIsEdgeStore(pb.getIsEdgeStore());
                trackAssembly.setIsNeedPicking(pb.getIsNeedPicking());
                trackAssembly.setUnit(pb.getUnit());
                trackAssembly.setSourceType(pb.getSourceType());
                if (!StringUtil.isNullOrEmpty(pb.getGroupBy())) {
                    if (pb.getId().equals(group.get(pb.getGroupBy()))) {
                        trackAssemblyList.add(trackAssembly);
                    }
                } else {
                    trackAssemblyList.add(trackAssembly);
                }

            }
        }
        return trackAssemblyList;
    }

    @Override
    public boolean deleteTrackHead(List<TrackHead> trackHeads) {
        try {
            List<String> ids = trackHeads.stream().filter(trackHead -> {
                if ("0".equals(trackHead.getStatus()) || "4".equals(trackHead.getStatus())) {
                    return true;
                } else {
                    return false;
                }
            }).map(trackHead -> trackHead.getId()).collect(Collectors.toList());
            int result = trackHeadMapper.deleteBatchIds(ids);
            if (result > 0) {
                //删除分流数据、工序垃圾数据等信息，避免数据库垃圾数据，料单数据处理
                for (String id : ids) {
                    //删除分流表数据
                    QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
                    queryWrapperTrackFlow.eq("track_head_id", id);
                    trackFlowMapper.delete(queryWrapperTrackFlow);
                    //删除工序
                    Map<String, Object> map = new HashMap<>();
                    map.put("track_head_id", id);
                    trackItemService.removeByMap(map);
                    //删除跟单物料关联表
                    List<TrackHeadRelation> relations = trackHeadRelationMapper.selectList(new QueryWrapper<TrackHeadRelation>().eq("th_id", id));
                    for (TrackHeadRelation relation : relations) {
                        if (relation.getType().equals("0")) { //输入物料
                            //回滚数量
                            lineStoreService.rollBackItem(relation.getNumber(), relation.getLsId());
                        } else if (relation.getType().equals("1")) { //输出物料
                            lineStoreService.removeById(relation.getLsId());
                        }
                        trackHeadRelationMapper.deleteById(relation.getId());
                    }
                }
                for (TrackHead trackHead : trackHeads) {
                    //删除bom垃圾数据，避免数据库垃圾数据
                    QueryWrapper<TrackAssembly> queryWrapperTrackAssembly = new QueryWrapper<>();
                    queryWrapperTrackAssembly.eq("track_head_id", trackHead.getId());
                    queryWrapperTrackAssembly.eq("branch_code", trackHead.getBranchCode());
                    queryWrapperTrackAssembly.eq("tenant_id", trackHead.getTenantId());
                    trackAssemblyService.remove(queryWrapperTrackAssembly);
                    //处理计划细节状态等
                    if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                        planService.planData(trackHead.getWorkPlanId());
                    }
                    //取消跟单关联
                    QueryWrapper<LineStore> queryWrapperLineStore = new QueryWrapper<>();
                    queryWrapperLineStore.eq("track_no", trackHead.getTrackNo());
                    queryWrapperLineStore.eq("branch_code", trackHead.getBranchCode());
                    queryWrapperLineStore.eq("tenant_id", trackHead.getTenantId());
                    LineStore lineStore = new LineStore();
                    lineStore.setTrackNo("");
                    lineStoreService.update(lineStore, queryWrapperLineStore);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
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
                TrackHead trackHeadOld = trackHeadMapper.selectById(t.getId());

                Plan plan = planService.getById(t.getWorkPlanId());
                //修改跟单管理计划id
                t.setWorkPlanNo(plan.getProjCode());
                t.setProductionOrder(plan.getOrderNo());
                t.setWorkNo(plan.getWorkNo());
                t.setProjectBomId(plan.getProjectBom());
                t.setProjectBomName(plan.getProjectBomName());
                t.setProjectBomWork(plan.getProjectBomWork());
                t.setProjectBomGroup(plan.getProjectBomGroup());
                trackHeadMapper.updateById(t);

                //修改老跟单匹配计划
                if (!StringUtils.isNullOrEmpty(trackHeadOld.getWorkPlanId())) {
                    planService.planData(trackHeadOld.getWorkPlanId());
                }
                //修改新跟单匹配计划
                planService.planData(t.getWorkPlanId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }


    /**
     * 功能描述: 跟单完成
     *
     * @param flowId 跟单分流id
     * @Author: zhiqiang.lu
     * @Date: 2022/7/6 18:07
     * @return: void
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void trackHeadFinish(String flowId) throws Exception {
        try {
            //跟单完成更新分流数据
            TrackFlow trackFlow = trackFlowMapper.selectById(flowId);
            trackFlow.setCompleteTime(new Date());
            trackFlow.setStatus("2");
            trackFlow.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            trackFlow.setModifyTime(new Date());
            trackFlowMapper.updateById(trackFlow);

            //跟单完成数量，状态更新
            TrackHead trackHead = trackHeadMapper.selectById(trackFlow.getTrackHeadId());
            trackHead.setNumberComplete(trackHead.getNumberComplete() + trackFlow.getNumber());
            if (trackHead.getNumber() == trackHead.getNumberComplete()) {
                trackHead.setStatus("2");
            }

            //完成品料单数据更新
            QueryWrapper<TrackHeadRelation> queryWrapperTrackHeadRelation = new QueryWrapper<>();
            queryWrapperTrackHeadRelation.eq("flow_id", flowId);
            queryWrapperTrackHeadRelation.eq("type", "1");
            List<TrackHeadRelation> trackHeadRelations = trackHeadRelationMapper.selectList(queryWrapperTrackHeadRelation);
            for (TrackHeadRelation trackHeadRelation : trackHeadRelations) {
                LineStore lineStore = lineStoreService.getById(trackHeadRelation.getLsId());
                lineStore.setStatus("0");
                lineStoreService.updateById(lineStore);
            }
            //更新跟单动作
            trackHeadMapper.updateById(trackHead);

            //计划数据更新
            if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                planService.planData(trackHead.getWorkPlanId());
            }
            if (!StringUtils.isNullOrEmpty(trackHead.getProductionOrderId())) {
                orderService.orderData(trackHead.getProductionOrderId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("跟单完成操作失败");
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

    @Override
    public List<TrackHead> queryTrackAssemblyByTrackNo(String trackNo) {
        QueryWrapper<TrackAssembly> wrapper = new QueryWrapper();
        wrapper.eq("track_head_id", trackNo);
        List<TrackAssembly> list = trackAssemblyService.list(wrapper);
        List<TrackHead> trackHeads = new ArrayList<>();
        list.forEach(i -> {
            QueryWrapper<TrackHead> tWrapper = new QueryWrapper<>();
            tWrapper.eq("product_no", i.getProductNo());
            TrackHead one = this.getOne(tWrapper);
            if (ObjectUtils.isNotNull(one)) {
                trackHeads.add(one);
            }
        });
        Map<String, TrackHead> collect = trackHeads.stream().collect(Collectors.toMap(TrackHead::getProductNo, v -> v, (a, b) -> a));
        return new ArrayList<>(collect.values());
    }

    @Override
    public IPage<TrackHead> queryBomList(QueryDto<TrackHead> trackHeads) {
        TrackHead trackHead = trackHeads.getParam();
        if (!StringUtils.isNullOrEmpty(trackHead.getProductNo())) {
            List<TrackHead> trackHeadList = queryTrackAssemblyByTrackNo(trackHead.getTrackNo());
            trackHeadMapper.queryBomList(new Page<>(trackHeads.getPage(), trackHeads.getSize()), trackHeadList);
        }
        return null;
    }

    @Override
    public IPage<TrackHead> selectTrackHeadAndFlow(Page<TrackHead> page, QueryWrapper<TrackHead> queryWrapper) {
        return trackHeadMapper.selectTrackHeadAndFlow(page, queryWrapper);
    }

    @Override
    public void trackHeadSplit(TrackHead trackHead, String trackNoNew, List<TrackFlow> trackFlow, List<TrackFlow> TrackFlowNew) {
        //更新原跟单
        trackHeadData(trackHead, trackFlow);
        trackHeadMapper.updateById(trackHead);
        //添加新的跟单
        TrackHead trackHeadNew = trackHeadData(trackHead, TrackFlowNew);
        //优先赋值
        trackHeadNew.setOriginalTrackId(trackHead.getId());
        trackHeadNew.setOriginalTrackNo(trackHead.getTrackNo());
        //更改为新值
        trackHeadNew.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        trackHeadNew.setTrackNo(trackNoNew);
        trackHeadMapper.insert(trackHead);
        codeRuleController.updateCode("track_no", "跟单编号", trackHeadNew.getTrackNo(), Calendar.getInstance().get(Calendar.YEAR) + "", SecurityUtils.getCurrentUser().getTenantId(), trackHeadNew.getBranchCode());
        //生产线迁移新跟单
        trackFlowMigrations(trackHeadNew.getId(), TrackFlowNew);
        //计划数据更新
        if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
            planService.planData(trackHead.getWorkPlanId());
        }
    }

    @Override
    public void trackHeadSplitBack(TrackHead trackHead) {
        TrackHead originalTrackHead = trackHeadMapper.selectById(trackHead.getOriginalTrackId());
        List<TrackFlow> originalTrackFlowList = trackFlowList(originalTrackHead.getId());
        List<TrackFlow> trackFlowList = trackFlowList(trackHead.getId());
        //生产线还原原跟单
        trackFlowMigrations(originalTrackHead.getId(), trackFlowList);
        //生产线合并
        originalTrackFlowList.addAll(trackFlowList);
        originalTrackHead = trackHeadData(originalTrackHead, originalTrackFlowList);
        trackHeadMapper.updateById(originalTrackHead);
        //删除回收的跟单
        trackHeadMapper.deleteById(trackHead);
    }

    /**
     * 功能描述: 生产线迁移新跟单
     *
     * @param id            跟单id
     * @param trackFlowList 生产线列表信息
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 11:37
     **/
    public void trackFlowMigrations(String id, List<TrackFlow> trackFlowList) {
        for (TrackFlow t : trackFlowList) {
            t.setTrackHeadId(id);
            trackFlowMapper.updateById(t);
            //工序迁移
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("flow_id", t.getId());
            updateWrapper.set("track_head_id", id);
            trackItemService.update(updateWrapper);
        }
    }

    /**
     * 功能描述: 产品编码拼接功能
     *
     * @param trackHead     跟单信息
     * @param trackFlowList 生产线列表信息
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 11:37
     **/
    public String productsNoStr(TrackHead trackHead, List<TrackFlow> trackFlowList) {
        //产品列表排序
        trackFlowsOrder(trackFlowList);
        //机加产品编码处理
        if (trackFlowList.size() == 1) {
            if ("0".equals(trackHead.getIsTestBar())) {
                return trackFlowList.get(0).getProductNo().replaceFirst(trackHead.getDrawingNo() + " ", "");
            } else {
                return trackFlowList.get(0).getProductNo().replaceFirst(trackHead.getDrawingNo() + " ", "") + "S";
            }
        }
        if (trackFlowList.size() > 1) {
            String productsNoStr = "";
            String productsNoTemp = "0";
            for (TrackFlow trackFlow : trackFlowList) {
                String pn = trackFlow.getProductNo().replaceFirst(trackHead.getDrawingNo() + " ", "");
                String pnOld = Utils.stringNumberAdd(productsNoTemp, 1);
                if (pn.equals(pnOld)) {
                    productsNoStr = productsNoStr.replaceAll("[-]" + productsNoTemp, "");
                    productsNoStr += "-" + pn;
                } else {
                    productsNoStr += "," + pn;
                }
                productsNoTemp = pn;
            }
            return productsNoStr.replaceFirst("[,]", "");
        }
        return null;
    }

    /**
     * 功能描述: 跟单数量、完成数量、状态计算
     *
     * @param trackHead     跟单信息
     * @param trackFlowList 生产线列表信息
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 11:37
     **/
    public TrackHead trackHeadData(TrackHead trackHead, List<TrackFlow> trackFlowList) {
        trackHead.setProductNo(productsNoStr(trackHead, trackFlowList));
        trackHead.setNumber(trackFlowList.size());
        trackHead.setFlowNumber(trackFlowList.size());
        int numberComplete = 0;
        String productNoDesc = "";
        //生产线迁移新跟单
        for (TrackFlow t : trackFlowList) {
            if ("2".equals(t.getStatus())) {
                numberComplete++;
            }
            productNoDesc += "," + t.getProductNo();
        }
        trackHead.setProductNoDesc(productNoDesc.replaceFirst(",", ""));
        trackHead.setNumberComplete(numberComplete);
        if (numberComplete < trackFlowList.size()) {
            //未完工
        } else {
            trackHead.setStatus("2");
        }

        trackHead.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        trackHead.setModifyTime(new Date());
        trackHead.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return trackHead;
    }

    /**
     * 功能描述: 产品列表list排序
     *
     * @param trackFlowList 生产线列表信息
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 11:37
     **/
    public void trackFlowsOrder(List<TrackFlow> trackFlowList) {
        Collections.sort(trackFlowList, new Comparator<TrackFlow>() {
            @Override
            public int compare(TrackFlow o1, TrackFlow o2) {
                return o1.getProductNo().compareTo(o2.getProductNo());
            }
        });
    }

    @Override
    public List<TrackFlow> trackFlowList(String trackHeadId) {
        QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
        queryWrapperTrackFlow.eq("track_head_id", trackHeadId);
        return trackFlowMapper.selectList(queryWrapperTrackFlow);
    }

    @Override
    public List<Map> selectTrackStoreCount(String drawingNos) {
        return trackHeadMapper.selectTrackStoreCount(drawingNos);
    }
}
