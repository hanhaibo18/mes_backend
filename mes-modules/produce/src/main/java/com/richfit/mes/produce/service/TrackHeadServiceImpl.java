package com.richfit.mes.produce.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.store.StoreAttachRel;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.controller.CodeRuleController;
import com.richfit.mes.produce.dao.*;
import com.richfit.mes.produce.entity.*;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.print.TemplateService;
import com.richfit.mes.produce.service.quality.ProduceInspectionRecordCardService;
import com.richfit.mes.produce.utils.FilesUtil;
import com.richfit.mes.produce.utils.InspectionRecordCardUtil;
import com.richfit.mes.produce.utils.TrackHeadUtil;
import com.richfit.mes.produce.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhiqiang.lu
 * @Description 跟单服务
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrackHeadServiceImpl extends ServiceImpl<TrackHeadMapper, TrackHead> implements TrackHeadService {

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
    public TrackHeadFlowService trackHeadFlowService;

    @Autowired
    public CodeRuleService codeRuleService;

    @Resource
    private PublicService publicService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private ProduceInspectionRecordCardService produceInspectionRecordCardService;

    @Resource
    private TrackItemMapper trackItemMapper;

    @Override
    public List<TrackHead> selectTrackHeadAccount(TeackHeadDto trackHead) {
        return trackHeadMapper.selectTrackHeadAccount(trackHead);
    }

    /**
     * 功能描述: 工序资料下载指定位置
     *
     * @param flowId 跟单分流id
     * @param path   保存路径
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     **/
    @Override
    public void downloadTrackItem(String flowId, String path) {
        //工序资料下载指定位置
        QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<>();
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
    public void downloadStoreFile(String id, String path) {
        QueryWrapper<StoreAttachRel> queryWrapperStoreAttachRel = new QueryWrapper<>();
        queryWrapperStoreAttachRel.eq("line_store_id", id);
        List<StoreAttachRel> storeAttachRels = storeAttachRelMapper.selectList(queryWrapperStoreAttachRel);
        for (StoreAttachRel sar : storeAttachRels) {
            downloads(sar.getAttachmentId(), path);
        }
    }

    @Override
    public List<TrackHead> selectTrackFlowList(Map<String, String> map) throws Exception {
        return trackHeadFlowService.selectTrackFlowList(map);
    }

    /**
     * 描述: 其他资料列表查询
     *
     * @param flowId 分流id
     * @Author: zhiqiang.lu
     * @Date: 2022/6/22 10:25
     **/
    @Override
    public List<LineStore> otherData(String flowId) {
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
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    /**
     * 描述: 生成完工资料
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/22 10:25
     **/
    @Override
    public String completionDataZip(String flowId) {
        try {
            TrackFlow trackFlow = trackHeadFlowService.getById(flowId);
            TrackHead trackHead = this.getById(trackFlow.getTrackHeadId());
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

            //质量检测卡资料下载
            ProduceInspectionRecordCard produceInspectionRecordCard = produceInspectionRecordCardService.selectProduceInspectionRecordCard(flowId);
            InspectionRecordCardUtil.excelFile(produceInspectionRecordCard, path + "/质量检测卡/" + produceInspectionRecordCard.getCardNo() + ".xlsx");

            //跟单合格证文件下载
            if (!StrUtil.isBlank(trackHead.getCertificateNo())) {
                templateService.certByNo(trackHead.getCertificateNo(), trackFlow.getBranchCode(), path + "/合格证/");
            }
            //跟单附件信息下载
            if (!StrUtil.isBlank(trackHead.getFilesId())) {
                String[] filesId = trackHead.getFilesId().split(",");
                for (String fileId : filesId) {
                    //通过文件id下载附件
                    downloads(fileId, path + "/附件/");
                }
            }
            ZipUtil.zip(path);
            return path + ".zip";
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    @Override
    public void completionData(String flowId) {
        try {
            //判断完工资料是否符合生产条件
            TrackFlow trackFlow = trackHeadFlowService.getById(flowId);
            if ("Y".equals(trackFlow.getIsCompletionData())) {
                throw new GlobalException("已经生成过完工资料，不能重复生成", ResultCode.FAILED);
            }
            TrackHead trackHead = this.getById(trackFlow.getTrackHeadId());
            if (StringUtils.isNullOrEmpty(trackHead.getCertificateNo())) {
                throw new GlobalException("需要生成合格证后才能生成完工资料", ResultCode.FAILED);
            }
            //完工资料zip保存文件服务器
            String filePath = completionDataZip(flowId);
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            fis.close();
            //文件转为byte[]并上传文件服务器
            CommonResult<Attachment> commonResult = systemServiceClient.uploadFile(fileBytes, file.getName());
            if (commonResult.getStatus() != 200) {
                throw new GlobalException("上传文件入库失败", ResultCode.FAILED);
            }
            trackFlow.setStatus("8");
            trackFlow.setIsCompletionData("Y");
            trackFlow.setCompletionData(commonResult.getData().getId());
            trackFlow.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            trackFlow.setModifyTime(new Date());
            trackHeadFlowService.updateById(trackFlow);
            QueryWrapper<TrackFlow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("track_head_id", trackHead.getId());
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            List<TrackFlow> trackFlowList = trackHeadFlowService.list(queryWrapper);
            //跟单多生产线判断，当全部生成后跟新跟单状态
            boolean flag = true;
            for (TrackFlow tf : trackFlowList) {
                if (!"Y".equals(tf.getIsCompletionData())) {
                    flag = false;
                }
            }
            if (flag) {
                trackHead.setStatus("8");
                trackHead.setIsCompletionData("Y");
            }
            this.updateById(trackHead);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }


    public void downloads(String id, String path) {
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
                throw new GlobalException("从文件服务器下载文件失败", ResultCode.FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
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
    @Override
    public boolean saveTrackHead(TrackHead trackHead) {
        //单件跟单处理
        try {
            List<TrackHead> trackHeadList = TrackHeadUtil.saveInfo(trackHead, codeRuleService);
            for (TrackHead th : trackHeadList) {
                trackHeadAdd(th, th.getTrackItems(), th.getProductNo(), th.getNumber());
            }
            //当匹配计划时更新计划状态
            planService.planData(trackHead.getWorkPlanId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
        return true;
    }

    @Autowired
    private HourStandardService hourStandardService;
    @Autowired
    private HourService hourService;

    /**
     * 描述: 热工跟单绑定工艺
     *
     * @Author: renzewen
     * @Date: 2022/12/19 10:25
     **/
    @Override
    public boolean rgSaveTrackHead(String trackNo, List<TrackItem> trackItems, String routerId, String routerVer) {
        //根据跟单号查询跟单
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        trackNo = trackNo.replaceAll(" ", "");
        queryWrapper.eq("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '')", trackNo);
        List<TrackHead> headList = this.list(queryWrapper);
        //查询跟单 如果绑定了工艺就是修改
        if (!StringUtils.isNullOrEmpty(headList.get(0).getRouterId())) {
            boolean bool = this.updataTrackHead(headList.get(0), trackItems);
        } else {
            //新增绑定
            //查询分流表
            List<String> headIds = headList.stream().map(TrackHead::getId).collect(Collectors.toList());
            QueryWrapper<TrackFlow> trackFlowQueryWrapper = new QueryWrapper<>();
            trackFlowQueryWrapper.in("track_head_id", headIds);
            List<TrackFlow> trackFlows = trackHeadFlowService.list(trackFlowQueryWrapper);
            //删除旧的
            if (headIds.size() > 0) {
                QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
                trackItemQueryWrapper.in("track_head_id", headIds);
                trackItemService.remove(trackItemQueryWrapper);
            }
            //绑定新的
            Map<String, List<TrackFlow>> trackFlowMap = trackFlows.stream().collect(Collectors.groupingBy(TrackFlow::getTrackHeadId));

            for (TrackHead trackHead : headList) {
                if (!ObjectUtil.isEmpty(trackFlowMap.get(trackHead.getId())) && trackFlowMap.get(trackHead.getId()).size() > 0) {
                    List<TrackFlow> flows = trackFlowMap.get(trackHead.getId());
                    for (TrackFlow flow : flows) {
                        if (trackItems != null && trackItems.size() > 0) {
                            for (TrackItem item : trackItems) {
                                item.setId(UUID.randomUUID().toString().replace("-", ""));
                                item.setTrackHeadId(trackHead.getId());
                                item.setDrawingNo(trackHead.getDrawingNo());
                                item.setFlowId(flow.getId());
                                item.setProductNo(trackHead.getDrawingNo() + " " + trackHead.getProductNoDesc());
                                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                                //可分配数量
                                item.setAssignableQty(trackHead.getNumber());
                                item.setNumber(trackHead.getNumber());
                                item.setIsSchedule(0);
                                item.setIsPrepare(0);
                                item.setIsNotarize(0);
                                //需要调度审核时展示
                                if (1 == item.getIsExistScheduleCheck()) {
                                    item.setIsScheduleCompleteShow(1);
                                } else {
                                    item.setIsScheduleCompleteShow(0);
                                }
                                if (trackHead.getStatus().equals("4")) {
                                    item.setIsCurrent(0);
                                }

                                trackItemService.save(item);
                            }
                        }
                    }
                }
                //跟单工艺属性赋值
                trackHead.setRouterId(routerId);
                trackHead.setRouterVer(routerVer);
                this.updateById(trackHead);
                //用于在跟单存在第一道工序自动派工的情况
                autoSchedule(trackHead);
            }

        }
        //加载工时标准
        Map<String, List<Hour>> hourGoup = getHourGoupByTypeCodeAndOptName();
        for (TrackItem item : trackItems) {
            //工时计算
            Double aDouble = calculationHeatItemHour(item, hourGoup.get(item.getTypeCode() + "_" + item.getOptName()));
            item.setHeatHour(aDouble);
        }
        trackItemService.saveOrUpdateBatch(trackItems);


        return true;
    }

    /**
     * 工时标准分组
     */
    private Map<String, List<Hour>> getHourGoupByTypeCodeAndOptName() {

        QueryWrapper<HourStandard> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("is_activate",HourStandard.YES_ACTIVATE);
        List<HourStandard> list = hourStandardService.list(queryWrapper1);
        if(list.size()>0){
            QueryWrapper<Hour> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("ver_id",list.get(0).getId());
            List<Hour> hours = hourService.list(queryWrapper2);
            //分组便于取值  key->typeCode_optName
            return hours.stream().collect(Collectors.groupingBy(item -> item.getTypeCode() + "_" + item.getOptName()));
        }
        return new HashMap<String, List<Hour>>();
    }

    /**
     * 描述: 根据工时标准计算跟单工序工时
     *       时标准重量上限>判断跟单重量>时标准重量下限
     * @Author: renzewen
     * @Date: 2023/1/4 10:25
     **/
    public Double calculationHeatItemHour(TrackItem item,List<Hour> hours){
        //跟单重量
        Float weight = trackHeadMapper.selectById(item.getTrackHeadId()).getWeight();
        //根据工时标准的重量上限下限来确定工时
        if(!ObjectUtil.isEmpty(hours)){
            for (Hour hour : hours) {
                if( Double.parseDouble(hour.getWeightUp())>=Double.parseDouble(String.valueOf(weight)) && Double.parseDouble(String.valueOf(weight))>=Double.parseDouble(hour.getWeightDown())){
                    return Double.parseDouble(hour.getHour());
                }
            }
        }
        return 0.0;
    }

    @Override
    public void changeProductNo(String trackHeadId, String productNo) {
        try {
            TrackHead trackHead = this.getById(trackHeadId);
            QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
            queryWrapperTrackFlow.eq("track_head_id", trackHeadId);
            TrackFlow trackFlow;
            try {
                trackFlow = trackHeadFlowService.getOne(queryWrapperTrackFlow);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new GlobalException("跟单产品编写修改只支持跟单只有单个产品编码，不支持多生产编码修改。", ResultCode.FAILED);
            }
            //生成产品编
            String produceNoDesc = trackHead.getDrawingNo() + " " + productNo;
            trackHead.setProductNo(productNo);
            trackHead.setProductNoDesc(produceNoDesc);
            trackFlow.setProductNo(produceNoDesc);

            //修改item表产品编号
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("track_head_id", trackHead.getId());
            updateWrapper.set("product_no", produceNoDesc);
            trackItemService.update(updateWrapper);

//            QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<>();
//            queryWrapperTrackItem.eq("track_head_id", trackHeadId);
//            List<TrackItem> trackItemList = trackItemService.list(queryWrapperTrackItem);
//            for (TrackItem trackItem : trackItemList) {
//                trackItem.setProductNo(produceNoDesc);
//            }
//            trackItemService.updateBatchById(trackItemList);
            trackHeadFlowService.updateById(trackFlow);
            this.updateById(trackHead);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException("修改产品编码出现异常：" + e.getMessage(), ResultCode.FAILED);
        }
    }


    /**
     * 描述: 跟单添加方法
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    public boolean trackHeadAdd(TrackHead trackHead, List<TrackItem> trackItems, String productsNo,
                                int number) {
        try {
            //查询跟单号码是否存在
            QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("track_no", trackHead.getTrackNo());
            queryWrapper.eq("branch_code", trackHead.getBranchCode());
            queryWrapper.eq("tenant_id", trackHead.getTenantId());
            List<TrackHead> trackHeads = trackHeadMapper.selectList(queryWrapper);
            if (trackHeads.size() > 0) {
                throw new GlobalException("跟单号码已存在！请联系管理员处理流程码问题！", ResultCode.FAILED);
            }

            //封装跟单信息数据
            trackHead.setId(UUID.randomUUID().toString().replace("-", ""));
            trackHead.setNumberComplete(0);
            trackHead.setNumber(number);
            trackHead.setTenantId(SecurityUtils.getCurrentUser().getTenantId());

            //添加跟单分流（生产线）
            List<TrackFlow> trackFlowList = new ArrayList<>();
            List<TrackHead> trackHeadList = TrackHeadUtil.flowInfo(trackHead);
            for (TrackHead th : trackHeadList) {
                TrackFlow trackFlow = trackHeadFlow(th, th.getTrackItems(), th.getProductNo(), th.getNumber());
                trackFlowList.add(trackFlow);
            }

            //添加跟单
            trackHead = trackHeadData(trackHead, trackFlowList);
            trackHeadMapper.insert(trackHead);

            //当跟单中存在bom(装配)
            if (!StringUtils.isNullOrEmpty(trackHead.getProjectBomId())) {
                trackAssemblyService.addTrackAssemblyByTrackHead(trackHead);
            }

            //用于在跟单存在第一道工序自动派工的情况
            autoSchedule(trackHead);

            //添加日志
            Action action = new Action();
            action.setActionType("0");
            action.setActionItem("2");
            action.setRemark("跟单号：" + trackHead.getTrackNo());
            actionService.saveAction(action);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
        return true;
    }

    /**
     * 描述: 跟单分流（生产线）单个添加方法
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    public TrackFlow trackHeadFlow(TrackHead trackHead, List<TrackItem> trackItems, String productsNo,
                                   int number) {
        try {
            String flowId = UUID.randomUUID().toString().replaceAll("-", "");

            //跟单添加料单数据处理
            this.lineStore(flowId, trackHead, productsNo, number);

            //添加跟单分流
            TrackFlow trackFlow = JSON.parseObject(JSON.toJSONString(trackHead), TrackFlow.class);
            trackFlow.setId(flowId);
            trackFlow.setNumber(number);
            trackFlow.setTrackHeadId(trackHead.getId());
            if (!StrUtil.isBlank(productsNo)) {
                //试棒类型拼接S
                if ("1".equals(trackHead.getIsTestBar())) {
                    productsNo = productsNo + "S";
                }
                if (!StrUtil.isBlank(productsNo)) {
                    trackFlow.setProductNo(trackHead.getDrawingNo() + " " + productsNo);
                }
                //查询产品编码是否存在
                QueryWrapper<TrackFlow> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("product_no", trackFlow.getProductNo());
                queryWrapper.eq("branch_code", trackFlow.getBranchCode());
                queryWrapper.eq("tenant_id", trackFlow.getTenantId());
                List<TrackFlow> trackFlowList = trackHeadFlowService.list(queryWrapper);
                if (trackFlowList.size() > 0) {
                    throw new GlobalException("产品编码已存在，不可以重复！", ResultCode.FAILED);
                }
            } else {
                trackFlow.setProductNo(null);
            }
            trackHeadFlowService.save(trackFlow);

            //跟单工序添加
            trackItemService.addItemByTrackHead(trackHead, trackItems, productsNo, number, flowId);
            return trackFlow;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    /**
     * 描述: 将侯欣雨加入的自动派工独立封装一下
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/9/23 10:25
     **/
    public void autoSchedule(TrackHead trackHead) {
        if ("0".equals(trackHead.getStatus()) || "1".equals(trackHead.getStatus())) {
            //获取当前跟单下所有第一道工序
            QueryWrapper<TrackItem> queryWrapperItem = new QueryWrapper<>();
            queryWrapperItem.eq("track_head_id", trackHead.getId());
            queryWrapperItem.eq("sequence_order_by", 1);
            List<TrackItem> trackItemList = trackItemService.list(queryWrapperItem);
            //自动派工工序
            List<TrackItem> trackItemAutoList = new ArrayList<>();
            List<TrackItem> trackItemNotAutoList = new ArrayList<>();
            for (TrackItem trackItemEntity : trackItemList) {
                QueryWrapper<TrackItem> queryWrapperAuto = new QueryWrapper<>();
                //查询自动派工的并行工序
                queryWrapperAuto.eq("flow_id", trackItemEntity.getFlowId());
                queryWrapperAuto.eq("is_auto_schedule", 1);
                queryWrapperAuto.eq("original_opt_sequence", trackItemEntity.getOriginalOptSequence());
                queryWrapperAuto.notIn("opt_type", 3);
                trackItemAutoList.addAll(trackItemService.list(queryWrapperAuto));
                //获取到并行的不自动派工的工序
                QueryWrapper<TrackItem> queryWrapperNotAuto = new QueryWrapper<>();
                queryWrapperNotAuto.eq("flow_id", trackItemEntity.getFlowId());
                queryWrapperNotAuto.eq("is_auto_schedule", 0);
                queryWrapperNotAuto.eq("original_opt_sequence", trackItemEntity.getOriginalOptSequence());
                queryWrapperNotAuto.notIn("opt_type", 3);
                trackItemService.list(queryWrapperNotAuto);
            }
            trackItemNotAutoList.forEach(item -> item.setIsCurrent(1));
            trackItemService.updateBatchById(trackItemNotAutoList);
            //循环工序
            for (TrackItem trackItem : trackItemAutoList) {
                Map<String, String> map = new HashMap<>(4);
                map.put("trackItemId", trackItem.getId());
                map.put("trackHeadId", trackHead.getId());
                map.put("trackNo", trackHead.getTrackNo());
                map.put("classes", trackHead.getClasses());
                publicService.automaticProcess(map);
            }
        }
    }

    /**
     * 描述: 跟单添加料单数据处理
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/9/23 10:25
     **/
    public void lineStore(String flowId, TrackHead trackHead, String productsNo, int number) {
        //仅带派工状态，也就是普通跟单新建的时候才进行库存的变更处理
        //只有机加、非试棒、状态为0时，创料建跟单时才会进行库存单关联
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
            //料单添加成品信息
            LineStore lineStoreCp = lineStoreService.addCpStoreByTrackHead(trackHead, productsNo, number);

            //添加跟单-分流-料单的关联信息
            TrackHeadRelation relationCp = new TrackHeadRelation();
            relationCp.setThId(trackHead.getId());
            relationCp.setFlowId(flowId);
            relationCp.setLsId(lineStoreCp.getId());
            relationCp.setType("1");
            relationCp.setNumber(number);
            trackHeadRelationMapper.insert(relationCp);
        }
    }

    /**
     * 描述: 跟单更新
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/21 10:25
     **/
    @Override
    public boolean updataTrackHead(TrackHead trackHead, List<TrackItem> trackItems) {
        try {
            TrackHead trackHeadOld = trackHeadMapper.selectById(trackHead.getId());
            //当跟单中存在bom
            if (!StringUtils.isNullOrEmpty(trackHead.getProjectBomId()) && !trackHead.getProjectBomId().equals(trackHeadOld.getProjectBomId())) {
                //删除历史数据
                QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("track_head_id", trackHead.getId());
                queryWrapper.eq("branch_code", trackHead.getBranchCode());
                queryWrapper.eq("tenant_id", trackHead.getTenantId());
                trackAssemblyService.remove(queryWrapper);
                //添加新的bom
                trackAssemblyService.addTrackAssemblyByTrackHead(trackHead);
            }
            trackHead.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            trackHead.setModifyTime(new Date());
            trackHeadMapper.updateById(trackHead);

            //工序批量修改（单件跟单多生产线、普通跟单判断）
            if ("N".equals(trackHead.getIsBatch()) && trackHead.getFlowNumber().compareTo(1) > 0) {
                //多生产线工序修改
                //工序顺序降序查询以派工的工序
//                QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<>();
//                queryWrapperTrackItem.eq("track_head_id", trackHead.getId());
//                queryWrapperTrackItem.eq("is_schedule", 1);
//                queryWrapperTrackItem.orderByDesc("opt_sequence");
//                List<TrackItem> trackItemList = trackItemService.list(queryWrapperTrackItem);
                //查询已派工最大工序顺序下所有工序
                List<TrackItem> trackItemList = trackItemMapper.getTrackItems(trackHead.getId());
                //取出最大的顺序数
                int optSequence = 0;
                if (!trackItemList.isEmpty()) {
                    optSequence = trackItemList.get(0).getOptSequence();
                }
                //删除大于最大顺序数的工序信息
                QueryWrapper<TrackItem> queryWrapperTrackItem2 = new QueryWrapper<>();
                queryWrapperTrackItem2.eq("track_head_id", trackHead.getId());
                queryWrapperTrackItem2.gt("opt_sequence", optSequence);
                trackItemService.remove(queryWrapperTrackItem2);
                //过滤掉小于或等于最大顺序数的工序
                int finalOptSequence = optSequence;
                List<TrackItem> itemList = trackItems.stream().filter(item -> (finalOptSequence < item.getOptSequence())).collect(Collectors.toList());
                Map<String, TrackItem> map = trackItemList.stream().collect(Collectors.groupingBy(TrackItem::getFlowId, Collectors.collectingAndThen(Collectors.toList(), value -> value.get(0))));
                //循环根据那个分流ID创建工序
                for (TrackItem item : map.values()) {
                    TrackFlow trackFlow = trackHeadFlowService.getById(item.getFlowId());
                    for (TrackItem trackItem : itemList) {
                        //分流Id不一样 往后所有的当前工序全部为否
                        if (!item.getFlowId().equals(trackItem.getFlowId())) {
                            trackItem.setIsCurrent(0);
                        }
                        trackItem.setId(UUID.randomUUID().toString().replace("-", ""));
                        trackItem.setFlowId(trackFlow.getId());
                        trackItem.setTrackHeadId(trackHead.getId());
                        trackItem.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                        trackItem.setModifyTime(new Date());
                        trackItem.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                        //可分配数量
                        trackItem.setAssignableQty(trackFlow.getNumber());
                        trackItem.setNumber(trackFlow.getNumber());
                        trackItem.setProductNo(trackFlow.getProductNo());
                        trackItemService.saveOrUpdate(trackItem);
                    }
                }


                //跟单工序添加
//                if (!trackItems.isEmpty()) {
//                    for (TrackItem item : itemList) {
//                        //批量添加未派工的工序
//                        QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
//                        queryWrapperTrackFlow.eq("track_head_id", trackHead.getId());
//                        List<TrackFlow> trackFlows = trackHeadFlowService.list(queryWrapperTrackFlow);
//                        for (TrackFlow trackFlow : trackFlows) {
//                            item.setId(UUID.randomUUID().toString().replace("-", ""));
//                            item.setFlowId(trackFlow.getId());
//                            item.setTrackHeadId(trackHead.getId());
//                            item.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
//                            item.setModifyTime(new Date());
//                            item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
//                            //可分配数量
//                            item.setAssignableQty(trackFlow.getNumber());
//                            item.setNumber(trackFlow.getNumber());
//                            trackItemService.saveOrUpdate(item);
//                        }
//                    }
//                }
            } else {
                //普通跟单工序添加与修改
                if (trackItems != null && trackItems.size() > 0) {
                    for (TrackItem item : trackItems) {
                        if (StringUtils.isNullOrEmpty(item.getId())) {
                            item.setId(UUID.randomUUID().toString().replace("-", ""));
                            item.setAssignableQty(trackHead.getNumber());
                            item.setNumber(trackHead.getNumber());

                        }
                        if (StrUtil.isBlank(item.getFlowId())) {
                            QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
                            queryWrapperTrackFlow.eq("track_head_id", trackHead.getId());
                            List<TrackFlow> trackFlows = trackHeadFlowService.list(queryWrapperTrackFlow);
                            item.setFlowId(trackFlows.get(0).getId());
                        }
                        item.setTrackHeadId(trackHead.getId());
                        item.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                        item.setModifyTime(new Date());
                        item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                        trackItemService.saveOrUpdate(item);
                    }
                }
                //删除为匹配的工序（跟单中删除该工序）
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
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
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
            if (result > 0 && ids != null && ids.size() > 0) {
                //删除分流数据、工序垃圾数据等信息，避免数据库垃圾数据，料单数据处理
                for (String id : ids) {
                    //删除分流表数据
                    QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
                    queryWrapperTrackFlow.eq("track_head_id", id);
                    trackHeadFlowService.remove(queryWrapperTrackFlow);
                    //删除工序
                    Map<String, Object> map = new HashMap<>();
                    map.put("track_head_id", id);
                    trackItemService.removeByMap(map);
                    //删除跟单物料关联表
                    List<TrackHeadRelation> relations = trackHeadRelationMapper.selectList(new QueryWrapper<TrackHeadRelation>().eq("th_id", id));
                    for (TrackHeadRelation relation : relations) {
                        if ("0".equals(relation.getType())) { //输入物料
                            //回滚数量
                            lineStoreService.rollBackItem(relation.getNumber(), relation.getLsId());
                        } else if ("1".equals(relation.getType())) { //输出物料
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
                    //处理计划细节状态等、计划通用计算方法
                    planService.planData(trackHead.getWorkPlanId());
                    //处理订单细节状态等、计划通用计算方法
                    orderService.orderDataTrackHead(trackHead);
                    //取消跟单关联
                    UpdateWrapper<LineStore> lineStoreUpdateWrapper = new UpdateWrapper<>();
                    lineStoreUpdateWrapper.eq("track_no", trackHead.getTrackNo());
                    lineStoreUpdateWrapper.eq("branch_code", trackHead.getBranchCode());
                    lineStoreUpdateWrapper.eq("tenant_id", trackHead.getTenantId());
                    lineStoreUpdateWrapper.set("track_no", "");
                    lineStoreService.update(lineStoreUpdateWrapper);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
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
    public boolean updateTrackHeadPlan(List<TrackHead> trackHeads) {
        try {
            for (TrackHead t : trackHeads) {
                TrackHead trackHeadOld = trackHeadMapper.selectById(t.getId());

                Plan plan = planService.getById(t.getWorkPlanId());
                //修改跟单管理计划id
                t.setWorkPlanId(plan.getId());
                t.setWorkPlanNo(plan.getProjCode());
                t.setWorkPlanProjectNo(plan.getProjectNo());
                t.setWorkPlanEndTime(plan.getEndTime());

                t.setWorkNo(plan.getWorkNo());
                t.setProductionOrder(plan.getOrderNo());
                t.setProductionOrderId(plan.getOrderId());
                t.setProjectBomId(plan.getProjectBom());
                t.setProjectBomName(plan.getProjectBomName());
                t.setProjectBomWork(plan.getProjectBomWork());
                t.setProjectBomGroup(plan.getProjectBomGroup());

                trackHeadMapper.updateById(t);

                //修改老跟单匹配计划
                planService.planData(trackHeadOld.getWorkPlanId());
                orderService.orderDataTrackHead(trackHeadOld);
                //修改新跟单匹配计划
                planService.planData(t.getWorkPlanId());
                orderService.orderDataTrackHead(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
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
    public void trackHeadFinish(String flowId) {
        try {
            //跟单完成更新分流数据
            TrackFlow trackFlow = trackHeadFlowService.getById(flowId);
            trackFlow.setCompleteTime(new Date());
            trackFlow.setStatus("2");
            trackFlow.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            trackFlow.setModifyTime(new Date());
            trackHeadFlowService.updateById(trackFlow);

            //跟单完成数量，状态更新
            int num = 0;
            TrackHead trackHead = trackHeadMapper.selectById(trackFlow.getTrackHeadId());
            QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
            queryWrapperTrackFlow.eq("track_head_id", trackFlow.getTrackHeadId());
            List<TrackFlow> trackFlowList = trackHeadFlowService.list(queryWrapperTrackFlow);
            for (TrackFlow tf : trackFlowList) {
                if (TrackHead.STATUS_2.equals(tf.getStatus()) || TrackHead.STATUS_8.equals(tf.getStatus()) || TrackHead.STATUS_9.equals(tf.getStatus())) {
                    num += trackFlow.getNumber();
                }
            }

            trackHead.setNumberComplete(num);
            if (trackHead.getNumber().equals(trackHead.getNumberComplete())) {
                trackHead.setStatus("2");
            }

            //完成品料单数据更新
            QueryWrapper<TrackHeadRelation> queryWrapperTrackHeadRelation = new QueryWrapper<>();
            queryWrapperTrackHeadRelation.eq("flow_id", flowId);
            queryWrapperTrackHeadRelation.eq("type", "1");
            List<TrackHeadRelation> trackHeadRelations = trackHeadRelationMapper.selectList(queryWrapperTrackHeadRelation);
            for (TrackHeadRelation trackHeadRelation : trackHeadRelations) {
                LineStore lineStore = lineStoreService.getById(trackHeadRelation.getLsId());
                //TODO:不了解业务临时处理一下
                if (lineStore == null) {
                    continue;
                }
                lineStore.setStatus("0");
                lineStoreService.updateById(lineStore);
            }
            //更新跟单动作
            trackHeadMapper.updateById(trackHead);

            //计划数据更新
            planService.planData(trackHead.getWorkPlanId());
            //订单数据更新
            orderService.orderDataTrackHead(trackHead);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    /**
     * 功能描述: 跟单报废
     *
     * @param id 跟单id
     * @Author: zhiqiang.lu
     * @Date: 2022/7/6 18:07
     * @return: void
     **/
    @Override
    public void trackHeadUseless(String id) {
        try {
            //跟单作废分流数据作废
            UpdateWrapper<TrackFlow> updateWrapperTrackFlow = new UpdateWrapper<>();
            updateWrapperTrackFlow.eq("track_head_id", id);
            updateWrapperTrackFlow.eq("type", "1");
            updateWrapperTrackFlow.set("status", "5");
            updateWrapperTrackFlow.set("complete_time", new Date());
            updateWrapperTrackFlow.set("complete_time", new Date());
            trackHeadFlowService.update(updateWrapperTrackFlow);

            //跟单作废
            TrackHead trackHead = trackHeadMapper.selectById(id);
            trackHead.setNumberComplete(0);
            trackHead.setStatus("5");

            //完成品料单数据删除
            QueryWrapper<TrackHeadRelation> queryWrapperTrackHeadRelation = new QueryWrapper<>();
            queryWrapperTrackHeadRelation.eq("th_id", id);
            queryWrapperTrackHeadRelation.eq("type", "1");
            List<TrackHeadRelation> trackHeadRelations = trackHeadRelationMapper.selectList(queryWrapperTrackHeadRelation);
            for (TrackHeadRelation trackHeadRelation : trackHeadRelations) {
                LineStore lineStore = lineStoreService.getById(trackHeadRelation.getLsId());
                lineStoreService.removeById(lineStore);
            }
            //更新跟单动作
            trackHeadMapper.updateById(trackHead);

            //计划数据更新
            planService.planData(trackHead.getWorkPlanId());
            //订单数据更新
            orderService.orderDataTrackHead(trackHead);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }


    @Override
    public IPage<TrackHead> selectTrackHeadRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query) {
        return trackHeadMapper.selectTrackHeadRouter(page, query);
    }

    @Override
    public IPage<TrackHead> selectTrackHeadCurrentRouter
            (Page<TrackHead> page, QueryWrapper<TrackHead> query) {
        return trackHeadMapper.selectTrackHeadCurrentRouter(page, query);
    }

    @Override
    public Integer queryTrackHeadList(String workPlanId) {
        return trackHeadMapper.selectTrackHeadNumber(workPlanId);
    }

    @Override
    public IPage<IncomingMaterialVO> queryMaterialList(Integer page, Integer size, String certificateNo, String
            drawingNo, String branchCode, String tenantId) {
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
        trackHead.setStatus("2");
        return this.updateById(trackHead);
    }

    @Override
    public List<TrackHead> queryTrackAssemblyByTrackNo(String flowId) {
        QueryWrapper<TrackAssembly> wrapper = new QueryWrapper();
        wrapper.eq("flow_Id", flowId);
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
    public void trackHeadSplit(TrackHead trackHead, String
            trackNoNew, List<TrackFlow> trackFlow, List<TrackFlow> trackFlowNew) {
        //更新原跟单
        trackHeadData(trackHead, trackFlow);
        trackHeadMapper.updateById(trackHead);
        //添加新的跟单
        TrackHead trackHeadNew = trackHeadData(trackHead, trackFlowNew);
        //优先赋值
        trackHeadNew.setOriginalTrackId(trackHead.getId());
        trackHeadNew.setOriginalTrackNo(trackHead.getTrackNo());
        //更改为新值
        trackHeadNew.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        trackHeadNew.setTrackNo(trackNoNew);
        trackHeadMapper.insert(trackHead);
        //生产线迁移新跟单
        trackFlowMigrations(trackHeadNew.getId(), trackFlowNew);
        //计划数据更新
        planService.planData(trackHead.getWorkPlanId());
    }

    @Override
    public void trackHeadBatchSplit(TrackHead trackHead, String
            trackNoNew, List<TrackFlow> trackFlow, List<TrackFlow> trackFlowNew) {
        if (trackFlow.size() != 1) {
            throw new GlobalException("批次跟单只能有一个生产线", ResultCode.FAILED);
        }
        if (trackFlowNew.size() != 1) {
            throw new GlobalException("批次跟单只能有一个生产线", ResultCode.FAILED);
        }

        //老工序工序查询
        QueryWrapper<TrackItem> wrapperTrackItem = new QueryWrapper();
        wrapperTrackItem.eq("track_head_id", trackHead.getId());
        wrapperTrackItem.orderByAsc("opt_sequence");
        List<TrackItem> trackItemListOld = trackItemService.list(wrapperTrackItem);
        //获取当前工序中的顺序最大值（包括并行工序）
        int optSequence = 0;
        TrackItem trackItemLast = new TrackItem();
        for (TrackItem trackItem : trackItemListOld) {
            if (trackItem.getIsCurrent() == 1) {
                //找到最大的当前工序序号
                optSequence = trackItem.getOptSequence();
                trackItemLast = trackItem;
            }
        }
        if (optSequence == trackItemListOld.size() && trackItemLast.getOptParallelType() == 1) {
            throw new GlobalException("最后一道工序为并行工序不允许拆分，请回滚至上工序。", ResultCode.FAILED);
        }
        if (optSequence == trackItemListOld.size() && trackItemLast.getIsDoing() > 0) {
            throw new GlobalException("最后一道已开工不允许拆分，请清除最后工序开工记录。", ResultCode.FAILED);
        }
        //更新原跟单生产线
        for (TrackFlow tf : trackFlow) {
            trackHeadFlowService.updateById(tf);
        }
        //原跟单数据处理
        trackHeadData(trackHead, trackFlow);

        //更新未开工的工序的数量
        for (TrackItem trackItem : trackItemListOld) {
            if (trackItem.getOptSequence() == optSequence && trackItem.getIsDoing() == 0 && trackItem.getOptParallelType() == 0) {
                //工序顺序等于当前工序且未开工且是非并行的工序数量才能修改
                trackItem.setNumber(trackHead.getNumber());
                trackItem.setAssignableQty(trackHead.getNumber());
                trackItem.setBatchQty(trackHead.getNumber());
                trackItemService.updateById(trackItem);
            } else if (trackItem.getOptSequence() > optSequence && trackItem.getIsDoing() == 0) {
                //工序顺序大于当前工序且未开工的工序数量才能修改
                trackItem.setNumber(trackHead.getNumber());
                trackItem.setAssignableQty(trackHead.getNumber());
                trackItem.setBatchQty(trackHead.getNumber());
                trackItemService.updateById(trackItem);
            }
        }
        trackHeadMapper.updateById(trackHead);


        //添加新的跟单
        TrackHead trackHeadNew = trackHeadData(trackHead, trackFlowNew);
        //优先赋值
        trackHeadNew.setOriginalTrackId(trackHead.getId());
        trackHeadNew.setOriginalTrackNo(trackHead.getTrackNo());
        //更改为新值
        trackHeadNew.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        trackHeadNew.setTrackNo(trackNoNew);
        trackHeadNew.setWorkPlanId(null);
        trackHeadNew.setWorkPlanEndTime(null);
        trackHeadNew.setWorkPlanProjectNo(null);
        trackHeadNew.setWorkPlanNo(null);
        trackHeadNew.setProductionOrderId(null);
        trackHeadNew.setProductionOrder(null);
        trackHeadMapper.insert(trackHeadNew);
        //添加新批次生产线
        for (TrackFlow tfn : trackFlowNew) {
            tfn.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            tfn.setTrackHeadId(trackHeadNew.getId());
            trackHeadFlowService.saveOrUpdate(tfn);

            //添加新生产线的工序
            for (TrackItem trackItem : trackItemListOld) {
                //工序顺序大于等于当前工序且未开工的工序数量才能修改
                trackItem.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                trackItem.setTrackHeadId(trackHeadNew.getId());
                trackItem.setFlowId(tfn.getId());
                trackItem.setNumber(trackHeadNew.getNumber());
                trackItem.setAssignableQty(trackHeadNew.getNumber());
                trackItem.setBatchQty(trackHeadNew.getNumber());
                trackItem.setIsDoing(0);
                trackItem.setIsQualityComplete(0);
                trackItem.setQualityCheckBy(null);
                trackItem.setQualityCheckBranch(null);
                trackItem.setQualityCompleteTime(null);
                trackItem.setQualityResult(null);
                trackItem.setScheduleCompleteBy(null);
                trackItem.setScheduleCompleteTime(null);
                trackItem.setScheduleCompleteResult(null);
                trackItem.setIsFinalComplete(null);
                trackItem.setIsTrackSequenceComplete(0);
                trackItemService.save(trackItem);
            }
        }

        //计划订单数据更新
        planService.planData(trackHead.getWorkPlanId());
        orderService.orderDataTrackHead(trackHead);
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

    @Override
    public void trackHeadSplitBatchBack(TrackHead trackHead) {
        //是否可以还原功能检测，当前跟单
        trackHeadSplitBatchBackCheck(trackHead);
        TrackHead originalTrackHead = this.getById(trackHead.getOriginalTrackId());
        //是否可以还原功能检测，还原的上级跟单
        trackHeadSplitBatchBackCheck(originalTrackHead);
        //计算合并后的跟单数量
        int number = originalTrackHead.getNumber() + trackHead.getNumber();
        //查询上级跟单的生产现
        QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
        queryWrapperTrackFlow.eq("track_head_id", originalTrackHead.getId());
        List<TrackFlow> trackFlowList = trackHeadFlowService.list(queryWrapperTrackFlow);
        //跟新上级跟单生产线数量
        for (TrackFlow trackFlow : trackFlowList) {
            trackFlow.setNumber(number);
            trackHeadFlowService.updateById(trackFlow);
        }
        //上级跟单计算并更新上级跟单
        originalTrackHead = this.trackHeadData(originalTrackHead, trackFlowList);
        this.updateById(originalTrackHead);

        //上级跟单工序数量修改
        UpdateWrapper<TrackItem> updateWrapperTrackItem = new UpdateWrapper();
        updateWrapperTrackItem.eq("track_head_id", originalTrackHead.getId());
        updateWrapperTrackItem.set("number", number);
        updateWrapperTrackItem.set("assignable_qty", number);
        updateWrapperTrackItem.set("batch_qty", number);
        trackItemService.update(updateWrapperTrackItem);

        //删除拆分的生产线
        QueryWrapper<TrackFlow> queryWrapperTrackFlowSplit = new QueryWrapper<>();
        queryWrapperTrackFlowSplit.eq("track_head_id", trackHead.getId());
        trackHeadFlowService.remove(queryWrapperTrackFlowSplit);
        //删除拆分的工序
        QueryWrapper<TrackItem> queryWrapperTrackItemSplit = new QueryWrapper<>();
        queryWrapperTrackItemSplit.eq("track_head_id", trackHead.getId());
        trackHeadFlowService.remove(queryWrapperTrackFlowSplit);
        //删除跟单
        this.removeById(trackHead);

        //计划订单数据更新
        planService.planData(originalTrackHead.getWorkPlanId());
        orderService.orderDataTrackHead(originalTrackHead);
    }

    public void trackHeadSplitBatchBackCheck(TrackHead trackHead) {
        QueryWrapper<TrackItem> wrapperTrackItem = new QueryWrapper();
        wrapperTrackItem.eq("track_head_id", trackHead.getId());
        wrapperTrackItem.eq("number", trackHead.getNumber());
        List<Integer> isDoing = new ArrayList<>();
        isDoing.add(1);
        isDoing.add(2);
        wrapperTrackItem.in("is_doing", isDoing);
        wrapperTrackItem.orderByAsc("opt_sequence");
        List<TrackItem> trackItemList = trackItemService.list(wrapperTrackItem);
        if (trackItemList != null && trackItemList.size() > 0) {
            throw new GlobalException("当前跟单或者原跟单已开工，不能还原跟单。", ResultCode.FAILED);
        }
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
        try {
            for (TrackFlow t : trackFlowList) {
                t.setTrackHeadId(id);
                trackHeadFlowService.updateById(t);
                //工序迁移
                UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("flow_id", t.getId());
                updateWrapper.set("track_head_id", id);
                trackItemService.update(updateWrapper);
            }
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
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
        if (trackFlowList.size() == 1 && !StrUtil.isBlank(trackFlowList.get(0).getProductNo())) {
            return trackFlowList.get(0).getProductNo().replaceFirst(trackHead.getDrawingNo() + " ", "");
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
        trackHead.setNumber(0);
        trackHead.setFlowNumber(trackFlowList.size());
        int numberComplete = 0;
        String productNoDesc = "";
        //生产线迁移新跟单
        for (TrackFlow t : trackFlowList) {
            if ("2".equals(t.getStatus()) || "8".equals(t.getStatus()) || "9".equals(t.getStatus())) {
                numberComplete += t.getNumber();
            }
            //当产生产先的品编码不为空时，进行产品编码的拼接
            if (!StringUtils.isNullOrEmpty(t.getProductNo())) {
                productNoDesc += "," + t.getProductNo();
            }
            //跟单总数量计算
            trackHead.setNumber(trackHead.getNumber() + t.getNumber());
        }
        trackHead.setProductNoDesc(productNoDesc.replaceFirst(",", ""));
        trackHead.setNumberComplete(numberComplete);
        if (numberComplete < trackHead.getNumber()) {
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
        return trackHeadFlowService.list(queryWrapperTrackFlow);
    }

    @Override
    public List<Map> selectTrackStoreCount(String drawingNos) {
        return trackHeadMapper.selectTrackStoreCount(drawingNos);
    }

    @Override
    public void addTrackHeadProductNo(String flowId, String productNo) {
        //查询跟单、跟单生产线信息
        TrackFlow trackFlow = trackHeadFlowService.getById(flowId);
        String trackHeadId = trackFlow.getTrackHeadId();
        TrackHead trackHead = this.getById(trackHeadId);

        trackHead.setProductNoDesc(trackHead.getDrawingNo() + " " + productNo);
        trackHead.setProductNo(productNo);
        this.updateById(trackHead);

        trackFlow.setProductNo(trackHead.getDrawingNo() + " " + productNo);
        trackHeadFlowService.updateById(trackFlow);
    }

    @Override
    public void trackHeadData(String id) {
        TrackHead trackHead = this.getById(id);
        QueryWrapper<TrackFlow> queryWrapperTrackFlow = new QueryWrapper<>();
        queryWrapperTrackFlow.eq("track_head_id", id);
        List<TrackFlow> trackFlowList = trackHeadFlowService.list(queryWrapperTrackFlow);
        boolean isNotSchedule = true;
        boolean isSchedule = true;
        boolean isFinish = true;
        for (TrackFlow trackFlow : trackFlowList) {
            if (!TrackHead.STATUS_0.equals(trackFlow.getStatus())) {
                isNotSchedule = false;
            }
            if (!TrackHead.STATUS_2.equals(trackFlow.getStatus())) {
                isFinish = false;
            }
        }
        //当存非全部未派工，或者非全部完工显示为在制
        if (isNotSchedule || isFinish) {
            isSchedule = false;
        }
        if (isNotSchedule) {
            UpdateWrapper<TrackHead> updateWrapperTrackHead = new UpdateWrapper<>();
            updateWrapperTrackHead.eq("id", id);
            updateWrapperTrackHead.set("status", "0");
            this.update(updateWrapperTrackHead);
        }
        if (isSchedule) {
            UpdateWrapper<TrackHead> updateWrapperTrackHead = new UpdateWrapper<>();
            updateWrapperTrackHead.eq("id", id);
            updateWrapperTrackHead.set("status", "1");
            this.update(updateWrapperTrackHead);
        }
        if (isFinish) {
            UpdateWrapper<TrackHead> updateWrapperTrackHead = new UpdateWrapper<>();
            updateWrapperTrackHead.eq("id", id);
            updateWrapperTrackHead.set("status", "2");
            this.update(updateWrapperTrackHead);
        }
        //计划数据更新
        planService.planData(trackHead.getWorkPlanId());
        orderService.orderDataTrackHead(trackHead);
    }

    /**
     * 功能描述: 跟单交库，并同事维护flow表，计划、订单表信息
     *
     * @param id 跟单id
     * @Author: zhiqiang.lu
     * @Date: 2022/9/27 8:57
     * @return: void
     **/
    @Override
    public void trackHeadDelivery(String id) {
        try {
            //更新flow状态更新
            UpdateWrapper<TrackFlow> updateWrapperTrackFlow = new UpdateWrapper<>();
            updateWrapperTrackFlow.eq("track_head_id", id);
            updateWrapperTrackFlow.set("status", "9");
            trackHeadFlowService.update(updateWrapperTrackFlow);

            //更新跟单状态动作
            System.out.println("-------------------");
            System.out.println(id);
            TrackHead trackHead = trackHeadMapper.selectById(id);
            trackHead.setStatus("9");
            trackHeadMapper.updateById(trackHead);

            //计划数据更新
            planService.planData(trackHead.getWorkPlanId());

            //订单数据更新
            orderService.orderDataTrackHead(trackHead);
        } catch (Exception e) {
            log.error("跟单交库方法 error [{}]", e);
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    @Override
    public int queryCountByWorkNo(String projectBomId) {
        int i = 0;
        QueryWrapper<TrackHead> queryTrackHead = new QueryWrapper<>();
        queryTrackHead.eq("project_bom_id", projectBomId);
        i += this.count(queryTrackHead);
        QueryWrapper<Plan> queryPlan = new QueryWrapper<>();
        queryPlan.eq("project_bom", projectBomId);
        i += planService.count(queryPlan);
        return i;
    }
}
