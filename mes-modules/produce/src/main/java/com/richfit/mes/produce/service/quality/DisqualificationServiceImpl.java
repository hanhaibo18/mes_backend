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
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.quality.DisqualificationMapper;
import com.richfit.mes.produce.dao.quality.DisqualificationUserOpinionMapper;
import com.richfit.mes.produce.entity.quality.*;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.TrackHeadFlowService;
import com.richfit.mes.produce.service.TrackItemService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
        queryWrapper.eq(StrUtil.isNotBlank(queryCheckDto.getTenantId()), "tenant_id", queryCheckDto.getTenantId());
        queryWrapper.eq("type", 2);
        queryWrapper.like("quality_check_by", SecurityUtils.getCurrentUser().getUsername() + ",");
        return this.page(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }


    @Override
    public IPage<Disqualification> queryDealWith(QueryCheckDto queryCheckDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        //图号查询
        if (StrUtil.isNotBlank(queryCheckDto.getDrawingNo())) {
            queryWrapper.like("dis.drawing_no", queryCheckDto.getDrawingNo());
        }
        //产品名称
        if (StrUtil.isNotBlank(queryCheckDto.getProductName())) {
            queryWrapper.like("dis.product_name", queryCheckDto.getProductName());
        }
        //跟单号
        if (StrUtil.isNotBlank(queryCheckDto.getTrackNo())) {
            queryCheckDto.setTrackNo(queryCheckDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(dis.track_no, char(13), ''), char(10), ''),' ', '') like '%" + queryCheckDto.getTrackNo() + "%'");
        }
        //申请单号
        if (StrUtil.isNotBlank(queryCheckDto.getProcessSheetNo())) {
            queryWrapper.like("dis.process_sheet_no", queryCheckDto.getProcessSheetNo());
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
        queryWrapper.and(wrapper -> wrapper.eq("final.unit_treatment_one", SecurityUtils.getCurrentUser().getTenantId()).or().eq("final.unit_treatment_two", SecurityUtils.getCurrentUser().getTenantId()));
        queryWrapper.and(wrapper -> wrapper.eq("dis.type", 3).or().eq("dis.type", 4));
        return disqualificationMapper.query(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }

    @Override
    public IPage<Disqualification> queryResponsibility(QueryCheckDto queryCheckDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        disqualificationQueryWrapper(queryWrapper, queryCheckDto);
        queryWrapper.eq("type", 5);
        return this.page(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }

    @Override
    public IPage<Disqualification> queryTechnology(QueryCheckDto queryCheckDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        disqualificationQueryWrapper(queryWrapper, queryCheckDto);
        queryWrapper.eq("type", 6);
        return this.page(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }

    private void disqualificationQueryWrapper(QueryWrapper<Disqualification> queryWrapper, QueryCheckDto queryCheckDto) {
        //图号查询
        if (StrUtil.isNotBlank(queryCheckDto.getDrawingNo())) {
            queryWrapper.like("drawing_no", queryCheckDto.getDrawingNo());
        }
        //产品名称
        if (StrUtil.isNotBlank(queryCheckDto.getProductName())) {
            queryWrapper.like("product_name", queryCheckDto.getProductName());
        }
        //跟单号
        if (StrUtil.isNotBlank(queryCheckDto.getTrackNo())) {
            queryCheckDto.setTrackNo(queryCheckDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + queryCheckDto.getTrackNo() + "%'");
        }
        //申请单号
        if (StrUtil.isNotBlank(queryCheckDto.getProcessSheetNo())) {
            queryWrapper.like("process_sheet_no", queryCheckDto.getProcessSheetNo());
        }
        //根据公司区分申请单
        try {
            //开始时间
            if (StrUtil.isNotBlank(queryCheckDto.getStartTime())) {
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + queryCheckDto.getStartTime() + " 00:00:00')");
            }
            //结束时间
            if (StrUtil.isNotBlank(queryCheckDto.getEndTime())) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(queryCheckDto.getEndTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
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
        finalResultService.saveOrUpdate(finalResult);
        //不合格意见
        //判断申请单状态是1 意见列表为空
//        if (1 == processJudge && CollUtil.isEmpty(disqualificationDto.getDisqualifications())) {
//            //为空处理不合格意见为列表
//            DisqualificationUserOpinion opinion = new DisqualificationUserOpinion();
//            opinion.setDisqualificationId(disqualification.getId());
//            opinion.setType(processJudge);
//            //查询人员姓名
//            TenantUserVo user = systemServiceClient.getUserById(SecurityUtils.getCurrentUser().getUserId()).getData();
//            opinion.setUserName(user.getEmplName());
//        }
//        userOpinionService.saveOrUpdateBatch(disqualificationDto.getDisqualifications());
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
                return 3;
            }
            //技术裁决结束返回处理单位1
            if (6 == disqualificationDto.getType()) {
                return 3;
            }
        }
        //不发布直接返回当前状态
        return disqualificationDto.getType();
    }

    /**
     * 功能描述: 保存派工人员接口
     *
     * @param userList
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/10/14 17:04
     * @return: void
     **/
    private void savePerson(List<TenantUserVo> userList, String id) {
        try {
            List<DisqualificationUserOpinion> userOpinions = new ArrayList<>();
            for (TenantUserVo user : userList) {
                DisqualificationUserOpinion opinion = new DisqualificationUserOpinion();
                opinion.setDisqualificationId(id);
                //赋值用户唯一Id
                opinion.setUserId(user.getId());
                //赋值用户姓名
                opinion.setUserName(user.getEmplName());
                //赋值用户车间
                opinion.setUserBranch(user.getBelongOrgId());
                //查询车间姓名并赋值
                Branch branch = baseServiceClient.queryTenantIdByBranchCode(user.getBelongOrgId());
                opinion.setUserBranchName(branch.getBranchName());
                userOpinions.add(opinion);
            }
            userOpinionService.saveBatch(userOpinions);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("保存人员失败!", ResultCode.FAILED);
        }
    }

    @Override
    public Boolean updateIsIssue(String id, String state) {
        UpdateWrapper<Disqualification> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        //开单状态增加开单时间
        if ("1".equals(state)) {
            updateWrapper.set("order_time", new Date());
        }
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
    public List<SignedRecordsVo> querySignedRecordsList(String disqualificationId) {
        //查询意见表
        QueryWrapper<DisqualificationUserOpinion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("disqualification_id", disqualificationId);
        List<SignedRecordsVo> recordsVoList = SignedRecordsVo.list(userOpinionService.list(queryWrapper));
        recordsVoList.forEach(records -> {
            QueryWrapper<DisqualificationFinalResult> queryWrapperFinalResult = new QueryWrapper<>();
//            queryWrapperFinalResult.eq("opinion_id", records.getId());
            DisqualificationFinalResult finalResult = finalResultService.getOne(queryWrapperFinalResult);
            if (null == finalResult) {
                return;
            }
        });
        //单查询开单时间
        Disqualification disqualification = this.getById(disqualificationId);
        SignedRecordsVo signedRecordsVo = new SignedRecordsVo();
        signedRecordsVo.setHandlingTime(disqualification.getOrderTime());
        //查询姓名
        CommonResult<TenantUserVo> userAccount = systemServiceClient.queryByUserAccount(disqualification.getCreateBy());
        signedRecordsVo.setUserName(userAccount.getData().getEmplName());
        //查询车间名称
        CommonResult<Branch> branch = baseServiceClient.selectBranchByCodeAndTenantId(disqualification.getBranchCode(), disqualification.getTenantId());
        signedRecordsVo.setBranchCodeName(branch.getData().getBranchName());
        signedRecordsVo.setOpinion("提报不合格品申请单");
        recordsVoList.add(0, signedRecordsVo);
        return recordsVoList;
    }

    @Override
    public DisqualificationItemVo inquiryRequestForm(String tiId, String branchCode, String disqualificationId) {
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
            //查询流水记录
            //查询文件
            disqualificationItemVo.setAttachmentList(attachmentService.queryAttachmentsByDisqualificationId(disqualificationItemVo.getId()));
        } else if (null != disqualificationItemVo) {
            disqualificationItemVo.setAttachmentList(Collections.emptyList());
            disqualificationItemVo.setSignedRecordsList(Collections.emptyList());
            disqualificationItemVo.setUserList(Collections.emptyList());
        }
        //2022/12/27  zhiqiang.lu  缺失预设值信息
        disqualificationItemVo.setTrackItemId(tiId);
        return disqualificationItemVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitOpinions(SaveOpinionDto saveOpinionDto) {
        savePerson(saveOpinionDto.getUserList(), saveOpinionDto.getId());
        UpdateWrapper<DisqualificationUserOpinion> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", saveOpinionDto.getOpinionId());
        updateWrapper.set("opinion", saveOpinionDto.getOpinion());
        updateWrapper.set("type", saveOpinionDto.getType());
        updateWrapper.set("modify_time", new Date());
        return userOpinionService.update(updateWrapper);
    }

    @Override
    public Boolean saveFinalResult(DisqualificationFinalResult disqualificationFinalResult) {
        return finalResultService.save(disqualificationFinalResult);
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


    private List<TenantUserVo> queryOpinionUser(String disqualificationId) {
        QueryWrapper<DisqualificationUserOpinion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("disqualification_id", disqualificationId);
        //查询人员并拼接返回数据
        List<DisqualificationUserOpinion> opinions = userOpinionService.list(queryWrapper);
        return opinions.stream().map(user -> {
            TenantUserVo tenantUserVo = new TenantUserVo();
            tenantUserVo.setId(user.getUserId());
            tenantUserVo.setEmplName(user.getUserName());
            tenantUserVo.setBelongOrgId(user.getUserBranch());
            return tenantUserVo;
        }).collect(Collectors.toList());
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
