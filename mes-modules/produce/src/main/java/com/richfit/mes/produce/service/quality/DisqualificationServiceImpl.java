package com.richfit.mes.produce.service.quality;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.quality.DisqualificationMapper;
import com.richfit.mes.produce.dao.quality.DisqualificationUserOpinionMapper;
import com.richfit.mes.produce.enmus.UnitEnum;
import com.richfit.mes.produce.entity.quality.DisqualificationDto;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;
import com.richfit.mes.produce.entity.quality.QueryCheckDto;
import com.richfit.mes.produce.entity.quality.QueryInspectorDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.CodeRuleService;
import com.richfit.mes.produce.service.TrackHeadFlowService;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.utils.Code;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private DisqualificationUserOpinionMapper userOpinionMapper;

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
    private DisqualificationMapper disqualificationMapper;

    @Resource
    private CodeRuleService codeRuleService;

    @Override
    public IPage<Disqualification> queryInspector(QueryInspectorDto queryInspectorDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
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
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + queryInspectorDto.getStartTime() + " 00:00:00')");
            }
            //结束时间
            if (StrUtil.isNotBlank(queryInspectorDto.getEndTime())) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(queryInspectorDto.getEndTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
            }
        } catch (Exception e) {
            throw new GlobalException("时间格式处理错误", ResultCode.FAILED);
        }
        //只查询本人创建的不合格品申请单
        queryWrapper.eq("create_by", SecurityUtils.getCurrentUser().getUsername());
        return this.page(new Page<>(queryInspectorDto.getPage(), queryInspectorDto.getLimit()), queryWrapper);
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
        return disqualificationMapper.query(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }

    private void disqualificationQueryWrapper(QueryWrapper<Disqualification> queryWrapper, QueryCheckDto queryCheckDto) {
        //图号查询
        queryWrapper.like(StrUtil.isNotBlank(queryCheckDto.getDrawingNo()), "dis.drawing_no", queryCheckDto.getDrawingNo());
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
        this.saveOrUpdate(disqualification);
        //处理不合格从表数据
        DisqualificationFinalResult finalResult = new DisqualificationFinalResult();
        BeanUtils.copyProperties(disqualificationDto, finalResult);
        finalResult.setId(disqualification.getId());
        //让步接收产品编号
        finalResult.setAcceptDeviationNo(String.join(",", disqualificationDto.getAcceptDeviationNoList()));
        //返修后产品编号
        finalResult.setRepairNo(String.join(",", disqualificationDto.getRepairNoList()));
        //报废后产品编号
        finalResult.setScrapNo(String.join(",", disqualificationDto.getScrapNoList()));
        //退货产品编号
        finalResult.setSalesReturnNo(String.join(",", disqualificationDto.getSalesReturnNoList()));
        //处理意见数据
        TenantUserVo user = systemServiceClient.getUserById(SecurityUtils.getCurrentUser().getUserId()).getData();
        switch (disqualificationDto.getType()) {
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
                return 3;
            }
            //技术裁决结束返回处理单位1
            if (6 == disqualificationDto.getType()) {
                //判断是否有处理单位2 有处理单位2状态返回到4
                if (StrUtil.isNotBlank(disqualificationDto.getUnitTreatmentTwo())) {
                    return 4;
                }
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
        //申请人提交
        if (1 == type) {
            saveRecord(id, UnitEnum.getMessage(type) + "提交。不合格情况:" + disqualificationDto.getDisqualificationCondition(), type, finalResult.getDisqualificationName());
        }
        //质控提交
        if (2 == type) {
            saveRecord(id, UnitEnum.getMessage(type) + "提交。意见:" + disqualificationDto.getQualityControlOpinion(), type, finalResult.getQualityName());
        }
        //处理单位一提交
        if (3 == type) {
            saveRecord(id, UnitEnum.getMessage(type) + "提交。意见:" + disqualificationDto.getUnitTreatmentOneOpinion(), type, finalResult.getTreatmentOneName());
        }
        //处理单位二提交
        if (4 == type) {
            saveRecord(id, UnitEnum.getMessage(type) + "提交。意见:" + disqualificationDto.getUnitTreatmentTwoOpinion(), type, finalResult.getTreatmentTwoName());
        }
        //责任裁决
        if (5 == type) {
            saveRecord(id, UnitEnum.getMessage(type) + "提交。责任裁决:" + disqualificationDto.getResponsibilityOpinion(), type, finalResult.getResponsibilityName());
        }
        //技术裁决
        if (6 == type) {
            saveRecord(id, UnitEnum.getMessage(type) + "提交。技术裁决:" + disqualificationDto.getTechnologyOpinion(), type, finalResult.getTechnologyName());
        }
        //申请人最后一步填写意见
        if (7 == type) {
            saveRecord(id, UnitEnum.getMessage(1) + "提交。返修情况:" + disqualificationDto.getQualityControlOpinion(), type, finalResult.getDisqualificationName());
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
            return systemServiceClient.queryUserByTenantId(branch.getTenantId());
        } else {
            return null;
        }
    }

    @Override
    public DisqualificationItemVo inquiryRequestForm(String tiId, String branchCode, String disqualificationId) {
        //无缘查询详情
        if (StrUtil.isBlank(tiId)) {
            DisqualificationItemVo disqualificationItemVo = new DisqualificationItemVo();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            disqualificationItemVo.setTrackItemId(uuid);
            //获取申请单编号
            try {
                String disqualificationNo = Code.value("disqualification_no", SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
                disqualificationItemVo.setProcessSheetNo(disqualificationNo);
                Code.update("disqualification_no", disqualificationNo, SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
                throw new GlobalException("获取申请单编号错误", ResultCode.FAILED);
            }
            disqualificationItemVo.setType("0");
            disqualificationItemVo.setSourceType(0);
            disqualificationItemVo.setRepairNoList(Collections.emptyList());
            disqualificationItemVo.setSalesReturnNoList(Collections.emptyList());
            disqualificationItemVo.setAcceptDeviationNoList(Collections.emptyList());
            disqualificationItemVo.setScrapNoList(Collections.emptyList());
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
            //传入是1 需要判断 是回滚状态2 还是回滚状态8
            case 1:
                if (disqualification.getType() == 2) {
                    disqualification.setType(1);
                } else if (disqualification.getType() == 8) {
                    disqualification.setType(7);
                }
                break;
            case 2:
                if (disqualification.getType() == 3) {
                    disqualification.setType(2);
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
                saveRecord(id, "打回到:" + UnitEnum.getMessage(disqualification.getType()), disqualification.getType(), userAccount.getData().getEmplName());
                break;

        }
        return this.updateById(disqualification);
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
