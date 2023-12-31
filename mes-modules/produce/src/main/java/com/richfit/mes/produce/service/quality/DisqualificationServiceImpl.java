package com.richfit.mes.produce.service.quality;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.quality.DisqualificationFinalResultMapper;
import com.richfit.mes.produce.dao.quality.DisqualificationMapper;
import com.richfit.mes.produce.enmus.TrackTypeEnum;
import com.richfit.mes.produce.enmus.TypeEnum;
import com.richfit.mes.produce.enmus.UnitEnum;
import com.richfit.mes.produce.entity.quality.*;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.CodeRuleService;
import com.richfit.mes.produce.service.TrackHeadFlowService;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.utils.Code;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: DisqualificationServiceImpl.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年09月29日 15:15:00
 */
@Service
public class DisqualificationServiceImpl extends ServiceImpl<DisqualificationMapper, Disqualification> implements DisqualificationService {

    @Resource
    private TrackHeadFlowService trackHeadFlowService;

    @Resource
    private DisqualificationUserOpinionService userOpinionService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private TrackItemService trackItemService;

    @Resource
    private DisqualificationAttachmentService attachmentService;

    @Resource
    private DisqualificationFinalResultService finalResultService;

    @Resource
    private DisqualificationFinalResultMapper finalResultMapper;

    @Resource
    private DisqualificationMapper disqualificationMapper;

    @Resource
    private CodeRuleService codeRuleService;

    @Autowired
    private DisqualificationAttachmentService disqualificationAttachmentService;

    @Autowired
    private DisqualificationFinalResultService disqualificationFinalResultService;

    @Autowired
    private DisqualificationUserOpinionService disqualificationUserOpinionService;

    // 质量检测部
    public static final String TENANT_ID = "12345678901234567890123456789100";
    // 不合格外协单位
    public static final String UNIT_CODE = "qualityUnqualityUnitW";
    // 不合格常用工序
    public static final String PROCESS_CODE = "qualityUnqualityOpt";
    // 不合格类型
    public static final String UNQUALIFIED_TYPE = "qualityUnqualityType";

    @Override
    public IPage<Disqualification> queryInspector(QueryInspectorDto queryInspectorDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        getDisqualificationByQueryInspectorDto(queryWrapper, queryInspectorDto);
        //只查询本人创建的不合格品申请单
        queryWrapper.eq("dis.create_by", SecurityUtils.getCurrentUser().getUsername());
        OrderUtil.query(queryWrapper, queryInspectorDto.getOrderCol(), queryInspectorDto.getOrder());
        return disqualificationMapper.query(new Page<>(queryInspectorDto.getPage(), queryInspectorDto.getLimit()), queryWrapper);
    }

