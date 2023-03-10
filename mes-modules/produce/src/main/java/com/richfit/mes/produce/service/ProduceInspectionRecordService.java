package com.richfit.mes.produce.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.enmus.InspectionRecordTypeEnum;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.entity.quality.InspectionPowerVo;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.quality.InspectionPowerService;
import com.richfit.mes.produce.utils.Code;
import com.richfit.mes.produce.utils.RecordStrategyFactory;
import com.richfit.mes.produce.utils.WordUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/***
 * 探伤记录
 * @author renzewen
 */
@Service
@Slf4j
public class ProduceInspectionRecordService {
    private final static String IS_DOING = "1"; //探伤任务开工
    private final static int IS_STATUS = 1;
    private final static int BACKOUT_STATUS = 2;
    private final static int IS_SCHEDULE = 1;
    private final static int IS_ASSIGN = 1; //委托单已派工
    @Autowired
    private ProduceInspectionRecordMtService produceInspectionRecordMtService;
    @Autowired
    private ProduceInspectionRecordRtService produceInspectionRecordRtService;
    @Autowired
    private ProduceInspectionRecordPtService produceInspectionRecordPtService;
    @Autowired
    private ProduceInspectionRecordUtService produceInspectionRecordUtService;

    @Autowired
    private ProduceDefectsInfoService produceDefectsInfoService;
    @Autowired
    private ProduceItemInspectInfoService produceItemInspectInfoService;
    @Autowired
    private WordUtil wordUtil;
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private TrackItemInspectionService trackItemInspectionService;
    @Autowired
    private TrackHeadMapper trackHeadMapper;
    @Autowired
    private ProbeInfoService probeInfoService;
    @Autowired
    private CodeRuleService codeRuleService;
    @Autowired
    private TrackHeadService trackHeadService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private TrackAssignService trackAssignService;
    @Resource
    public PublicService publicService;
    @Autowired
    private InspectionPowerService inspectionPowerService;
    @Autowired
    private TrackHeadFlowService trackHeadFlowService;

    /**
     * 查询探伤列表
     *
     * @param inspectionPowerVo
     * @return
     */
    public IPage<InspectionPower> page(InspectionPowerVo inspectionPowerVo) {

        //跟单工序查询
        QueryWrapper<InspectionPower> queryWrapper = getProwerQueryWrapper(inspectionPowerVo);

        Page<InspectionPower> assignPowers = inspectionPowerService.page(new Page<InspectionPower>(inspectionPowerVo.getPage(), inspectionPowerVo.getLimit()), queryWrapper);
        //列表值转换给前端用于显示
        for (InspectionPower record : assignPowers.getRecords()) {
            if (!ObjectUtil.isEmpty(record.getConsignor())) {
                String consignor = record.getConsignor();
                TenantUserVo data = systemServiceClient.getUserById(consignor).getData();
                //委托人
                record.setConsignor(ObjectUtil.isEmpty(data) ? null : data.getEmplName());
                //委托单位
                record.setComeFromDepart(systemServiceClient.getTenantById(record.getTenantId()).getData().getTenantName());
                String startDoingUser = record.getStartDoingUser();
                //开工人
                if (!StringUtils.isEmpty(startDoingUser)) {
                    record.setStartDoingUser(systemServiceClient.getUserById(startDoingUser).getData().getEmplName());
                }
                //探伤审核人
                if (!StringUtils.isEmpty(record.getAuditBy()) && !record.getAuditBy().equals("/")) {
                    record.setAuditBy(systemServiceClient.getUserById(record.getAuditBy()).getData().getEmplName());
                }
                //探伤检验人
                if (!StringUtils.isEmpty(record.getCheckBy())) {
                    record.setCheckBy(systemServiceClient.getUserById(record.getCheckBy()).getData().getEmplName());
                }
                //是否存在探伤记录赋值，便于前端按钮判断
                List<ProduceItemInspectInfo> list = new ArrayList<>();
                if (!StringUtils.isEmpty(record.getId())) {
                    QueryWrapper<ProduceItemInspectInfo> itemInspectInfoQueryWrapper = new QueryWrapper<>();
                    itemInspectInfoQueryWrapper.eq("power_id", record.getId());
                    list = produceItemInspectInfoService.list(itemInspectInfoQueryWrapper);
                }
                record.setIsHaveRecord(list.size() > 0 ? 1 : 0);
            }
        }
        //为探伤任务赋跟单和工序的的一些属性
        setHeadAndItemInfoToPower(assignPowers.getRecords());

        return assignPowers;
    }

    /**
     * 跟单和工序属性赋值
     *
     * @param assignPowers
     */
    private void setHeadAndItemInfoToPower(List<InspectionPower> assignPowers) {
        for (InspectionPower inspectionPower : assignPowers) {
            //赋跟单属性
            TrackHead trackHead = trackHeadMapper.selecProjectNametById(inspectionPower.getHeadId());
            if (!ObjectUtil.isEmpty(trackHead)) {
                inspectionPower.setTrackNo(trackHead.getTrackNo());
                inspectionPower.setWorkNo(trackHead.getWorkNo());
                //产品名称
                inspectionPower.setProductName(trackHead.getProductName());
                //项目名称
                inspectionPower.setProjectName(trackHead.getProjectName());
                //材质
                inspectionPower.setTexture(trackHead.getTexture());
            }
            //赋工序属性
            TrackItem item = trackItemService.getById(inspectionPower.getItemId());
            if (!ObjectUtil.isEmpty(item)) {
                //工序名称
                inspectionPower.setOptName(item.getOptName());
                //工序号
                inspectionPower.setOptNo(item.getOptNo());
                //产品编号
                inspectionPower.setProductNo(item.getProductNo());
            }
            QueryWrapper<ProduceItemInspectInfo> itemInspectInfoQueryWrapper = new QueryWrapper<>();
            itemInspectInfoQueryWrapper.eq("power_id", inspectionPower.getId()).eq("is_new", "1");
            List<ProduceItemInspectInfo> list = produceItemInspectInfoService.list(itemInspectInfoQueryWrapper);
            //完工的时候下载报告的
            if (list.size() > 0) {
                inspectionPower.setRecordId(list.get(0).getInspectRecordId());
            }
        }
    }


    /**
     * 探伤任务构造查询参数
     *
     * @param inspectionPowerVo
     * @return
     */
    private QueryWrapper<InspectionPower> getProwerQueryWrapper(InspectionPowerVo inspectionPowerVo) {
        QueryWrapper<InspectionPower> queryWrapper = new QueryWrapper<InspectionPower>();

        if (!StringUtils.isEmpty(inspectionPowerVo.getTenantId())) {
            queryWrapper.eq("tenant_id", inspectionPowerVo.getTenantId());
        }

        //委托单号
        queryWrapper.eq(!StringUtils.isEmpty(inspectionPowerVo.getOrderNo()), "order_no", inspectionPowerVo.getOrderNo());
        //已审核
        if ("1".equals(inspectionPowerVo.getIsAudit())) {
            queryWrapper.isNotNull("audit_by");
        } else if ("0".equals(inspectionPowerVo.getIsAudit())) {
            //未审核
            queryWrapper.isNull("audit_by");
        }
        if ("0".equals(inspectionPowerVo.getIsDoing())) {
            //待报工 （包括未开工和当前登陆人已开工的）
            queryWrapper.and(wapper3 -> wapper3.eq("is_doing", "0").or(wapper -> wapper.eq("start_doing_user", SecurityUtils.getCurrentUser().getUserId()).and(wapper2 -> wapper2.eq("is_doing", "1"))));
        } else if ("1".equals(inspectionPowerVo.getIsDoing())) {
            //已完工
            queryWrapper.eq("is_doing", "2");
        }

        if (!StringUtils.isEmpty(inspectionPowerVo.getTrackNo())) {
            String trackNo = inspectionPowerVo.getTrackNo().replaceAll(" ", "");
            queryWrapper.inSql("id", "select id from  produce_inspection_power where head_id in ( select id from produce_track_head where replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') LIKE '" + trackNo + '%' + "')");
        }
        if (!StringUtils.isEmpty(inspectionPowerVo.getProductName())) {
            queryWrapper.inSql("id", "select id from  produce_inspection_power where head_id in ( select id from produce_track_head where product_name LIKE '" + inspectionPowerVo.getProductName() + '%' + "')");
        }
        if (!StringUtils.isEmpty(inspectionPowerVo.getProductNo())) {
            String productNo = inspectionPowerVo.getProductNo().replaceAll(" ", "");
            queryWrapper.inSql("id", "select id from  produce_inspection_power where item_id in ( select id from produce_track_item where replace(product_no,' ','') LIKE '" + productNo + '%' + "')");
        }
        if (!StringUtils.isEmpty(inspectionPowerVo.getStartTime())) {
            queryWrapper.ge("date_format(power_time, '%Y-%m-%d')", inspectionPowerVo.getStartTime());
        }
        if (!StringUtils.isEmpty(inspectionPowerVo.getEndTime())) {
            queryWrapper.le("date_format(power_time, '%Y-%m-%d')", inspectionPowerVo.getEndTime());
        }
        if (!StringUtils.isEmpty(inspectionPowerVo.getIsExistHeadInfo())) {
            //有源委托单
            queryWrapper.isNotNull("0".equals(inspectionPowerVo.getIsExistHeadInfo()), "item_id");
            //无源委托单
            queryWrapper.isNull("1".equals(inspectionPowerVo.getIsExistHeadInfo()), "item_id");
        }
        //查询自己租户的
        queryWrapper.eq(!StringUtils.isEmpty(inspectionPowerVo.getBranchCode()), "inspection_depart", inspectionPowerVo.getBranchCode());
        //已经委托的探伤委托单
        queryWrapper.eq("status", IS_STATUS);
        //图号查询
        queryWrapper.likeLeft(!StringUtils.isEmpty(inspectionPowerVo.getDrawNo()), "draw_no", inspectionPowerVo.getDrawNo());
        //检测类型
        queryWrapper.eq(!StringUtils.isEmpty(inspectionPowerVo.getTempType()), "temp_type", inspectionPowerVo.getTempType());
        //排序
        if (!StringUtils.isEmpty(inspectionPowerVo.getOrderCol())) {
            OrderUtil.query(queryWrapper, inspectionPowerVo.getOrderCol(), inspectionPowerVo.getOrder());
        } else {
            queryWrapper.orderByDesc("power_time");
        }
        return queryWrapper;
    }