    private void getDisqualificationByQueryInspectorDto(QueryWrapper queryWrapper, QueryInspectorDto queryInspectorDto) {
        //图号查询
        if (StrUtil.isNotBlank(queryInspectorDto.getDrawingNo())) {
            queryWrapper.like("drawing_no", queryInspectorDto.getDrawingNo());
        }
        //产品名称
        if (StrUtil.isNotBlank(queryInspectorDto.getProductName())) {
            queryWrapper.like("product_name", queryInspectorDto.getProductName());
        }
        //跟单号
        if (StrUtil.isNotBlank(queryInspectorDto.getTrackNo())) {
            queryInspectorDto.setTrackNo(queryInspectorDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + queryInspectorDto.getTrackNo() + "%'");
        }
        //申请单号
        if (StrUtil.isNotBlank(queryInspectorDto.getProcessSheetNo())) {
            queryWrapper.like("process_sheet_no", queryInspectorDto.getProcessSheetNo());
        }
        //申请单状态
        if (StrUtil.isNotBlank(queryInspectorDto.getType())) {
            queryWrapper.eq("type", queryInspectorDto.getType());
        }
        try {
            //开始时间
            if (StrUtil.isNotBlank(queryInspectorDto.getStartTime())) {
                queryWrapper.apply("UNIX_TIMESTAMP(dis.create_time) >= UNIX_TIMESTAMP('" + queryInspectorDto.getStartTime() + " 00:00:00')");
            }
            //结束时间
            if (StrUtil.isNotBlank(queryInspectorDto.getEndTime())) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(queryInspectorDto.getEndTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(dis.create_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
            }
        } catch (Exception e) {
            throw new GlobalException("时间格式处理错误", ResultCode.FAILED);
        }
    }

    @Override
    public IPage<Disqualification> queryCheck(QueryCheckDto queryCheckDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        disqualificationQueryWrapper(queryWrapper, queryCheckDto);
        queryWrapper.eq(StrUtil.isNotBlank(queryCheckDto.getTenantId()), "dis.tenant_id", queryCheckDto.getTenantId());
        //判断是否处理 true=已处理 false=未处理
        if (Boolean.TRUE.equals(queryCheckDto.getIsDispose())) {
            queryWrapper.gt("dis.type", 2);
            queryWrapper.isNotNull("final.quality_control_opinion");
        } else {
            queryWrapper.eq("dis.type", 2);
        }
        queryWrapper.like("dis.quality_check_by", SecurityUtils.getCurrentUser().getUsername() + ",");
        OrderUtil.query(queryWrapper, queryCheckDto.getOrderCol(), queryCheckDto.getOrder());
        return disqualificationMapper.query(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }


    @Override
    public IPage<Disqualification> queryDealWith(QueryCheckDto queryCheckDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        disqualificationQueryWrapper(queryWrapper, queryCheckDto);
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        //判断是否处理 true=已处理 false=未处理
        if (Boolean.TRUE.equals(queryCheckDto.getIsDispose())) {
            queryWrapper.and(wrapper -> wrapper
                    .and(one -> one.gt("dis.type", 3)
                            .eq("final.unit_treatment_one", tenantId)
                            .isNotNull("unit_treatment_one_opinion"))
                    .or(two -> two.gt("dis.type", 4)
                            .eq("final.unit_treatment_two", tenantId)
                            .isNotNull("unit_treatment_two_opinion")));
        } else {
            queryWrapper.and(wrapper -> wrapper
                    .and(one -> one.eq("dis.type", 3).eq("final.unit_treatment_one", tenantId))
                    .or(two -> two.eq("dis.type", 4).eq("final.unit_treatment_two", tenantId)));
        }
        OrderUtil.query(queryWrapper, queryCheckDto.getOrderCol(), queryCheckDto.getOrder());
        return disqualificationMapper.query(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }

    @Override
    public IPage<Disqualification> queryResponsibility(QueryCheckDto queryCheckDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        disqualificationQueryWrapper(queryWrapper, queryCheckDto);
        //判断是否处理 true=已处理 false=未处理
        if (Boolean.TRUE.equals(queryCheckDto.getIsDispose())) {
            queryWrapper.gt("type", 5);
            queryWrapper.isNotNull("responsibility_opinion");
        } else {
            queryWrapper.eq("type", 5);
        }
        OrderUtil.query(queryWrapper, queryCheckDto.getOrderCol(), queryCheckDto.getOrder());
        return disqualificationMapper.query(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }

    @Override
    public IPage<Disqualification> queryTechnology(QueryCheckDto queryCheckDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        disqualificationQueryWrapper(queryWrapper, queryCheckDto);
        //判断是否处理 true=已处理 false=未处理
        if (Boolean.TRUE.equals(queryCheckDto.getIsDispose())) {
            queryWrapper.gt("type", 6);
            queryWrapper.isNotNull("technology_opinion");
        } else {
            queryWrapper.eq("type", 6);
        }
        OrderUtil.query(queryWrapper, queryCheckDto.getOrderCol(), queryCheckDto.getOrder());
        return disqualificationMapper.query(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }

    private void disqualificationQueryWrapper(QueryWrapper<Disqualification> queryWrapper, QueryCheckDto queryCheckDto) {
        //图号查询
        queryWrapper.like(StrUtil.isNotBlank(queryCheckDto.getDrawingNo()), "dis.part_drawing_no", queryCheckDto.getDrawingNo());
        //产品名称
        queryWrapper.like(StrUtil.isNotBlank(queryCheckDto.getProductName()), "dis.product_name", queryCheckDto.getProductName());
        //申请单号
        queryWrapper.like(StrUtil.isNotBlank(queryCheckDto.getProcessSheetNo()), "dis.process_sheet_no", queryCheckDto.getProcessSheetNo());
        //跟单号
        if (StrUtil.isNotBlank(queryCheckDto.getTrackNo())) {
            queryCheckDto.setTrackNo(queryCheckDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(dis.track_no, char(13), ''), char(10), ''),' ', '') like '%" + queryCheckDto.getTrackNo() + "%'");
        }
        try {
            //开始时间
            if (StrUtil.isNotBlank(queryCheckDto.getStartTime())) {
                queryWrapper.apply("UNIX_TIMESTAMP(dis.modify_time) >= UNIX_TIMESTAMP('" + queryCheckDto.getStartTime() + " 00:00:00')");
            }
            //结束时间
            if (StrUtil.isNotBlank(queryCheckDto.getEndTime())) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(queryCheckDto.getEndTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(dis.modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
            }
        } catch (Exception e) {
            throw new GlobalException("时间格式处理错误", ResultCode.FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateDisqualification(DisqualificationDto disqualificationDto) {
        //增加产品编号和数量校验
        List<String> productNoSize = Arrays.asList(disqualificationDto.getProductNo().split(","));
        if ("0".equals(disqualificationDto.getTrackHeadType()) && disqualificationDto.getNumber() != productNoSize.size()) {
            throw new GlobalException("数量与产品编号不匹配", ResultCode.FAILED);
        }
        //先判断流程
        int processJudge = processJudge(disqualificationDto);
        //处理人员信息
        StringBuilder sb = new StringBuilder();
        if (CollUtil.isNotEmpty(disqualificationDto.getUserList())) {
            for (String userAccount : disqualificationDto.getUserList()) {
                sb.append(userAccount).append(",");
            }
        }
        //处理不合格主表数据
        Disqualification disqualification = new Disqualification();
        BeanUtils.copyProperties(disqualificationDto, disqualification);
        disqualification.setType(processJudge);
        disqualification.setQualityCheckBy(sb.toString());
        //判断有没有Id,没有新增tenantId参数
        if (StrUtil.isBlank(disqualification.getId())) {
            disqualification.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        }
        //处理不合格类型
        if (CollectionUtils.isNotEmpty(disqualificationDto.getTypeList())) {
            String type = StringUtils.join(disqualificationDto.getTypeList(), ",");
            disqualification.setDisqualificationType(type);
            disqualification.setMissiveBranch(disqualificationDto.getBranchCode());
        }
        //申请单为空创建申请单
        if (StrUtil.isBlank(disqualification.getProcessSheetNo()) && disqualificationDto.getIsSubmit() == 1) {
            //获取申请单编号
            try {
                String disqualificationNo = Code.value("disqualification_no", SecurityUtils.getCurrentUser().getTenantId(), disqualificationDto.getBranchCode(), codeRuleService);
                disqualification.setProcessSheetNo(disqualificationNo);
                Code.update("disqualification_no", disqualificationNo, SecurityUtils.getCurrentUser().getTenantId(), disqualificationDto.getBranchCode(), codeRuleService);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
                throw new GlobalException("获取申请单编号错误", ResultCode.FAILED);
            }
        }
        this.saveOrUpdate(disqualification);
        if (StrUtil.isNotBlank(disqualificationDto.getTrackItemId())) {
            List<String> split = Arrays.asList(disqualificationDto.getTrackItemId().split(","));
            UpdateWrapper<TrackItem> update = new UpdateWrapper<>();
            update.in("id", split);
            update.set("disqualification_id", disqualification.getId());
            trackItemService.update(update);
        }
        //处理不合格从表数据
        DisqualificationFinalResult finalResult = new DisqualificationFinalResult();
        BeanUtils.copyProperties(disqualificationDto, finalResult);
        finalResult.setId(disqualification.getId());
        if (CollectionUtils.isNotEmpty(disqualificationDto.getAcceptDeviationNoList())) {
            //让步接收产品编号
            finalResult.setAcceptDeviationNo(String.join(",", disqualificationDto.getAcceptDeviationNoList()));
        }
        if (CollectionUtils.isNotEmpty(disqualificationDto.getRepairNoList())) {
            //返修后产品编号
            finalResult.setRepairNo(String.join(",", disqualificationDto.getRepairNoList()));
        }
        if (CollectionUtils.isNotEmpty(disqualificationDto.getRepairQualifiedNoList())) {
            //返修合格产品编号
            finalResult.setRepairQualifiedNo(String.join(",", disqualificationDto.getRepairQualifiedNoList()));
        }
        if (CollectionUtils.isNotEmpty(disqualificationDto.getRepairNotQualifiedNoList())) {
            //返修不合格产品编号
            finalResult.setRepairNotQualifiedNo(String.join(",", disqualificationDto.getRepairNotQualifiedNoList()));
        }
        if (CollectionUtils.isNotEmpty(disqualificationDto.getScrapNoList())) {
            //报废后产品编号
            finalResult.setScrapNo(String.join(",", disqualificationDto.getScrapNoList()));
        }
        if (CollectionUtils.isNotEmpty(disqualificationDto.getSalesReturnNoList())) {
            //退货产品编号
            finalResult.setSalesReturnNo(String.join(",", disqualificationDto.getSalesReturnNoList()));
        }
        //处理意见数据
        TenantUserVo user = systemServiceClient.getUserById(SecurityUtils.getCurrentUser().getUserId()).getData();
        switch (disqualificationDto.getType()) {
            case 0:
            case 1:
                finalResult.setDisqualificationName(user.getEmplName());
                finalResult.setDisqualificationTime(new Date());
                break;
            case 2:
                finalResult.setQualityName(user.getEmplName());
                finalResult.setQualityTime(new Date());
                break;
            case 3:
                finalResult.setTreatmentOneName(user.getEmplName());
                finalResult.setTreatmentOneTime(new Date());
                break;
            case 4:
                finalResult.setTreatmentTwoName(user.getEmplName());
                finalResult.setTreatmentTwoTime(new Date());
                break;
            case 5:
                finalResult.setResponsibilityName(user.getEmplName());
                finalResult.setResponsibilityTime(new Date());
                break;
            case 6:
                finalResult.setTechnologyName(user.getEmplName());
                finalResult.setTechnologyTime(new Date());
                break;
            case 7:
                finalResult.setRecapUser(user.getEmplName());
                finalResult.setRecapTime(new Date());
                break;
            default:
                break;
        }
        finalResult.setTenantId(disqualification.getTenantId());
        finalResultService.saveOrUpdate(finalResult);
        //发布才进行签核记录
        if (1 == disqualificationDto.getIsSubmit()) {
            processingRecord(disqualificationDto, disqualification.getId(), finalResult);
        }
        //处理文件列表
        QueryWrapper<DisqualificationAttachment> queryWrapperAttachment = new QueryWrapper<>();
        queryWrapperAttachment.eq("disqualification_id", disqualification.getId());
        attachmentService.remove(queryWrapperAttachment);
        //2022/12/27  15:13  zhiqiang.lu 附件添加报错，补充初始值
        for (DisqualificationAttachment disqualificationAttachment : disqualification.getAttachmentList()) {
            disqualificationAttachment.setDisqualificationId(disqualification.getId());
            disqualificationAttachment.setBranchCode(disqualification.getBranchCode());
            disqualificationAttachment.setTenantId(disqualification.getTenantId());
        }
        attachmentService.saveAttachment(disqualification.getAttachmentList());
        return true;
    }


    /**
     * 功能描述: 流程个控制方法
     *
     * @param disqualificationDto
     * @Author: xinYu.hou
     * @Date: 2022/12/19 4:19
     * @return: int
     **/
    private int processJudge(DisqualificationDto disqualificationDto) {
        //判断是否发布 1 = 发布 0 = 不发布
        if (1 == disqualificationDto.getIsSubmit()) {
            //开局处理单||质控评审 发布直接进行下一步
            if (1 == disqualificationDto.getType() || 2 == disqualificationDto.getType()) {
                return disqualificationDto.getType() + 1;
            }
            //无源发布
            if (0 == disqualificationDto.getType()) {
                return 2;
            }
            //申请人最后一步填写
            if (7 == disqualificationDto.getType()) {
                return 8;
            }
            //判断是否发起责任裁决
            if (1 == disqualificationDto.getIsResponsibility()) {
                //进入则人裁决
                return 5;
            }
            //判断是否发起技术裁决
            if (1 == disqualificationDto.getIsTechnology()) {
                return 6;
            }
            //判断处理单位一
            if (3 == disqualificationDto.getType()) {
                //处理单位2存在
                if (StrUtil.isNotBlank(disqualificationDto.getUnitTreatmentTwo())) {
                    //返回处理单位2状态
                    return disqualificationDto.getType() + 1;
                } else {
                    //没有直接进入状态7 质检员关闭处理单流程
                    return 7;
                }
            }
            //处理单位2 处理单位填报完 直接进入质检员关闭流程
            if (4 == disqualificationDto.getType()) {
                return 7;
            }
            //责任裁决结束返回处理单位1
            if (5 == disqualificationDto.getType()) {
                //判断是否有处理单位2 有处理单位2状态返回到4
                if (StrUtil.isNotBlank(disqualificationDto.getUnitTreatmentTwo())) {
                    return 4;
                }
                //没有返回处理单位1
                return 3;
            }
            //技术裁决结束返回处理单位1
            if (6 == disqualificationDto.getType()) {
                //判断是否有处理单位2 有处理单位2状态返回到4
                if (StrUtil.isNotBlank(disqualificationDto.getUnitTreatmentTwo())) {
                    return 4;
                }
                //没有返回处理单位1
                return 3;
            }
        }
        //不发布直接返回当前状态
        return disqualificationDto.getType();
    }

    /**
     * 功能描述: 处理签核记录
     *
     * @param disqualificationDto
     * @Author: xinYu.hou
     * @Date: 2023/1/30 16:07
     * @return: void
     **/
    private void processingRecord(DisqualificationDto disqualificationDto, String id, DisqualificationFinalResult finalResult) {
        Integer type = disqualificationDto.getType();
        switch (type) {
            case 1:
                //申请人提交
                saveRecord(id, UnitEnum.getMessage(type) + "提交。不合格情况:" + disqualificationDto.getDisqualificationCondition(), type, finalResult.getDisqualificationName());
                break;
            case 2:
                //质控提交
                saveRecord(id, UnitEnum.getMessage(type) + "提交。意见:" + disqualificationDto.getQualityControlOpinion(), type, finalResult.getQualityName());
                break;
            case 3:
                //处理单位一提交
                saveRecord(id, UnitEnum.getMessage(type) + "提交。意见:" + disqualificationDto.getUnitTreatmentOneOpinion(), type, finalResult.getTreatmentOneName());
                break;
            case 4:
                //处理单位二提交
                saveRecord(id, UnitEnum.getMessage(type) + "提交。意见:" + disqualificationDto.getUnitTreatmentTwoOpinion(), type, finalResult.getTreatmentTwoName());
                break;
            case 5:
                //责任裁决
                saveRecord(id, UnitEnum.getMessage(type) + "提交。责任裁决:" + disqualificationDto.getResponsibilityOpinion(), type, finalResult.getResponsibilityName());
                break;
            case 6:
                //技术裁决
                saveRecord(id, UnitEnum.getMessage(type) + "提交。技术裁决:" + disqualificationDto.getTechnologyOpinion(), type, finalResult.getTechnologyName());
                break;
            case 7:
                //申请人最后一步填写意见
                saveRecord(id, UnitEnum.getMessage(1) + "提交。返修情况:" + disqualificationDto.getQualityControlOpinion(), type, finalResult.getDisqualificationName());
                break;
            default:
                break;
        }
    }

    @Override
    public Boolean updateIsIssue(String id) {
        Disqualification disqualification = this.getById(id);
        if (disqualification == null) {
            throw new GlobalException("未查询到不合格信息", ResultCode.FAILED);
        }
        if (disqualification.getType() != 8) {
            throw new GlobalException("流程还未结束,不允许关单", ResultCode.FAILED);
        }
        UpdateWrapper<Disqualification> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("type", 9);
        updateWrapper.set("close_time", new Date());
        return this.update(updateWrapper);
    }

    @Override
    public List<TenantUserVo> queryUser() {
        //获取branchCode
        String value = value("qualityManagement");
        //获取TenantId
        Branch branch = baseServiceClient.queryTenantIdByBranchCode(value);
        if (branch != null && branch.getTenantId() != null) {
            //根据租户Id查询人员列表
            return systemServiceClient.queryUserByTenantIdAndRole(branch.getTenantId()).getData();
        } else {
            return null;
        }
    }


    @Override
    public DisqualificationItemVo inquiryRequestFormNew(String disqualificationId, String branchCode) {
        //无缘查询详情
        if (StrUtil.isBlank(disqualificationId)) {
            DisqualificationItemVo disqualificationItemVo = new DisqualificationItemVo();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            disqualificationItemVo.setTrackItemId(uuid);
            disqualificationItemVo.setType(0);
            disqualificationItemVo.setSourceType(0);
            disqualificationItemVo.setRepairNoList(Collections.emptyList());
            disqualificationItemVo.setSalesReturnNoList(Collections.emptyList());
            disqualificationItemVo.setAcceptDeviationNoList(Collections.emptyList());
            disqualificationItemVo.setScrapNoList(Collections.emptyList());
            disqualificationItemVo.setBranchCode(branchCode);
            //赋值上一次申请单参数
            DisqualificationItemVo data = this.queryLastTimeDataByCreateBy(branchCode);
            if (data != null) {
                disqualificationItemVo.setQualityCheckBy(data.getQualityCheckBy());
                disqualificationItemVo.setTrackHeadType(data.getTrackHeadType());
                disqualificationItemVo.setTypeList(Arrays.asList(data.getDisqualificationType().split(",")));
                disqualificationItemVo.setDisqualificationType(data.getDisqualificationType());
                disqualificationItemVo.setDiscoverTenant(data.getDiscoverTenant());
                disqualificationItemVo.setUnitResponsibilityWithin(data.getUnitResponsibilityWithin());
                //责任单位外 不为空
                if (StrUtil.isNotBlank(data.getUnitResponsibilityOutside())) {
                    disqualificationItemVo.setUnitResponsibilityOutside(data.getUnitResponsibilityOutside());
                }
                //发现工序 不为空
                if (StrUtil.isNotBlank(data.getDiscoverItem())) {
                    disqualificationItemVo.setDiscoverItem(data.getDiscoverItem());
                }
                disqualificationItemVo.setTotalWeight(data.getTotalWeight());
            }
            return disqualificationItemVo;
        }
        //有源头
        DisqualificationItemVo disqualificationItemVo = new DisqualificationItemVo();
        Disqualification disqualification = this.getById(disqualificationId);
        BeanUtils.copyProperties(disqualification, disqualificationItemVo);
        if (StrUtil.isNotBlank(disqualification.getDisqualificationType())) {
            disqualificationItemVo.setTypeList(Arrays.asList(disqualification.getDisqualificationType().split(",")));
        }
        //对象不为空,ID不为空
        DisqualificationFinalResult finalResult = finalResultService.getById(disqualificationItemVo.getId());
        //改为Copy
        BeanUtils.copyProperties(finalResult, disqualificationItemVo);
        //编号列表保存
        disqualificationItemVo.DisqualificationFinalResult(finalResult);
        //处理质控工程师列表
        disqualificationItemVo.setUserList(Arrays.asList(disqualificationItemVo.getQualityCheckBy().split(",")));
        //查询签核记录
        QueryWrapper<DisqualificationUserOpinion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("disqualification_id", disqualificationItemVo.getId());
        queryWrapper.orderByAsc("create_time");
        disqualificationItemVo.setUserOpinionsList(userOpinionService.list(queryWrapper));
        //查询文件
        disqualificationItemVo.setAttachmentList(attachmentService.queryAttachmentsByDisqualificationId(disqualificationItemVo.getId()));
        return disqualificationItemVo;
    }

    @Override
    public DisqualificationItemVo inquiryRequestForm(String tiId, String branchCode, String disqualificationId) {
        //无缘查询详情
        if (StrUtil.isBlank(tiId)) {
            DisqualificationItemVo disqualificationItemVo = new DisqualificationItemVo();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            disqualificationItemVo.setTrackItemId(uuid);
            disqualificationItemVo.setType(0);
            disqualificationItemVo.setSourceType(0);
            disqualificationItemVo.setRepairNoList(Collections.emptyList());
            disqualificationItemVo.setSalesReturnNoList(Collections.emptyList());
            disqualificationItemVo.setAcceptDeviationNoList(Collections.emptyList());
            disqualificationItemVo.setScrapNoList(Collections.emptyList());
            disqualificationItemVo.setBranchCode(branchCode);
            return disqualificationItemVo;
        }
        //有源头
        DisqualificationItemVo disqualificationItemVo = new DisqualificationItemVo();
        if (StrUtil.isNotBlank(disqualificationId)) {
            Disqualification disqualification = this.getById(disqualificationId);
            BeanUtils.copyProperties(disqualificationItemVo, disqualification);
        } else {
            disqualificationItemVo = trackItemService.queryItem(tiId, branchCode);
        }
        //对象不为空,ID不为空
        if (null != disqualificationItemVo && StrUtil.isNotBlank(disqualificationItemVo.getId())) {
            DisqualificationFinalResult finalResult = finalResultService.getById(disqualificationItemVo.getId());
            disqualificationItemVo.DisqualificationFinalResult(finalResult);
            //处理质控工程师列表
            disqualificationItemVo.setUserList(Arrays.asList(disqualificationItemVo.getQualityCheckBy().split(",")));
            //查询签核记录
            QueryWrapper<DisqualificationUserOpinion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("disqualification_id", disqualificationItemVo.getId());
            queryWrapper.orderByAsc("create_time");
            disqualificationItemVo.setUserOpinionsList(userOpinionService.list(queryWrapper));
            //查询文件
            disqualificationItemVo.setAttachmentList(attachmentService.queryAttachmentsByDisqualificationId(disqualificationItemVo.getId()));
        } else if (null != disqualificationItemVo) {
            disqualificationItemVo.setSourceType(1);
            disqualificationItemVo.setAttachmentList(Collections.emptyList());
            disqualificationItemVo.setUserOpinionsList(Collections.emptyList());
            disqualificationItemVo.setUserList(Collections.emptyList());
        }
        //2022/12/27  zhiqiang.lu  缺失预设值信息
        disqualificationItemVo.setTrackItemId(tiId);
        disqualificationItemVo.setBranchCode(branchCode);
        return disqualificationItemVo;
    }


    @Override
    public List<Map<String, String>> queryProductNoList(String trackHeadId) {
        QueryWrapper<TrackFlow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        List<TrackFlow> list = trackHeadFlowService.list(queryWrapper);
        List<Map<String, String>> flowList = new ArrayList<>();
        for (TrackFlow trackFlow : list) {
            Map<String, String> map = new HashMap<>(1);
            map.put("label", trackFlow.getProductNo());
            map.put("value", trackFlow.getProductNo());
            flowList.add(map);
        }
        return flowList;
    }

    @Override
    public Boolean rollBack(String id, Integer type) {
        Disqualification disqualification = this.getById(id);
        switch (type) {
            //传入是1 需要判断 是回滚状态2 还是回滚状态8
            case 1:
                if (disqualification.getType() == 2) {
                    disqualification.setType(1);
                } else if (disqualification.getType() == 8) {
                    disqualification.setType(7);
                } else {
                    throw new GlobalException("未能回滚,状态不允许回滚", ResultCode.FAILED);
                }
                break;
            case 2:
                if (disqualification.getType() == 3) {
                    disqualification.setType(2);
                } else {
                    throw new GlobalException("未能回滚,状态不允许回滚", ResultCode.FAILED);
                }
                break;
            case 3:
                //判断是状态3 还是状态4
                DisqualificationFinalResult finalResult = finalResultService.getById(disqualification.getId());
                String tenantId = SecurityUtils.getCurrentUser().getTenantId();
                //是处理单位1
                if (finalResult.getUnitTreatmentOne().equals(tenantId)) {
                    if (disqualification.getType() == 4) {
                        disqualification.setType(3);
                    }
                }
                //处理单位2
                if (finalResult.getUnitTreatmentTwo().equals(tenantId)) {
                    if (disqualification.getType() == 7) {
                        disqualification.setType(4);
                    }
                }
                break;
            default:
                //获取当前登录人姓名
                CommonResult<TenantUserVo> userAccount = systemServiceClient.queryByUserAccount(SecurityUtils.getCurrentUser().getUsername());
                saveRecord(id, "回滚到:" + UnitEnum.getMessage(disqualification.getType()), disqualification.getType(), userAccount.getData().getEmplName());
                break;

        }
        return this.updateById(disqualification);
    }

    @Override
    public Boolean rollBackAll(String id) {
        Disqualification disqualification = this.getById(id);
        if (1 == disqualification.getSourceType()) {
            disqualification.setType(1);
        } else {
            disqualification.setType(0);
        }
        return this.updateById(disqualification);
    }

    @Override
    public Boolean sendBack(String id, Integer type) {
        Disqualification disqualification = this.getById(id);
        switch (type) {
            case 2:
                if (disqualification.getType() == 2) {
                    //有源头
                    if (disqualification.getSourceType() == 1) {
                        disqualification.setType(1);
                    } else {
                        disqualification.setType(0);
                    }
                }
                break;
            case 3:
                //判断是状态3 还是状态4
                DisqualificationFinalResult finalResult = finalResultService.getById(disqualification.getId());
                String tenantId = SecurityUtils.getCurrentUser().getTenantId();
                //是处理单位1
                if (tenantId.equals(finalResult.getUnitTreatmentOne())) {
                    if (disqualification.getType() == 3) {
                        disqualification.setType(2);
                    }
                }
                //处理单位2
                if (tenantId.equals(finalResult.getUnitTreatmentTwo())) {
                    if (disqualification.getType() == 4) {
                        disqualification.setType(3);
                    }
                }
                break;
            default:
                //获取当前登录人姓名
                CommonResult<TenantUserVo> userAccount = systemServiceClient.queryByUserAccount(SecurityUtils.getCurrentUser().getUsername());
                saveRecord(id, "打回到:" + UnitEnum.getMessage(disqualification.getType()), disqualification.getType(), userAccount.getData().getEmplName());
                break;

        }
        return this.updateById(disqualification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(String disqualificationId) {
        //删除不合格品列表 表记录
        disqualificationMapper.deleteById(disqualificationId);
        //删除produce_disqualification_attachment表记录
        QueryWrapper<DisqualificationAttachment> disqualificationAttachmentQueryWrapper = new QueryWrapper<>();
        disqualificationAttachmentQueryWrapper.eq("disqualification_id", disqualificationId);
        List<DisqualificationAttachment> disqualificationAttachments = disqualificationAttachmentService.list(disqualificationAttachmentQueryWrapper);
        //获取关联file_id
        List<String> fileIds = disqualificationAttachments.stream().map(DisqualificationAttachment::getFileId).collect(Collectors.toList());
        disqualificationAttachmentService.remove(disqualificationAttachmentQueryWrapper);
        //删除produce_disqualification_final_result 表记录
        disqualificationFinalResultService.removeById(disqualificationId);
        //删除produce_disqualification_user_opinion 表记录
        QueryWrapper<DisqualificationUserOpinion> disqualificationUserOpinionQueryWrapper = new QueryWrapper<>();
        disqualificationUserOpinionQueryWrapper.eq("disqualification_id", disqualificationId);
        disqualificationUserOpinionService.remove(disqualificationUserOpinionQueryWrapper);
        //删除关联文件
        for (String id : fileIds) {
            systemServiceClient.delete(id);
        }
        return "success";
    }

    /**
     * 按逗号分隔的属性转换 不合格类型
     *
     * @param target
     * @param map
     * @return
     */
    private static String convertType(String target, Map<String, ItemParam> map) {
        String[] split = target.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : split) {
            if (map.containsKey(s)) {
                stringBuilder.append(map.get(s).getLabel()).append(",");
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        }
        return null;
    }

    @Override
    public DisqualificationItemVo queryLastTimeDataByCreateBy(String branchCode) {
        //有源头
        DisqualificationItemVo disqualificationItemVo = new DisqualificationItemVo();
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", SecurityUtils.getCurrentUser().getUsername());
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.orderByDesc("create_time");
        List<Disqualification> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        BeanUtils.copyProperties(list.get(0), disqualificationItemVo);
        DisqualificationFinalResult finalResult = finalResultService.getById(disqualificationItemVo.getId());
        disqualificationItemVo.DisqualificationFinalResult(finalResult);
        return disqualificationItemVo;
    }

    /**
     * 按逗号分隔的属性转换
     *
     * @param
     * @param
     * @return
     */
    private static String convertName(String target, Map<String, String> map) {
        String[] split = target.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : split) {
            if (map.containsKey(s)) {
                stringBuilder.append(map.get(s)).append(",");
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        }
        return null;
    }

    @Override
    public IPage<Disqualification> queryInspectorByCompany(QueryInspectorDto queryInspectorDto) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        getDisqualificationByQueryInspectorDto(queryWrapper, queryInspectorDto);
        //只查询本租户创建的不合格品申请单
        if (!tenantId.equals(TENANT_ID)) {
            queryWrapper.and(wrapper -> wrapper.eq("unit_treatment_one", tenantId).or().eq("unit_responsibility_within", tenantId).or().eq("unit_treatment_two", tenantId).or().eq("dis.tenant_id", tenantId));
        }
        // 不合格品信息
        return disqualificationMapper.query(new Page<>(queryInspectorDto.getPage(), queryInspectorDto.getLimit()), queryWrapper);
    }

    @Override
    public void exportExcel(HttpServletResponse rsp, QueryInspectorDto queryInspectorDto) {
        try {
            String tenantId = SecurityUtils.getCurrentUser().getTenantId();
            QueryWrapper<DisqualificationResultVo> queryWrapper = new QueryWrapper<>();
            getDisqualificationByQueryInspectorDto(queryWrapper, queryInspectorDto);
            //只查询本租户创建的不合格品申请单
            if (!tenantId.equals(TENANT_ID)) {
                queryWrapper.and(wrapper -> wrapper.eq("unit_treatment_one", tenantId).or().eq("unit_responsibility_within", tenantId).or().eq("unit_treatment_two", tenantId).or().eq("dis.tenant_id", tenantId));
            }
            List<DisqualificationResultVo> list = disqualificationMapper.queryDisqualificationResult(queryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                // 获取不合格类型
                Map<String, ItemParam> typeMap = systemServiceClient.findItemParamByCode(UNQUALIFIED_TYPE, TENANT_ID).getData().stream().collect(Collectors.toMap(ItemParam::getCode, x -> x, (value1, value2) -> value2));
                // 获取所有的租户列表信息(包括内置)
                Map<String, Tenant> tenantMap = systemServiceClient.queryTenantAllList().getData().stream().collect(Collectors.toMap(Tenant::getId, x -> x, (value1, value2) -> value2));
                // 获取不合格外协
                Map<String, ItemParam> unitMap = systemServiceClient.findItemParamByCode(UNIT_CODE, TENANT_ID).getData().stream().collect(Collectors.toMap(ItemParam::getCode, x -> x, (value1, value2) -> value2));
                // 获取不合格常用工序
                Map<String, ItemParam> processMap = systemServiceClient.findItemParamByCode(PROCESS_CODE, TENANT_ID).getData().stream().collect(Collectors.toMap(ItemParam::getCode, x -> x, (value1, value2) -> value2));
                // 获取用户和姓名键值对
                Map<String, String> usersAccountMap = systemServiceClient.usersAccount().getData();
                for (DisqualificationResultVo disqualificationResultVo : list) {
                    if (StringUtils.isNotEmpty(disqualificationResultVo.getDisqualificationType())) {
                        // 不合格类型
                        disqualificationResultVo.setDisqualificationType(convertType(disqualificationResultVo.getDisqualificationType(), typeMap));
                    }
                    if (StringUtils.isNotEmpty(disqualificationResultVo.getUnitResponsibilityWithin())) {
                        // 责任单位内
                        disqualificationResultVo.setUnitResponsibilityWithin(tenantMap.get(disqualificationResultVo.getUnitResponsibilityWithin()).getTenantName());
                    }
                    if (StringUtils.isNotEmpty(disqualificationResultVo.getUnitTreatmentOne())) {
                        // 处理单位1
                        disqualificationResultVo.setUnitTreatmentOne(tenantMap.get(disqualificationResultVo.getUnitTreatmentOne()).getTenantName());
                    }
                    if (StringUtils.isNotEmpty(disqualificationResultVo.getUnitTreatmentTwo())) {
                        // 处理单位2
                        disqualificationResultVo.setUnitTreatmentTwo(tenantMap.get(disqualificationResultVo.getUnitTreatmentTwo()).getTenantName());
                    }
                    if (StringUtils.isNotEmpty(disqualificationResultVo.getDiscoverTenant())) {
                        // 发现单位
                        disqualificationResultVo.setDiscoverTenant(tenantMap.get(disqualificationResultVo.getDiscoverTenant()).getTenantName());
                    }
                    if (StringUtils.isNotEmpty(disqualificationResultVo.getUnitResponsibilityOutside())) {
                        // 责任单位(外)
                        ItemParam itemParam = unitMap.get(disqualificationResultVo.getUnitResponsibilityOutside());
                        if (null != itemParam) {
                            disqualificationResultVo.setUnitResponsibilityOutside(itemParam.getLabel());
                        } else {
                            disqualificationResultVo.setUnitResponsibilityOutside(disqualificationResultVo.getUnitResponsibilityOutside());
                        }
                    }
                    if (StringUtils.isNotEmpty(disqualificationResultVo.getDiscoverItem())) {
                        // 发现工序
                        if (ObjectUtils.isNotEmpty(processMap.get(disqualificationResultVo.getDiscoverItem()))) {
                            disqualificationResultVo.setDiscoverItem(processMap.get(disqualificationResultVo.getDiscoverItem()).getLabel());
                        }
                    }
                    if (StringUtils.isNotEmpty(disqualificationResultVo.getQualityCheckBy())) {
                        // 质控工程师
                        disqualificationResultVo.setQualityCheckBy(convertName(disqualificationResultVo.getQualityCheckBy(), usersAccountMap));
                    }
                    if (StringUtils.isNotEmpty(disqualificationResultVo.getTrackHeadType())) {
                        // 跟单类型
                        disqualificationResultVo.setTrackHeadType(TrackTypeEnum.getMessage(disqualificationResultVo.getTrackHeadType()));
                    }
                }
                // 读文件
                ClassPathResource classPathResource = new ClassPathResource("excel/" + "disqualificationTemplate.xlsx");
                ExcelWriter writer = null;
                writer = ExcelUtil.getReader(classPathResource.getInputStream()).getWriter();
                writer.writeCellValue("A1", new StringBuilder().append("共搜索到").append(list.size()).append("条符合条件的信息"));
                writer.resetRow();
                writer.passRows(5);
                int currentRow = writer.getCurrentRow();
                // 依次写入Excel
                for (DisqualificationResultVo disqualification : list) {
                    writer.writeCellValue(0, currentRow, disqualification.getDisqualificationName());
                    writer.writeCellValue(1, currentRow, TypeEnum.getMessage(disqualification.getType()));
                    writer.writeCellValue(2, currentRow, disqualification.getCreateTime());
                    writer.writeCellValue(3, currentRow, disqualification.getBranchCode());
                    writer.writeCellValue(4, currentRow, disqualification.getProcessSheetNo());
                    writer.writeCellValue(5, currentRow, disqualification.getTrackNo());
                    writer.writeCellValue(6, currentRow, disqualification.getDisqualificationType());
                    writer.writeCellValue(7, currentRow, disqualification.getDiscoverTenant());
                    writer.writeCellValue(8, currentRow, disqualification.getUnitResponsibilityWithin());
                    writer.writeCellValue(9, currentRow, disqualification.getUnitResponsibilityOutside());
                    writer.writeCellValue(10, currentRow, disqualification.getWorkNo());
                    writer.writeCellValue(11, currentRow, disqualification.getProductName());
                    writer.writeCellValue(12, currentRow, disqualification.getPartName());
                    writer.writeCellValue(13, currentRow, disqualification.getPartDrawingNo());
                    writer.writeCellValue(14, currentRow, disqualification.getProductNo());
                    writer.writeCellValue(15, currentRow, disqualification.getTrackHeadType());
                    writer.writeCellValue(16, currentRow, disqualification.getPartMaterials());
                    writer.writeCellValue(17, currentRow, disqualification.getHeatNumber());
                    writer.writeCellValue(18, currentRow, disqualification.getNumber());
                    writer.writeCellValue(19, currentRow, disqualification.getTotalWeight());
                    writer.writeCellValue(20, currentRow, disqualification.getDisqualificationCondition());
                    writer.writeCellValue(21, currentRow, disqualification.getQualityControlOpinion());
                    writer.writeCellValue(22, currentRow, disqualification.getUnitTreatmentOneOpinion());
                    writer.writeCellValue(23, currentRow, disqualification.getUnitTreatmentTwoOpinion());
                    writer.writeCellValue(24, currentRow, disqualification.getResponsibilityOpinion());
                    writer.writeCellValue(25, currentRow, disqualification.getQualityCheckBy());
                    writer.writeCellValue(26, currentRow, disqualification.getUnitTreatmentOne());
                    writer.writeCellValue(27, currentRow, disqualification.getUnitTreatmentTwo());
                    writer.writeCellValue(28, currentRow, disqualification.getDiscoverItem());
                    writer.writeCellValue(29, currentRow, disqualification.getDiscardTime());
                    writer.writeCellValue(30, currentRow, disqualification.getReuseTime());
                    writer.writeCellValue(31, currentRow, disqualification.getAcceptDeviation());
                    writer.writeCellValue(32, currentRow, disqualification.getRepairQualified());
                    writer.writeCellValue(33, currentRow, disqualification.getScrap());
                    writer.writeCellValue(34, currentRow, disqualification.getSalesReturn());
                    writer.writeCellValue(35, currentRow, disqualification.getSalesReturnLoss());
                    writer.writeCellValue(36, currentRow, disqualification.getCloseTime());
                    writer.writeCellValue(37, currentRow, disqualification.getTreatmentOneName());
                    writer.writeCellValue(38, currentRow, disqualification.getTreatmentTwoName());
                    writer.writeCellValue(39, currentRow, disqualification.getResponsibilityName());
                    writer.writeCellValue(40, currentRow, disqualification.getTechnologyName());
                    currentRow++;
                }
                rsp.setContentType("application/octet-stream");
                rsp.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("不合格品处理单查询结果.xlsx", "UTF-8"));
                ServletOutputStream outputStream = rsp.getOutputStream();
                writer.flush(outputStream, true);
                IoUtil.close(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRecord(String id, String record, Integer type, String name) {
        QueryWrapper<DisqualificationUserOpinion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("disqualification_id", id);
        //获取当前登录租户名称
        CommonResult<Tenant> tenant = systemServiceClient.getTenantById(SecurityUtils.getCurrentUser().getTenantId());
        DisqualificationUserOpinion userOpinion = new DisqualificationUserOpinion();
        userOpinion.setDisqualificationId(id)
                .setSort(userOpinionService.count(queryWrapper) + 1)
                .setType(type)
                .setName(name)
                .setTenantName(tenant.getData().getTenantName())
                .setTenantId(tenant.getData().getId())
                .setOpinion(record);
        userOpinionService.save(userOpinion);
    }

    /**
     * 功能描述: 获取字典
     *
     * @param code
     * @Author: xinYu.hou
     * @Date: 2022/10/14 15:16
     * @return: String
     **/
    private String value(String code) {
        try {
            CommonResult<List<ItemParam>> result = systemServiceClient.selectItemClass(code, null);
            ItemParam itemParam = result.getData().get(0);
            return itemParam.getCode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("获取检测部门错误", ResultCode.FAILED);
        }
    }

}