    /**
     * 保存探伤记录
     *
     * @return
     */
    public CommonResult saveRecords(ProduceInspectionRecordDto produceInspectionRecordDto) throws Exception {
        //要保存的记录实体
        JSONObject jsonObject = produceInspectionRecordDto.getInspectionRecord();
        //获取模板类型
        String tempType = produceInspectionRecordDto.getTempType();
        //branchCode
        String branchCode = produceInspectionRecordDto.getBranchCode();
        //已审核的修改，需要本地保存一份审核记录，故将新修改的新增
        if (!StringUtils.isEmpty(jsonObject.getString("id"))) {
            produceInspectionRecordDto.setInspectionRecord(jsonObject);
            //powerIds赋值
            QueryWrapper<ProduceItemInspectInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("inspect_record_id", jsonObject.getString("id"))
                    .eq("is_new", "1");
            List<ProduceItemInspectInfo> list = produceItemInspectInfoService.list(queryWrapper);
            List<String> powerIds = list.stream().map(ProduceItemInspectInfo::getPowerId).collect(Collectors.toList());
            produceInspectionRecordDto.setPowerIds(powerIds);
            jsonObject.remove("id");
        } else {
            //如果是新增委托，保存流水号
            if (!StringUtils.isEmpty(tempType) && !ObjectUtil.isEmpty(jsonObject.get("recordNo"))) {
                codeRuleService.updateCode("inspection_code_" + tempType, null, jsonObject.get("recordNo").toString(), null, SecurityUtils.getCurrentUser().getTenantId(), branchCode);
                //保存报告号
                produceInspectionRecordDto.getInspectionRecord().put("reportNo",Code.valueOnUpdate("inspection_reports_" + tempType, SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService));
            }

        }
        return saveRecord(produceInspectionRecordDto);
    }

    /**
     * 保存探伤记录方法
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult saveRecord(ProduceInspectionRecordDto produceInspectionRecordDto) throws Exception {
        //获取模板类型
        String tempType = produceInspectionRecordDto.getTempType();
        //要保存的记录实体
        JSONObject jsonObject = produceInspectionRecordDto.getInspectionRecord();
        //检验员赋值（当前登陆人）
        jsonObject.put("check_by", SecurityUtils.getCurrentUser().getUserId());
        //使用上一次数据的情况过滤数据
        jsonObject.remove("modifyTime");
        jsonObject.remove("modifyBy");
        jsonObject.remove("createTime");
        jsonObject.remove("createBy");
        jsonObject.remove("isAudit");
        jsonObject.remove("auditRemark");
        //缺陷记录
        List<ProduceDefectsInfo> produceDefectsInfos = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("defectsInfoList")), ProduceDefectsInfo.class);
        //探头信息
        List<ProbeInfo> probeInfoList = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("probeInfoList")), ProbeInfo.class);
        //探伤任务ids
        List<String> powerIds = produceInspectionRecordDto.getPowerIds();
        //探伤记录id
        String recordId = null;

        if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
            //保存探伤记录
            ProduceInspectionRecordMt produceInspectionRecordMt = jsonObject.toJavaObject(ProduceInspectionRecordMt.class);
            produceInspectionRecordMtService.saveOrUpdate(produceInspectionRecordMt);
            recordId = produceInspectionRecordMt.getId();
        } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
            ProduceInspectionRecordPt produceInspectionRecordPt = jsonObject.toJavaObject(ProduceInspectionRecordPt.class);
            produceInspectionRecordPtService.saveOrUpdate(produceInspectionRecordPt);
            recordId = produceInspectionRecordPt.getId();
        } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
            ProduceInspectionRecordRt produceInspectionRecordRt = jsonObject.toJavaObject(ProduceInspectionRecordRt.class);
            produceInspectionRecordRtService.saveOrUpdate(produceInspectionRecordRt);
            recordId = produceInspectionRecordRt.getId();
        } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
            ProduceInspectionRecordUt produceInspectionRecordUt = jsonObject.toJavaObject(ProduceInspectionRecordUt.class);
            produceInspectionRecordUtService.saveOrUpdate(produceInspectionRecordUt);
            recordId = produceInspectionRecordUt.getId();
        } else {
            throw new GlobalException(ResultCode.INVALID_ARGUMENTS.getMessage(), ResultCode.INVALID_ARGUMENTS);
        }

        //新增记录需要任务and探伤记录绑定操作
        if (ObjectUtil.isEmpty(jsonObject.get("id"))) {
            List<ProduceItemInspectInfo> produceItemInspectInfos = new ArrayList<>();
            for (String powerId : powerIds) {
                ProduceItemInspectInfo produceItemInspectInfo = new ProduceItemInspectInfo();
                produceItemInspectInfo.setPowerId(powerId);
                produceItemInspectInfo.setInspectRecordId(recordId);
                produceItemInspectInfo.setTempType(tempType);

                produceItemInspectInfo.setAuditBy(String.valueOf(jsonObject.get("auditBy")));
                //检验人为当前新建探伤记录的人
                produceItemInspectInfo.setCheckBy(SecurityUtils.getCurrentUser().getUserId());
                //状态为未审核
                produceItemInspectInfo.setIsAudit("0");
                //最新的记录标识
                produceItemInspectInfo.setIsNew("1");
                //探伤记录号
                produceItemInspectInfo.setRecordNo(String.valueOf(jsonObject.get("recordNo")));
                //核验结果（0、合格  1、不合格）
                produceItemInspectInfo.setInspectionResults(String.valueOf(jsonObject.get("inspectionResults")));
                produceItemInspectInfos.add(produceItemInspectInfo);
                //修改之前的记录为历史记录
                UpdateWrapper<ProduceItemInspectInfo> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("power_id", powerId)
                        .set("is_new", "0");
                produceItemInspectInfoService.update(updateWrapper);
            }
            produceItemInspectInfoService.saveBatch(produceItemInspectInfos);
        }
        //修改探伤任务的最新探伤记录信息
        if (powerIds.size() > 0) {
            UpdateWrapper<InspectionPower> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", powerIds)
                    .set("audit_by", String.valueOf(jsonObject.get("auditBy")))
                    .set("check_by", SecurityUtils.getCurrentUser().getUserId())
                    .set("inspect_record_no", String.valueOf(jsonObject.get("recordNo")))
                    .set(StringUtils.isEmpty(String.valueOf(jsonObject.get("reportNo"))), "report_no", String.valueOf(jsonObject.get("reportNo")))
                    .set("insp_temp_type", tempType)
                    .set("flaw_detection", String.valueOf(jsonObject.get("inspectionResults")))
                    .set("audit_status", 0);
            inspectionPowerService.update(updateWrapper);
        }

        //ut保存探头
        if (!ObjectUtil.isEmpty(probeInfoList) && InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
            for (ProbeInfo probeInfo : probeInfoList) {
                probeInfo.setRecordId(recordId);
                probeInfo.setId(null);
            }
            probeInfoService.saveBatch(probeInfoList);
        }


        //rt保存缺陷记录
        if (!ObjectUtil.isEmpty(produceDefectsInfos) && InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
            for (ProduceDefectsInfo produceDefectsInfo : produceDefectsInfos) {
                produceDefectsInfo.setRecordId(recordId);
                produceDefectsInfo.setId(null);
            }
            produceDefectsInfoService.saveBatch(produceDefectsInfos);
        }

        return null;
    }

    /**
     * 根据工序id查询探伤记录
     *
     * @param powerId
     * @return
     */
    public List<Map<String, Object>> queryLastInfoByPowerId(String powerId, String isAudit, String isNew) {
        //在探伤主表里过滤
        QueryWrapper<ProduceItemInspectInfo> itemInspectInfoQueryWrapper = new QueryWrapper<>();
        itemInspectInfoQueryWrapper.eq("power_id", powerId);

        if (!StringUtils.isEmpty(isAudit)) {
            //itemInspectInfoQueryWrapper.eq("is_audit", isAudit);
        }
        //历史记录
        itemInspectInfoQueryWrapper.ne(isNew.equals("0"), "is_new", "1");
        itemInspectInfoQueryWrapper.ne(isNew.equals("0"), "is_audit", "0");
        //新纪录
        itemInspectInfoQueryWrapper.eq(isNew.equals("1"), "is_new", "1");

        //探伤记录填写页面 根据登陆人 = 检验人查询
        //itemInspectInfoQueryWrapper.eq("check_by", SecurityUtils.getCurrentUser().getUserId());
       /* if (CHECK.equals(checkOrAudit)) {

        } else if (AUDIT.equals(checkOrAudit)) {
            //探伤记录审核页面 根据登陆人 = 审核人查询
            itemInspectInfoQueryWrapper.and(wrapper->wrapper.eq("audit_by", SecurityUtils.getCurrentUser().getUserId()).or(wrapper2->wrapper2.eq("audit_by","/")));
        }*/
        List<ProduceItemInspectInfo> list = produceItemInspectInfoService.list(itemInspectInfoQueryWrapper);
        //按照模板类型分组  key->模板类型  value->探伤记录id
        Map<String, List<String>> tempValues = list.stream().collect(Collectors.groupingBy(ProduceItemInspectInfo::getTempType, Collectors.mapping(ProduceItemInspectInfo::getInspectRecordId, Collectors.toList())));
        //要返回的集合
        List<Object> returnList = new ArrayList<>();
        DateTime date1 = DateUtil.date();
        tempValues.forEach((tempType, ids) -> {
            if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
                returnList.addAll(produceInspectionRecordMtService.queryListByIds(ids));
            } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
                returnList.addAll(produceInspectionRecordPtService.queryListByIds(ids));
            } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
                returnList.addAll(produceInspectionRecordRtService.queryListByIds(ids));
            } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
                returnList.addAll(produceInspectionRecordUtService.queryListByIds(ids));
            }
        });
        log.info("时间:" + String.valueOf(DateUtil.betweenMs(date1, DateUtil.date())));
        DateTime date2 = DateUtil.date();

        List<Map<String, Object>> listMap = new ArrayList<>();
        //转换map 顺便file详情查询
        for (Object o : returnList) {
            listMap.add(objectToMap(o));
        }
        log.info("时间:" + String.valueOf(DateUtil.betweenMs(date2, DateUtil.date())));
        //根据修改时间排序
        if (listMap.size() > 0) {
            listMap.sort((t1, t2) ->
                    t2.get("modifyTime").toString().compareTo(t1.get("modifyTime").toString())
            );
        }

        return listMap;
    }

    /**
     * 返回最近一条记录
     *
     * @param powerId
     * @return
     */
    public Object queryLastInfoByPowerId(String powerId) {
        //所有记录 isnew=1 为新纪录
        List<Map<String, Object>> inspects = queryLastInfoByPowerId(powerId, null, "1");
        if (inspects.size() > 0) {
            return inspects.get(0);
        }
        return null;
    }

    /**
     * 根据记录id查询探伤记录详情
     *
     * @param id
     * @return
     */
    public Object queryInfoByRecordId(String id) {
        QueryWrapper<ProduceItemInspectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("inspect_record_id", id)
                .eq("is_new", "1");
        List<ProduceItemInspectInfo> list = produceItemInspectInfoService.list(queryWrapper);
        if (list.size() > 0) {
            ProduceItemInspectInfo produceItemInspectInfo = list.get(0);
            Object object = null;
            if (InspectionRecordTypeEnum.MT.getType().equals(produceItemInspectInfo.getTempType())) {
                object = produceInspectionRecordMtService.getById(produceItemInspectInfo.getInspectRecordId());
            } else if (InspectionRecordTypeEnum.PT.getType().equals(produceItemInspectInfo.getTempType())) {
                object = produceInspectionRecordPtService.getById(produceItemInspectInfo.getInspectRecordId());
            } else if (InspectionRecordTypeEnum.RT.getType().equals(produceItemInspectInfo.getTempType())) {
                object = produceInspectionRecordRtService.getById(produceItemInspectInfo.getInspectRecordId());
            } else if (InspectionRecordTypeEnum.UT.getType().equals(produceItemInspectInfo.getTempType())) {
                object = produceInspectionRecordUtService.getById(produceItemInspectInfo.getInspectRecordId());
            } else {
                throw new GlobalException(ResultCode.INVALID_ARGUMENTS.getMessage(), ResultCode.INVALID_ARGUMENTS);
            }
            Map<String, Object> map = objectToMap(object);
            InspectionPower power = inspectionPowerService.getById(produceItemInspectInfo.getPowerId());
            //赋跟单属性
            TrackHead trackHead = trackHeadMapper.selecProjectNametById(power.getHeadId());
            if (!ObjectUtil.isEmpty(trackHead)) {
                map.put("workNo", trackHead.getTrackNo());
                //产品名称
                map.put("productName", trackHead.getProductName());
                //项目名称
                map.put("projectName", trackHead.getProjectName());
                //材质
                map.put("texture", trackHead.getTexture());
                //跟单类型
                map.put("classes", trackHead.getClasses());
            }
            //赋工序属性
            TrackItem item = trackItemService.getById(power.getItemId());
            if (!ObjectUtil.isEmpty(item)) {
                //工序名称
                map.put("optName", item.getOptName());
                //工序号
                map.put("optNo", item.getOptNo());
                //产品编号
                map.put("productNo", item.getProductNo());
                //租户id便于审核时查询对应公司的质检人员
                map.put("tenantId", item.getTenantId());
            }
            return map;

        }
        return null;
    }

    /**
     * 批量撤回探伤记录
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean backoutRecord(List<String> powerIds) {

        QueryWrapper<ProduceItemInspectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("power_id", powerIds)
                .eq("is_new", "1");
        List<ProduceItemInspectInfo> list = produceItemInspectInfoService.list(queryWrapper);
        //校验
        List<ProduceItemInspectInfo> checkList = list.stream().filter(item -> !StringUtils.isEmpty(item.getIsAudit()) && !"0".equals(item.getIsAudit())).collect(Collectors.toList());
        if (checkList.size() > 0) {
            throw new GlobalException("有已经被审核的记录，不能执行撤回操作", ResultCode.FAILED);
        }

        //删除最新的探伤记录
        produceItemInspectInfoService.remove(queryWrapper);
        //将上一条记录改为当前记录
        QueryWrapper<ProduceItemInspectInfo> historyListQueryWrapper = new QueryWrapper<>();
        historyListQueryWrapper.in("power_id", powerIds)
                .orderByDesc("create_time");
        List<ProduceItemInspectInfo> historyList = produceItemInspectInfoService.list(historyListQueryWrapper);
        Map<String, List<ProduceItemInspectInfo>> historyMap = historyList.stream().collect(Collectors.groupingBy(item -> item.getPowerId()));

        historyMap.forEach((key, value) -> {
            if (value.size() > 0) {
                UpdateWrapper<ProduceItemInspectInfo> produceItemInspectInfoUpdateWrapper = new UpdateWrapper<>();
                produceItemInspectInfoUpdateWrapper.eq("power_id", key).eq("inspect_record_id", value.get(0).getInspectRecordId()).set("is_new", "1");
                produceItemInspectInfoService.update(produceItemInspectInfoUpdateWrapper);
                //探伤任务最新记录改为最新的信息
                UpdateWrapper<InspectionPower> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", key)
                        .set("audit_by", String.valueOf(value.get(0).getAuditBy()))
                        .set("check_by", SecurityUtils.getCurrentUser().getUserId())
                        .set("insp_temp_type", String.valueOf(value.get(0).getTempType()))
                        .set("audit_status", String.valueOf(value.get(0).getIsAudit()))
                        .set("audit_remark", String.valueOf(value.get(0).getAuditRemark()));
                inspectionPowerService.update(updateWrapper);
            }
        });
        return true;
    }

    /**
     * 探伤记录审核列表
     *
     * @param inspectionPowerVo
     * @return
     */
    public Object queryRecordByAuditBy(InspectionPowerVo inspectionPowerVo) {

        //从中间表查询审核人是当前用户、最新的探伤记录
        QueryWrapper<ProduceItemInspectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .and(warpper1 -> warpper1.eq("audit_by", SecurityUtils.getCurrentUser().getUserId()).or(warpper -> warpper.eq("audit_by", "/")))
                .eq(!StringUtils.isEmpty(inspectionPowerVo.getRecordNo()), "record_no", inspectionPowerVo.getRecordNo())
                .ge(!StringUtils.isEmpty(inspectionPowerVo.getStartTime()), "date_format(modify_time, '%Y-%m-%d')", inspectionPowerVo.getStartTime())
                .le(!StringUtils.isEmpty(inspectionPowerVo.getEndTime()), "date_format(modify_time, '%Y-%m-%d')", inspectionPowerVo.getEndTime())
                .inSql("power_id", "select a.power_id from (select max(power_id) as power_id from produce_item_inspect_info where is_new = '1' GROUP BY inspect_record_id) a")
                .orderByDesc("modify_time");
        if (!StringUtils.isEmpty(inspectionPowerVo.getIsAudit())) {
            if ("0".equals(inspectionPowerVo.getIsAudit())) {
                queryWrapper.eq("is_audit", "0")
                        .eq("is_new", "1");
            } else {
                queryWrapper.ne("is_audit", "0");
            }
        }

        //列表
        List<ProduceItemInspectInfo> inspects = produceItemInspectInfoService.list(queryWrapper);

        //分页，后续要操作的list提高接口效率
        List<ProduceItemInspectInfo> subList = inspects.stream().skip((inspectionPowerVo.getPage() - 1) * inspectionPowerVo.getLimit()).limit(inspectionPowerVo.getLimit()).
                collect(Collectors.toList());
        //为要返回的探伤记录赋值
        List<Map<String, Object>> maps = new ArrayList<>();
        if (subList.size() > 0) {
            //按照模板类型分组  key->模板类型  value->探伤记录id
            Map<String, List<String>> tempValues = subList.stream().collect(Collectors.groupingBy(ProduceItemInspectInfo::getTempType, Collectors.mapping(ProduceItemInspectInfo::getInspectRecordId, Collectors.toList())));
            //探伤记录列表
            List<Object> recordList = new ArrayList<>();
            tempValues.forEach((tempType, ids) -> {
                if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
                    recordList.addAll(produceInspectionRecordMtService.queryListByIds(ids));
                } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
                    recordList.addAll(produceInspectionRecordPtService.queryListByIds(ids));
                } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
                    recordList.addAll(produceInspectionRecordRtService.queryListByIds(ids));
                } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
                    recordList.addAll(produceInspectionRecordUtService.queryListByIds(ids));
                }
            });

            //探伤委托单信息，便于给探伤记录列表赋值
            List<String> powerIds = subList.stream().map(ProduceItemInspectInfo::getPowerId).collect(Collectors.toList());
            List<InspectionPower> inspectionPowers = inspectionPowerService.listByIds(powerIds);
            Map<String, InspectionPower> powerMap = inspectionPowers.stream().collect(Collectors.toMap(InspectionPower::getInspectRecordNo, Function.identity(), (x1, x2) -> x1));

            for (Object o : recordList) {
                Map<String, Object> map = objectToMap(o);
                InspectionPower power = powerMap.get(String.valueOf(map.get("recordNo")));
                if (!ObjectUtil.isEmpty(power)) {
                    List<InspectionPower> powers = new ArrayList<>();
                    powers.add(power);
                    setHeadAndItemInfoToPower(powers);
                    map.put("optName", powers.get(0).getOptName());
                    map.put("optNo", powers.get(0).getOptNo());
                    map.put("headId", powers.get(0).getHeadId());
                    map.put("trackNo", powers.get(0).getTrackNo());
                    map.put("texture", powers.get(0).getTexture());
                    map.put("productNo", powers.get(0).getProductNo());
                    map.put("drawNo", powers.get(0).getDrawNo());
                    map.put("checkType", powers.get(0).getCheckType());
                    //是否存在探伤记录赋值，便于前端按钮判断
                    List<ProduceItemInspectInfo> list = new ArrayList<>();
                    if (!StringUtils.isEmpty(powers.get(0))) {
                        QueryWrapper<ProduceItemInspectInfo> itemInspectInfoQueryWrapper = new QueryWrapper<>();
                        itemInspectInfoQueryWrapper.eq("power_id", powers.get(0).getId());
                        list = produceItemInspectInfoService.list(itemInspectInfoQueryWrapper);
                    }
                    map.put("isHaveRecord", list.size() > 0 ? 1 : 0);
                }
                //ge
                maps.add(map);
            }
        }


        //总页数
        int pages = 0;
        if (inspects.size() > 0) {
            pages = inspects.size() % inspectionPowerVo.getLimit() == 0 ? inspects.size() / inspectionPowerVo.getLimit() : inspects.size() / inspectionPowerVo.getLimit() + 1;
        }

        //总数
        int total = inspects.size();
        Map<String, Object> returnMap = new HashMap<>();
        maps.sort((t1, t2) -> t1.get("modifyTime").toString().compareTo(t2.get("modifyTime").toString()));
        returnMap.put("records", maps);
        returnMap.put("pages", pages);
        returnMap.put("total", total);
        return returnMap;
    }


    /**
     * 激活下工序
     *
     * @param itemId             探伤工序id
     * @param itemId             探伤工序id
     * @param flawDetectioRemark 探伤备注
     * @param flawDetection      探伤结果
     * @param tempType           探伤记录模板
     * @param recordNo           探伤记录编号
     * @param checkBy            探伤检验人
     * @param auditBy            探伤审核人
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditSubmitRecord(String itemId, String flawDetectioRemark, Integer flawDetection, String tempType, String recordNo, String checkBy, String auditBy) {
        //同步回跟单工序表
        TrackItem trackItem = trackItemService.getById(itemId);
        trackItem.setFlawDetectionRemark(flawDetectioRemark);
        trackItem.setFlawDetection(flawDetection);
        trackItem.setTempType(tempType);
        trackItem.setInspectRecordNo(recordNo);
        trackItem.setCheckBy(checkBy);
        trackItem.setAuditBy(auditBy);
        //质检字段赋值
        //探伤工序审核通过的不需要质检
        trackItem.setIsExistQualityCheck(0);
        //探伤工序不走调度
        trackItem.setIsExistScheduleCheck(0);
        //更改状态 标识当前工序完成
        trackItem.setIsDoing(2);
        trackItem.setIsOperationComplete(1);
        trackItem.setIsQualityComplete(1);

        //工序激活
        boolean next = trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0);
        if (next) {
            trackItem.setIsFinalComplete(String.valueOf(1));
        }
        trackItemService.updateById(trackItem);

        //派工状态设置为完成
        UpdateWrapper<Assign> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("ti_id", trackItem.getId());
        //state = 2 (已完工)
        updateWrapper.set("state", 2);
        trackAssignService.update(updateWrapper);

        //激活下工序
        if (next) {
            Map<String, String> map = new HashMap<String, String>(1);
            map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
            publicService.activationProcess(map);
        }

        return true;
    }

    /**
     * 派到质检不合格处理
     *
     * @param itemId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean toCheck(String itemId, String inspector, String checkBranch) {
        //同步回跟单工序表
        TrackItem trackItem = trackItemService.getById(itemId);
        //质检字段赋值
        //质检
        trackItem.setIsExistQualityCheck(1);
        //探伤工序不走调度
        trackItem.setIsExistScheduleCheck(0);
        //更改状态 标识当前工序完成
        trackItem.setIsDoing(2);
        //当前工序是否最终完成
        trackItem.setIsOperationComplete(1);
        //是否质检确认
        trackItem.setIsQualityComplete(0);
        //是否复检
        trackItem.setIsRecheck("1");

        trackItem.setQualityCheckBy(inspector);
        trackItem.setQualityCheckBranch(checkBranch);

        trackItemService.updateById(trackItem);

        //派工状态设置为完成
        UpdateWrapper<Assign> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("ti_id", trackItem.getId());
        //state = 2 (已完工)
        updateWrapper.set("state", 2);
        trackAssignService.update(updateWrapper);

        return true;

    }


    /**
     * 报告导出doc
     */
    public void exoprtReport(HttpServletResponse response, String id) throws IOException, TemplateException {
        //探伤记录、探伤工序、探伤模板信息
        ProduceItemInspectInfo produceItemInspectInfo = new ProduceItemInspectInfo();
        //根据探伤记录id定位
        QueryWrapper<ProduceItemInspectInfo> itemInspectInfoQueryWrapper = new QueryWrapper<>();
        itemInspectInfoQueryWrapper.eq("inspect_record_id", id);
        List<ProduceItemInspectInfo> produceItemInspectInfos = produceItemInspectInfoService.list(itemInspectInfoQueryWrapper);
        if (produceItemInspectInfos.size() > 0) {
            produceItemInspectInfo = produceItemInspectInfos.get(0);
        } else {
            throw new GlobalException(ResultCode.ITEM_NOT_FOUND.getMessage(), ResultCode.ITEM_NOT_FOUND);
        }

        //跟单信息
        TrackHead trackHead = null;
        //探伤记录
        Map<String, Object> recordInfo = new HashMap<>();
        //查询探伤工序
        InspectionPower power = inspectionPowerService.getById(produceItemInspectInfo.getPowerId());
        TrackItem trackItem = trackItemService.getById(power.getItemId());

        if (!ObjectUtil.isEmpty(produceItemInspectInfo)) {
            List<Map<String, Object>> list = new ArrayList<>();
            if (InspectionRecordTypeEnum.MT.getType().equals(produceItemInspectInfo.getTempType())) {
                list = produceInspectionRecordMtService.listMaps(new QueryWrapper<ProduceInspectionRecordMt>().eq("id", produceItemInspectInfo.getInspectRecordId()));
            } else if (InspectionRecordTypeEnum.PT.getType().equals(produceItemInspectInfo.getTempType())) {
                list = produceInspectionRecordPtService.listMaps(new QueryWrapper<ProduceInspectionRecordPt>().eq("id", produceItemInspectInfo.getInspectRecordId()));
            } else if (InspectionRecordTypeEnum.RT.getType().equals(produceItemInspectInfo.getTempType())) {
                list = produceInspectionRecordRtService.listMaps(new QueryWrapper<ProduceInspectionRecordRt>().eq("id", produceItemInspectInfo.getInspectRecordId()));
            } else if (InspectionRecordTypeEnum.UT.getType().equals(produceItemInspectInfo.getTempType())) {
                list = produceInspectionRecordUtService.listMaps(new QueryWrapper<ProduceInspectionRecordUt>().eq("id", produceItemInspectInfo.getInspectRecordId()));
            }
            if (!CollectionUtil.isEmpty(list)) {
                recordInfo = list.get(0);
            }
        }

        if (!ObjectUtil.isEmpty(recordInfo)) {
            //缺陷记录
            QueryWrapper<ProduceDefectsInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("record_id", recordInfo.get("id")).orderByAsc("serial_number");
            List<ProduceDefectsInfo> defectsInfoList = produceDefectsInfoService.list(queryWrapper);
            recordInfo.put("defectsInfoList", defectsInfoList);
            //探头
            List<ProbeInfo> probeInfoList = probeInfoService.list(new QueryWrapper<ProbeInfo>().eq("record_id", recordInfo.get("id")).orderByAsc("serial_num"));
            recordInfo.put("probeInfoList", probeInfoList);
        }


        Map<String, Object> dataMap = new HashMap<>();
        //填充数据
        createDataMap(trackHead, recordInfo, dataMap, produceItemInspectInfo.getTempType(), power);

        //根据模板类型获取模板和导出文件名
        Map<String, String> tempNameAndDocNameMap = checkTempNameAndDocName(produceItemInspectInfo.getTempType());
        //导出
        wordUtil.exoprtReport(response, dataMap, tempNameAndDocNameMap.get("tempName"), tempNameAndDocNameMap.get("docName"));
    }

    /**
     * 构造导出填充数据dataMap
     *
     * @param trackHead
     * @param recordInfo
     * @param dataMap
     * @param tempType
     */
    private void createDataMap(TrackHead trackHead, Map<String, Object> recordInfo, Map<String, Object> dataMap, String tempType, InspectionPower power) throws IOException {
        if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
            createMtDataMap(recordInfo, dataMap);
        } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
            createPtDataMap(recordInfo, dataMap);
        } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
            createRtDataMap(recordInfo, dataMap);
        } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
            createUtDataMap(recordInfo, dataMap);
        }
        //图号
        dataMap.put("drawingNo", ObjectUtil.isEmpty(trackHead) ? power.getDrawNo() : trackHead.getDrawingNo());
        //零件名称
        dataMap.put("materialName", ObjectUtil.isEmpty(trackHead) ? power.getSampleName() : trackHead.getMaterialName());
        //材质
        if (!ObjectUtil.isEmpty(trackHead)) {
            dataMap.put("texture", trackHead.getTexture());
        }

        dataMap.put("year", String.valueOf(DateUtil.year(DateUtil.date())));
        dataMap.put("month", DateUtil.thisMonth() + 1);
        dataMap.put("day", DateUtil.dayOfMonth(DateUtil.date()));
    }

    //mt模板填充
    private void createMtDataMap(Map<String, Object> recordInfo, Map<String, Object> dataMap) throws IOException {
        //mt探伤记录
        ProduceInspectionRecordMt produceInspectionRecordMt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordMt.class);

        //人员信息转换
        if (!ObjectUtil.isEmpty(produceInspectionRecordMt.getAuditBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordMt.getAuditBy()).getData();
            produceInspectionRecordMt.setAuditBy(data.getUserAccount());
        }
        if (!ObjectUtil.isEmpty(produceInspectionRecordMt.getCheckBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordMt.getCheckBy()).getData();
            produceInspectionRecordMt.setCheckBy(data.getUserAccount());
        }

        dataMap.putAll(JSON.parseObject(JSON.toJSONString(produceInspectionRecordMt), Map.class));
        //图片base64编码
        if (!StringUtils.isEmpty(produceInspectionRecordMt.getDiagramAttachmentId())) {
            dataMap.put("img", systemServiceClient.getBase64Code(produceInspectionRecordMt.getDiagramAttachmentId()).getData());
        }

    }

    //rt模板填充
    private void createRtDataMap(Map<String, Object> recordInfo, Map<String, Object> dataMap) throws IOException {
        //mt探伤记录
        ProduceInspectionRecordRt produceInspectionRecordRt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordRt.class);

        if (!ObjectUtil.isEmpty(produceInspectionRecordRt.getAuditBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordRt.getAuditBy()).getData();
            produceInspectionRecordRt.setAuditBy(data.getUserAccount());
        }
        if (!ObjectUtil.isEmpty(produceInspectionRecordRt.getCheckBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordRt.getCheckBy()).getData();
            produceInspectionRecordRt.setCheckBy(data.getUserAccount());
        }

        dataMap.putAll(JSON.parseObject(JSON.toJSONString(produceInspectionRecordRt), Map.class));
        //图片base64编码
        if (!StringUtils.isEmpty(produceInspectionRecordRt.getDiagramAttachmentId())) {
            dataMap.put("img", systemServiceClient.getBase64Code(produceInspectionRecordRt.getDiagramAttachmentId()).getData());
        }
    }

    //pt模板填充
    private void createPtDataMap(Map<String, Object> recordInfo, Map<String, Object> dataMap) throws IOException {
        //pt探伤记录
        ProduceInspectionRecordPt produceInspectionRecordPt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordPt.class);

        if (!ObjectUtil.isEmpty(produceInspectionRecordPt.getAuditBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordPt.getAuditBy()).getData();
            produceInspectionRecordPt.setAuditBy(data.getUserAccount());
        }
        if (!ObjectUtil.isEmpty(produceInspectionRecordPt.getCheckBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordPt.getCheckBy()).getData();
            produceInspectionRecordPt.setCheckBy(data.getUserAccount());
        }
        dataMap.putAll(JSON.parseObject(JSON.toJSONString(produceInspectionRecordPt), Map.class));

        if (!StringUtils.isEmpty(produceInspectionRecordPt.getDiagramAttachmentId())) {
            //图片base64编码
            dataMap.put("img", systemServiceClient.getBase64Code(produceInspectionRecordPt.getDiagramAttachmentId()).getData());
        }

    }

    //ut模板填充
    private void createUtDataMap(Map<String, Object> recordInfo, Map<String, Object> dataMap) throws IOException {
        //ut探伤记录
        ProduceInspectionRecordUt produceInspectionRecordUt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordUt.class);

        if (!ObjectUtil.isEmpty(produceInspectionRecordUt.getAuditBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordUt.getAuditBy()).getData();
            produceInspectionRecordUt.setAuditBy(data.getUserAccount());
        }
        if (!ObjectUtil.isEmpty(produceInspectionRecordUt.getCheckBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordUt.getCheckBy()).getData();
            produceInspectionRecordUt.setCheckBy(data.getUserAccount());
        }
        dataMap.putAll(JSON.parseObject(JSON.toJSONString(produceInspectionRecordUt), Map.class));

        //图片base64编码
        if (!StringUtils.isEmpty(produceInspectionRecordUt.getDiagramAttachmentId())) {
            dataMap.put("img", systemServiceClient.getBase64Code(produceInspectionRecordUt.getDiagramAttachmentId()).getData());
        }
        //探头列表
        List<ProbeInfo> probeInfoList = probeInfoService.list(new QueryWrapper<ProbeInfo>()
                .eq("record_id", produceInspectionRecordUt.getId())
                .orderByAsc("serial_num"));
        //探头列表
        dataMap.put("probeInfoList", probeInfoList);

    }

    /**
     * 根据模板类型获取模板和导出文件名
     *
     * @param tempType
     * @return
     */
    private Map<String, String> checkTempNameAndDocName(String tempType) {
        Map<String, String> returnMap = new HashMap<>();
        if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
            returnMap.put("tempName", "mtTemp.ftl");
            returnMap.put("docName", "磁粉探伤报告");
        } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
            returnMap.put("tempName", "ptTemp.ftl");
            returnMap.put("docName", "渗透探伤报告");
        } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
            returnMap.put("tempName", "rtTemp.ftl");
            returnMap.put("docName", "射线探伤报告");
        } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
            returnMap.put("tempName", "utTemp.ftl");
            returnMap.put("docName", "超声探伤报告");
        } else {
            throw new GlobalException(ResultCode.ITEM_NOT_FOUND.getMessage(), ResultCode.FAILED);
        }
        return returnMap;
    }


    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public Map<String, Object> objectToMap(Object obj) {
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(obj));

        //插入附件信息
        /*if(!ObjectUtil.isEmpty(jsonObject.get("diagramAttachmentId"))){
            List<Object> fileInfos = new ArrayList<>();
            String[] diagramAttachmentIds = jsonObject.get("diagramAttachmentId").toString().split(",");
            for (String diagramAttachmentId : diagramAttachmentIds) {
                Attachment data = systemServiceClient.attachment(diagramAttachmentId).getData();
                data.setPreviewUrl(String.valueOf(systemServiceClient.getPreviewUrl(diagramAttachmentId).getData()));
            }
            jsonObject.put("fileList", fileInfos);
        }*/

        if (!ObjectUtil.isEmpty(jsonObject.get("auditBy"))) {
            String auditBy = jsonObject.get("auditBy").toString();
            if (!"/".equals(auditBy)) {
                TenantUserVo data = systemServiceClient.getUserById(auditBy).getData();
                jsonObject.put("auditByInfo", data);
            }
        }
        if (!ObjectUtil.isEmpty(jsonObject.get("checkBy"))) {
            String checkBy = jsonObject.get("checkBy").toString();
            if (!"/".equals(checkBy)) {
                TenantUserVo data = systemServiceClient.getUserById(checkBy).getData();
                jsonObject.put("checkByInfo", data);
            }
        }

        return jsonObject;
    }

    /**
     * 探伤记录审核
     * 功能描述：1、修改探伤记录审核状态会影响探伤任务的状态，探伤任务和探伤记录为多对多的关系，所以当一个探伤记录状态改变会影响多个探伤任务审核状态
     * 2、探伤工序和探伤任务为一对多的关系，故需要根据对应跟单工序的探伤任务状态来判断工序是否探伤完成
     * 3、跟单工序状态判断：多个探伤任务情况下，只要有一个为不合格便将此跟单发送质检部门复检做不合格处理，当全部为合格并且审核通过便激活下工序此探伤过程完工
     *
     * @param id
     * @param tempType
     * @param isAudit
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean auditByRecord(String id, String tempType, String isAudit, String auditRemark, String inspector, String checkBranch) {
        //根据模板修改审核记录信息
        RecordStrategyFactory.getRecordStragegy(tempType).updateAuditInfo(id, isAudit, auditRemark);
        //修改中间表的数据
        UpdateWrapper<ProduceItemInspectInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("inspect_record_id", id).set("is_audit", isAudit).set("audit_by", SecurityUtils.getCurrentUser().getUserId()).set("audit_remark", auditRemark);
        produceItemInspectInfoService.update(updateWrapper);
        //修改探伤任务数据（将最新的记录信息更新到任务中）
        QueryWrapper<ProduceItemInspectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_new", "1")
                .eq("inspect_record_id", id);
        List<ProduceItemInspectInfo> inspects = produceItemInspectInfoService.list(queryWrapper);
        //要修改的任务id
        List<String> powerIds = inspects.stream().map(ProduceItemInspectInfo::getPowerId).collect(Collectors.toList());
        UpdateWrapper<InspectionPower> inspectionPowerUpdateWrapper = new UpdateWrapper<>();
        inspectionPowerUpdateWrapper.in("id", powerIds)
                .set("audit_status", isAudit)
                .set("audit_remark", auditRemark)
                .set("audit_by", SecurityUtils.getCurrentUser().getUserId())
                .set("inspector", inspector)
                .set("check_branch", checkBranch);
        inspectionPowerService.update(inspectionPowerUpdateWrapper);
        //审核通过探伤任务处理
        if ("1".equals(isAudit)) {
            isToNextItemDeal(powerIds);
        }

        return true;
    }

    /**
     * 探伤记录审核
     * 审核通过探伤任务处理功能描述：探伤任务分为有源跟单（带有跟单工序的探伤任务，需要下工序处理的），无源跟单（质检部门自己创建的探伤任务没用跟单工序的）
     *
     * @param powerIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void isToNextItemDeal(List<String> powerIds) {
        QueryWrapper<InspectionPower> powerQueryWrapper = new QueryWrapper<>();
        powerQueryWrapper.in("id", powerIds);
        //要处理的探伤任务
        List<InspectionPower> powers = inspectionPowerService.list(powerQueryWrapper);
        //有源的
        List<InspectionPower> headItems = powers.stream().filter(item -> !StringUtils.isEmpty(item.getItemId())).collect(Collectors.toList());
        //无源的
        List<InspectionPower> noHeadItems = powers.stream().filter(item -> StringUtils.isEmpty(item.getItemId())).collect(Collectors.toList());
        //有源跟单工序处理
        dealItemBypowerTask(headItems);

        //无源的探伤任务处理  完工
        for (InspectionPower noHeadItem : noHeadItems) {
            noHeadItem.setIsDoing("2");
            inspectionPowerService.updateById(noHeadItem);
        }


    }

    /**
     * 根据探伤任务审核状态变化判断跟单工序走向
     *
     * @param headItems
     */
    private void dealItemBypowerTask(List<InspectionPower> headItems) {
        //关联此记录的有源的跟单工序
        List<String> items = headItems.stream().map(InspectionPower::getItemId).collect(Collectors.toList());
        if (items.size() > 0) {
            QueryWrapper<InspectionPower> itemTasksWrapper = new QueryWrapper<>();
            itemTasksWrapper.in("item_id", items);
            List<InspectionPower> itemPowerList = inspectionPowerService.list(itemTasksWrapper);
            //分组   判断关联此记录的跟单工序的各个探伤任务状态
            Map<String, List<InspectionPower>> itemPowerGroup = itemPowerList.stream().collect(Collectors.groupingBy(InspectionPower::getItemId));
            //跟单工序走向处理
            for (Map.Entry<String, List<InspectionPower>> entry : itemPowerGroup.entrySet()) {
                String key = entry.getKey();
                List<InspectionPower> values = entry.getValue();
                //校验该工序的任务否全部审核通过
                List<InspectionPower> collect = values.stream().filter(item -> item.getAuditStatus() == 1).collect(Collectors.toList());
                if (values.size() == collect.size()) {
                    //是否激活下工序标识
                    boolean flag = true;
                    for (InspectionPower value : values) {
                        //核验结果
                        String inspectionResults = value.getFlawDetection();
                        //不合格
                        if ("0".equals(inspectionResults)) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        //激活下工序
                        auditSubmitRecord(key, null, null, null, null, null, null);
                    } else {
                        //如果不合格 发送最近审核不合格的质检人 降序排列取第一个
                        List<InspectionPower> noStandardList = values.stream().filter(item -> "0".equals(item.getFlawDetection())).collect(Collectors.toList());
                        noStandardList.sort((t1, t2) -> t2.getModifyTime().compareTo(t1.getModifyTime()));
                        //发送分公司质检
                        toCheck(key, noStandardList.get(0).getInspector(), noStandardList.get(0).getCheckBranch());
                    }
                    for (InspectionPower value : values) {
                        value.setIsDoing("2");
                        inspectionPowerService.updateById(value);
                    }
                }
            }
        }
    }

    /**
     * 批量新增委托单
     */
    public CommonResult saveInspectionPowers(List<InspectionPower> inspectionPowers) throws Exception {

        //1、修改保存委托信息
        for (InspectionPower inspectionPower : inspectionPowers) {

            inspectionPower.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            if (StringUtils.isEmpty(inspectionPower.getId())) {
                //获取委托单号
                String orderNo = codeRuleService.gerCode("order_no", null, null, SecurityUtils.getCurrentUser().getTenantId(), inspectionPower.getBranchCode()).getCurValue();
                inspectionPower.setOrderNo(orderNo);
                //保存探伤委托单号
                Code.update("order_no", inspectionPower.getOrderNo(), SecurityUtils.getCurrentUser().getTenantId(), inspectionPower.getBranchCode(), codeRuleService);
                //委托人赋值
                inspectionPower.setConsignor(SecurityUtils.getCurrentUser().getUserId());
            }
            //委托时间
            inspectionPower.setPowerTime(DateUtil.format(DateUtil.date(), "YYYY-MM-dd HH:mm:ss"));
            //待开工
            inspectionPower.setIsDoing("0");
            //保存
            inspectionPowerService.saveOrUpdate(inspectionPower);
        }
        //2、派工处理(有源)  只有从跟单派工发起的委托可以触发派工
        if (inspectionPowers.size() > 0 && !StringUtils.isEmpty(inspectionPowers.get(0).getHeadId())) {
            TrackItem trackItem = trackItemService.getById(inspectionPowers.get(0).getItemId());
            TrackHead trackHead = trackHeadService.getById(inspectionPowers.get(0).getHeadId());
            String branchCode = inspectionPowers.get(0).getBranchCode();
            //已经派工状态
            trackItem.setIsSchedule(IS_SCHEDULE);
            trackItemService.updateById(trackItem);
            //将跟单状态改为在制
            if (!StringUtils.isEmpty(trackHead.getStatus()) || "0".equals(trackHead.getStatus())) {
                trackHead.setStatus("1");
                trackHeadService.updateById(trackHead);
                UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
                update.set("status", "1");
                update.eq("id", trackItem.getFlowId());
                trackHeadFlowService.update(update);
            }
            InspectionPower inspectionPower = inspectionPowers.get(0);
            //如果是跟单派工发起的委托，修改跟单工序为已派工
            if (!StringUtils.isEmpty(inspectionPower.getItemId())) {
                //派工表
                Assign assign = new Assign();
                assign.setTiId(inspectionPower.getItemId()); //工序id
                assign.setTrackId(inspectionPower.getHeadId());
                assign.setSiteId("");  //南北站branchCode
                assign.setSiteName(inspectionPower.getInspectionDepart()); //南站北站
                assign.setQty(inspectionPower.getNum()); //派工数量 如果后续需要控制探伤数量的话需要处理
                assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                assign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
                assign.setAssignTime(new Date());
                assign.setModifyTime(new Date());
                assign.setCreateTime(new Date());
                assign.setFlowId(trackItem.getFlowId());
                assign.setTrackNo(trackHead.getTrackNo());
                assign.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                assign.setClasses(trackHead.getClasses());
                assign.setBranchCode(branchCode);
                //assign.setPowerId(inspectionPower.getId());
                trackAssignService.save(assign);
            }
        }

        return CommonResult.success(true);
    }

    /**
     * 保存委托单
     */
    public CommonResult saveInspectionPower(InspectionPower inspectionPower) throws Exception {

        inspectionPower.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        if (StringUtils.isEmpty(inspectionPower.getId())) {
            //保存探伤委托单号
            Code.update("order_no", inspectionPower.getOrderNo(), SecurityUtils.getCurrentUser().getTenantId(), inspectionPower.getBranchCode(), codeRuleService);
            //委托人赋值
            inspectionPower.setConsignor(SecurityUtils.getCurrentUser().getUserId());
        } else {
            InspectionPower byId = inspectionPowerService.getById(inspectionPower.getId());
            if (byId.getStatus() == IS_STATUS) {
                return CommonResult.failed("该委托单已经发起委托，不能修改");
            }
        }
        //委托时间
        inspectionPower.setPowerTime(DateUtil.format(DateUtil.date(), "YYYY-MM-dd HH:mm:ss"));
        //待开工
        inspectionPower.setIsDoing("0");
        //保存
        inspectionPowerService.saveOrUpdate(inspectionPower);

        return CommonResult.success(true);
    }

    /**
     * 探伤开工
     *
     * @param ids 探伤委托单ids
     */
    public boolean startsWork(List<String> ids) {
        if (ids.size() > 0) {
            QueryWrapper<InspectionPower> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", ids);
            //要开工的任务
            List<InspectionPower> inspectionPowers = inspectionPowerService.list(queryWrapper);
            //有源的
            List<InspectionPower> haveList = inspectionPowers.stream().filter(item -> !StringUtils.isEmpty(item.getItemId())).collect(Collectors.toList());
            //有源的需要跟新派工信息
            List<String> powerIds = haveList.stream().map(InspectionPower::getId).collect(Collectors.toList());

            if (powerIds.size() > 0) {
                //更新跟单工序信息
                for (InspectionPower inspectionPower : haveList) {
                    TrackItem trackItem = trackItemService.getById(inspectionPower.getItemId());
                    trackItem.setIsDoing(1);
                    trackItem.setStartDoingTime(new Date());
                    trackItem.setStartDoingUser(SecurityUtils.getCurrentUser().getUsername());
                    trackItemService.updateById(trackItem);
                    //更新派工信息
                    UpdateWrapper<Assign> assignUpdateWrapper = new UpdateWrapper<>();
                    assignUpdateWrapper.in("ti_id", inspectionPower.getItemId())
                            //设置开工状态
                            .set("state", 1);
                    trackAssignService.update(assignUpdateWrapper);
                }
            }
            //更新探伤委托单开工状态
            for (InspectionPower inspectionPower : inspectionPowers) {
                inspectionPower.setIsDoing(IS_DOING);
                inspectionPower.setStartDoingTime(new Date());
                inspectionPower.setStartDoingUser(SecurityUtils.getCurrentUser().getUserId());
            }
            inspectionPowerService.updateBatchById(inspectionPowers);

        }
        return true;
    }

    /**
     * 批量委托
     */
    public boolean powerOrder(List<String> ids) {
        if (ids.size() > 0) {
            UpdateWrapper<InspectionPower> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", ids)
                    .set("status", IS_STATUS)
                    .set("power_time", DateUtil.date());
            return inspectionPowerService.update(updateWrapper);
        }
        return true;
    }

    /**
     * 批量撤回
     */
    public boolean backOutOrder(List<String> ids, String backRemark) {
        if (ids.size() > 0) {
            UpdateWrapper<InspectionPower> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", ids)
                    .set("status", BACKOUT_STATUS)
                    .set("back_remark", backRemark);
            return inspectionPowerService.update(updateWrapper);
        }
        return true;
    }

    /**
     * 探伤委托指派人
     */
    public boolean assignPower(List<String> ids, String assignBy) throws GlobalException {

        if (ids.size() > 0) {
            QueryWrapper<InspectionPower> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", ids);
            List<InspectionPower> list = inspectionPowerService.list(queryWrapper);
            //校验 已经派工的不能再次指派
            List<InspectionPower> assginByNullList = list.stream().filter(item -> !StringUtils.isEmpty(item.getAssignBy())).collect(Collectors.toList());
            if (assginByNullList.size() > 0) {
                throw new GlobalException("选中的委托单中，有已经指派的委托单", ResultCode.FORBIDDEN);
            }
            //指派派工人
            UpdateWrapper<InspectionPower> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", ids)
                    .set("assign_by", assignBy)
                    //改为已派工状态
                    .set("assign_status", IS_ASSIGN)
                    .set("assign_time", DateUtil.date());
            return inspectionPowerService.update(updateWrapper);
        }
        return true;
    }

    /**
     * 委托单分页查询
     *
     * @param inspectionPowerVo
     * @return
     */
    public IPage queryPowerOrderPage(InspectionPowerVo inspectionPowerVo) {
        QueryWrapper<InspectionPower> queryWrapper = new QueryWrapper<>();
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getOrderNo())) {
            queryWrapper.eq("order_no", inspectionPowerVo.getOrderNo());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getInspectionDepart())) {
            queryWrapper.eq("inspection_depart", inspectionPowerVo.getInspectionDepart());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getSampleName())) {
            queryWrapper.eq("sample_name", inspectionPowerVo.getSampleName());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getStartTime())) {
            queryWrapper.ge("date_format(power_time, '%Y-%m-%d')", inspectionPowerVo.getStartTime());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getEndTime())) {
            queryWrapper.le("date_format(power_time, '%Y-%m-%d')", inspectionPowerVo.getEndTime());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getDrawNo())) {
            queryWrapper.eq("draw_no", inspectionPowerVo.getDrawNo());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getStatus())) {
            queryWrapper.in("status", inspectionPowerVo.getStatus().split(","));
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getBranchCode())) {
            queryWrapper.eq("branch_code", inspectionPowerVo.getBranchCode());
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("consignor", SecurityUtils.getCurrentUser().getUserId());
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getOrderCol())) {
            OrderUtil.query(queryWrapper, inspectionPowerVo.getOrderCol(), inspectionPowerVo.getOrder());
        } else {
            queryWrapper.orderByDesc("power_time");
        }
        if (!org.springframework.util.StringUtils.isEmpty(inspectionPowerVo.getIsExistHeadInfo())) {
            //有源委托单
            queryWrapper.isNotNull("0".equals(inspectionPowerVo.getIsExistHeadInfo()), "item_id");
            //无源委托单
            queryWrapper.isNull("1".equals(inspectionPowerVo.getIsExistHeadInfo()), "item_id");
        }
        Page<InspectionPower> page = inspectionPowerService.page(new Page<InspectionPower>(inspectionPowerVo.getPage(), inspectionPowerVo.getLimit()), queryWrapper);
        //委托人转换
        for (InspectionPower record : page.getRecords()) {
            if (!ObjectUtil.isEmpty(record.getConsignor())) {
                String consignor = record.getConsignor();
                TenantUserVo data = systemServiceClient.getUserById(consignor).getData();
                record.setConsignor(data.getEmplName());
            }
            String itemId = record.getItemId();
            String headId = record.getHeadId();
            if (!StringUtils.isEmpty(itemId)) {
                TrackItem trackItem = trackItemService.getById(itemId);
                TrackHead trackHead = trackHeadService.getById(headId);
                record.setTrackNo(trackHead.getTrackNo());
                record.setOptNo(trackItem.getOptNo());
                record.setOptName(trackItem.getOptName());
                record.setProductNo(trackItem.getProductNo());
                record.setTrackType(trackHead.getTrackType());
            }
            QueryWrapper<ProduceItemInspectInfo> itemInspectInfoQueryWrapper = new QueryWrapper<>();
            itemInspectInfoQueryWrapper.eq("power_id", record.getId()).eq("is_new", "1");
            List<ProduceItemInspectInfo> list = produceItemInspectInfoService.list(itemInspectInfoQueryWrapper);
            //完工的时候下载报告的
            if (list.size() > 0) {
                record.setRecordId(list.get(0).getInspectRecordId());
            }
        }
        return page;
    }

    /*
     * 导出委托单
     * @param parentId
     * @param branchCode
     * @param rsp
     */
    public void exportExcel(InspectionPowerVo inspectionPowerVo, HttpServletResponse rsp) {
        QueryWrapper<InspectionPower> queryWrapper = new QueryWrapper<>();
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getOrderNo())) {
            queryWrapper.eq("order_no", inspectionPowerVo.getOrderNo());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getInspectionDepart())) {
            queryWrapper.eq("inspection_depart", inspectionPowerVo.getInspectionDepart());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getSampleName())) {
            queryWrapper.eq("sample_name", inspectionPowerVo.getSampleName());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getStartTime())) {
            queryWrapper.ge("date_format(power_time, '%Y-%m-%d')", inspectionPowerVo.getStartTime());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getEndTime())) {
            queryWrapper.le("date_format(power_time, '%Y-%m-%d')", inspectionPowerVo.getEndTime());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getDrawNo())) {
            queryWrapper.eq("draw_no", inspectionPowerVo.getDrawNo());
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getStatus())) {
            queryWrapper.in("status", inspectionPowerVo.getStatus().split(","));
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getBranchCode())) {
            queryWrapper.eq("branch_code", inspectionPowerVo.getBranchCode());
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("consignor", SecurityUtils.getCurrentUser().getUserId());
        if (!org.apache.commons.lang.StringUtils.isEmpty(inspectionPowerVo.getOrderCol())) {
            OrderUtil.query(queryWrapper, inspectionPowerVo.getOrderCol(), inspectionPowerVo.getOrder());
        } else {
            queryWrapper.orderByDesc("power_time");
        }
        if (!org.springframework.util.StringUtils.isEmpty(inspectionPowerVo.getIsExistHeadInfo())) {
            //有源委托单
            queryWrapper.isNotNull("0".equals(inspectionPowerVo.getIsExistHeadInfo()), "item_id");
            //无源委托单
            queryWrapper.isNull("1".equals(inspectionPowerVo.getIsExistHeadInfo()), "item_id");
        }
        List<InspectionPower> list = inspectionPowerService.list(queryWrapper);
        TenantUserVo data = systemServiceClient.getUserById(SecurityUtils.getCurrentUser().getUserId()).getData();
        for (InspectionPower inspectionPower : list) {
            if (inspectionPower.getStatus() == 0) {
                inspectionPower.setStatusShow("待委托");
            }
            if (inspectionPower.getStatus() == 1) {
                inspectionPower.setStatusShow("已委托");
            }
            if (inspectionPower.getStatus() == 2) {
                inspectionPower.setStatusShow("驳回");
            }
            inspectionPower.setConsignor(data.getEmplName());
            inspectionPower.setComeFromDepart(data.getBelongOrgId());
        }

        try {

            String fileName = "委托单导出_" + DateUtil.format(DateUtil.date(), "YYYY-MM-dd") + ".xlsx";

            String[] columnHeaders = {"委托单号", "状态", "钻机号", "样品名称", "图号", "检测类型", "产品类型", "数量", "探伤站", "探伤类型", "单重", "长度", "处数", "创建人", "创建单位", "创建时间"};

            String[] fieldNames = {"orderNo", "statusShow", "drilNo", "sampleName", "drawNo", "tempType", "productType", "num", "inspectionDepart", "checkType", "single", "length", "reviseNum", "consignor", "comeFromDepart", "createTime"};

            //export
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public CommonResult importPowerInfosExcel(MultipartFile file, String branchCode) {
        CommonResult result = CommonResult.success(true);
        //封装工时信息实体类
        String[] fieldNames = {"drilNo", "drawNo", "sampleName", "inspectionDepart", "checkType", "tempType", "weldString", "castString", "forgString", "fluorescentString", "num", "single", "length", "reviseNum", "priorityString","workpieceAddress"};

        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //模板校验
            //将导入的excel数据生成实体类list
            List<InspectionPower> checkInfo = ExcelUtils.importExcel(excelFile, InspectionPower.class, fieldNames, 2, 0, 0, tempName.toString());
            if (checkInfo.size() > 0) {
                if ("钻机号".equals(checkInfo.get(0).getDrilNo()) &&
                        "图号".equals(checkInfo.get(0).getDrawNo()) &&
                        "样品名称".equals(checkInfo.get(0).getSampleName()) &&
                        "探伤站机构编码".equals(checkInfo.get(0).getInspectionDepart())) {

                } else {
                    return CommonResult.failed("模板不正确!");
                }
            } else {
                return CommonResult.failed("模板不正确!");
            }
            //将导入的excel数据生成实体类list
            List<InspectionPower> list = ExcelUtils.importExcel(excelFile, InspectionPower.class, fieldNames, 3, 0, 0, tempName.toString());
            for (InspectionPower inspectionPower : list) {
                inspectionPower.setBranchCode(branchCode);
                inspectionPower.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                inspectionPower.setWeld(!StringUtils.isEmpty(inspectionPower.getWeldString()) && inspectionPower.getWeldString().equals("是") ? 1 : 0);
                inspectionPower.setCast(!StringUtils.isEmpty(inspectionPower.getCastString()) && inspectionPower.getCastString().equals("是") ? 1 : 0);
                inspectionPower.setForg(!StringUtils.isEmpty(inspectionPower.getForgString()) && inspectionPower.getForgString().equals("是") ? 1 : 0);
                inspectionPower.setFluorescent(!StringUtils.isEmpty(inspectionPower.getFluorescentString()) && inspectionPower.getFluorescentString().equals("是") ? 1 : 0);
                if (!StringUtils.isEmpty(inspectionPower.getPriorityString()) && inspectionPower.getPriorityString().equals("低")) {
                    inspectionPower.setPriority(0);
                }
                if (!StringUtils.isEmpty(inspectionPower.getPriorityString()) && inspectionPower.getPriorityString().equals("中")) {
                    inspectionPower.setPriority(1);
                }
                if (!StringUtils.isEmpty(inspectionPower.getPriorityString()) && inspectionPower.getPriorityString().equals("高")) {
                    inspectionPower.setPriority(2);
                }
            }
            FileUtils.delete(excelFile);
            //保存委托单
            this.saveInspectionPowers(list);

        } catch (Exception e) {
            return CommonResult.failed();
        }
        return result;
    }


    /**
     * 分页查询探伤派工信息
     *
     * @param page
     * @param limit
     * @param startTime
     * @param endTime
     * @param trackNo
     * @param productName
     * @param branchCode
     * @param tenantId
     * @return
     *//*
    public IPage<Assign> assginPage(int page, int limit, String startTime, String endTime, String trackNo, String productName, String productNo, String branchCode, String tenantId, Integer isOperationComplete) {
        QueryWrapper<TrackItemInspection> queryWrapper = new QueryWrapper<TrackItemInspection>();
        if (!StringUtils.isEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }

        if (!StringUtils.isEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') LIKE '" + trackNo + '%' + "')");
        }
        if (!StringUtils.isEmpty(productName)) {
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where product_name LIKE '" + productName + '%' + "')");
        }
        if (!StringUtils.isEmpty(productNo)) {
            queryWrapper.likeLeft("productNo", productNo);
        }
        if (!StringUtils.isEmpty(startTime)) {
            queryWrapper.ge("date_format(modify_time, '%Y-%m-%d')", startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            queryWrapper.le("date_format(modify_time, '%Y-%m-%d')", endTime);
        }
        queryWrapper.orderByDesc("modify_time");
        IPage<TrackItemInspection> trackItemInspections = trackItemInspectionService.page(new Page<TrackItemInspection>(page, limit), queryWrapper);

        //工序ids
        List<String> itemIds = trackItemInspections.getRecords().stream().map(TrackItemInspection::getId).collect(Collectors.toList());

        if (itemIds.size() > 0) {
            IPage<Assign> assigns = trackAssignMapper.queryPageNew(new Page<Assign>(page, limit), new QueryWrapper<Assign>().in("ti_id", itemIds).notIn("state",2));
            for (int i = 0; i < assigns.getRecords().size(); i++) {
                assigns.getRecords().get(i).setAssignPersons(trackAssignPersonMapper.selectList(new QueryWrapper<AssignPerson>().eq("assign_id", assigns.getRecords().get(i).getId())));
            }
            if (null != assigns.getRecords()) {
                for (Assign assign : assigns.getRecords()) {
                    TrackHead trackHead = trackHeadService.getById(assign.getTrackId());
                    TrackItem trackItem = trackItemService.getById(assign.getTiId());
                    assign.setWeight(trackHead.getWeight());
                    assign.setWorkNo(trackHead.getWorkNo());
                    assign.setProductName(trackHead.getProductName());
                    assign.setPartsName(trackHead.getMaterialName());
                    assign.setTotalQuantity(trackItem.getNumber());
                    assign.setDispatchingNumber(trackItem.getAssignableQty());
                    assign.setWorkPlanNo(trackHead.getWorkPlanNo());
                }
            }

            return assigns;
        }
        return null;
    }
*/
}
