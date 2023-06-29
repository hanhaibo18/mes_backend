package com.richfit.mes.produce.service.notice;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.SkillNotice;
import com.richfit.mes.common.model.produce.SkillNoticeTenant;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.model.util.TimeUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.SkillNoticeMapper;
import com.richfit.mes.produce.entity.AcceptDispatchDto;
import com.richfit.mes.produce.entity.DispatchDto;
import com.richfit.mes.produce.entity.SkillIssueNoticeDto;
import com.richfit.mes.produce.entity.SkillNoticeDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SkillNoticeServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 技术通知实现类
 * @CreateTime: 2023年06月02日 15:48:00
 */
@Service
public class SkillNoticeServiceImpl extends ServiceImpl<SkillNoticeMapper, SkillNotice> implements SkillNoticeService {

    @Resource
    private SkillNoticeMapper skillNoticeMapper;

    @Resource
    private SkillNoticeTenantService noticeTenantService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Override
    public IPage<SkillNotice> querySkillPage(SkillNoticeDto skillNoticeDto) {
        QueryWrapper<SkillNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(skillNoticeDto.getSkillNoticeNumber()), "skill_notice_number", skillNoticeDto.getSkillNoticeNumber());
        queryWrapper.eq(StrUtil.isNotBlank(skillNoticeDto.getWorkNo()), "work_no", skillNoticeDto.getWorkNo());
        queryWrapper.eq(StrUtil.isNotBlank(skillNoticeDto.getNotificationState()), "notification_state", skillNoticeDto.getNotificationState());
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        TimeUtil.queryStartTime(queryWrapper, skillNoticeDto.getStateTime());
        TimeUtil.queryEndTime(queryWrapper, skillNoticeDto.getEndTime());
        OrderUtil.query(queryWrapper, skillNoticeDto.getOrder(), skillNoticeDto.getOrderCol());
        return this.page(new Page<>(skillNoticeDto.getPage(), skillNoticeDto.getSize()), queryWrapper);
    }

    @Override
    public Boolean acceptanceOfNotice(List<String> idList) {
        UpdateWrapper<SkillNotice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idList);
        updateWrapper.set("notification_state", "1");
        return this.update(updateWrapper);
    }

    @Override
    public Boolean dispatchNotification(List<String> idList) {
        UpdateWrapper<SkillNotice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idList);
        updateWrapper.set("dispatch_state", "1");
        return this.update(updateWrapper);
    }

    @Override
    public IPage<SkillNotice> queryDispatchPage(DispatchDto dispatchDto) {
        QueryWrapper<SkillNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("notification_state", "1");
        queryWrapper.eq(StrUtil.isNotBlank(dispatchDto.getSkillNoticeNumber()), "dispatch_notice_number", dispatchDto.getSkillNoticeNumber());
        queryWrapper.eq(StrUtil.isNotBlank(dispatchDto.getWorkNo()), "work_no", dispatchDto.getWorkNo());
        queryWrapper.eq(StrUtil.isNotBlank(dispatchDto.getDrillingRigName()), "drilling_rig_name", dispatchDto.getDrillingRigName());
        queryWrapper.eq(StrUtil.isNotBlank(dispatchDto.getDispatchState()), "dispatch_state", dispatchDto.getDispatchState());
        queryWrapper.eq(StrUtil.isNotBlank(dispatchDto.getUnit()), "unit", dispatchDto.getUnit());
        queryWrapper.eq(StrUtil.isNotBlank(dispatchDto.getDrawingNo()), "drawingNo", dispatchDto.getDrawingNo());
        TimeUtil.queryStartTime(queryWrapper, dispatchDto.getStateTime(), "issue_time");
        TimeUtil.queryEndTime(queryWrapper, dispatchDto.getEndTime(), "issue_time");
        OrderUtil.query(queryWrapper, dispatchDto.getOrder(), dispatchDto.getOrderCol());
        queryWrapper.groupBy("id");
        IPage<SkillNotice> acceptingPage = skillNoticeMapper.queryDispatchPage(new Page<>(dispatchDto.getPage(), dispatchDto.getSize()), queryWrapper);
        if (CollectionUtils.isEmpty(acceptingPage.getRecords())) {
            return null;
        }
        unitData(acceptingPage.getRecords());
        return acceptingPage;
    }

    @Override
    public Boolean updateDispatch(SkillNotice skillNotice) {
        return this.updateById(skillNotice);
    }

    @Override
    public Boolean dispatchNoticeDelivery(SkillIssueNoticeDto issueNoticeDto) {
        if (CollectionUtils.isEmpty(issueNoticeDto.getExecutableUnitList())) {
            throw new GlobalException("请选择执行单位", ResultCode.FAILED);
        }
        UpdateWrapper<SkillNotice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", issueNoticeDto.getIdList());
        updateWrapper.set("dispatch_state", "2");
        this.update(updateWrapper);
        List<SkillNoticeTenant> list = new ArrayList<>();
        for (String id : issueNoticeDto.getIdList()) {
            for (String unit : issueNoticeDto.getExecutableUnitList()) {
                SkillNoticeTenant noticeTenant = new SkillNoticeTenant();
                noticeTenant.setUnit(unit);
                noticeTenant.setSkillId(id);
                list.add(noticeTenant);
            }
        }
        return noticeTenantService.saveBatch(list);
    }

    @Override
    public IPage<SkillNotice> receiveDispatchNotification(AcceptDispatchDto acceptDispatchDto) {
        QueryWrapper<SkillNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("n.id = t.skill_id ");
        queryWrapper.eq("dispatch_state", "2");
        queryWrapper.eq(StrUtil.isNotBlank(acceptDispatchDto.getSkillNoticeNumber()), "dispatch_notice_number", acceptDispatchDto.getSkillNoticeNumber());
        queryWrapper.eq(StrUtil.isNotBlank(acceptDispatchDto.getWorkNo()), "work_no", acceptDispatchDto.getWorkNo());
        queryWrapper.eq(StrUtil.isNotBlank(acceptDispatchDto.getDrillingRigName()), "drilling_rig_name", acceptDispatchDto.getDrillingRigName());
        queryWrapper.eq(StrUtil.isNotBlank(acceptDispatchDto.getAcceptingState()), "accepting_state", acceptDispatchDto.getAcceptingState());
        queryWrapper.eq(StrUtil.isNotBlank(acceptDispatchDto.getDrawingNo()), "drawingNo", acceptDispatchDto.getDrawingNo());
        queryWrapper.eq("unit", SecurityUtils.getCurrentUser().getTenantId());
        TimeUtil.queryStartTime(queryWrapper, acceptDispatchDto.getStateTime(), "issue_time");
        TimeUtil.queryEndTime(queryWrapper, acceptDispatchDto.getEndTime(), "issue_time");
        OrderUtil.query(queryWrapper, acceptDispatchDto.getOrder(), acceptDispatchDto.getOrderCol());
        queryWrapper.groupBy("id");
        IPage<SkillNotice> acceptingPage = skillNoticeMapper.queryAcceptingPage(new Page<>(acceptDispatchDto.getPage(), acceptDispatchDto.getSize()), queryWrapper);
        if (CollectionUtils.isEmpty(acceptingPage.getRecords())) {
            return null;
        }
        unitData(acceptingPage.getRecords());
        return acceptingPage;
    }

    @Override
    public Boolean receiveDispatchNotificationAffirm(List<String> idList) {
        UpdateWrapper<SkillNotice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idList);
        updateWrapper.set("accepting_state", 1);
        return this.update(updateWrapper);
    }

    private void unitData(List<SkillNotice> noticeList) {
        //获取所有排产单 车间数据
        List<String> idList = noticeList.stream().map(SkillNotice::getId).collect(Collectors.toList());
        QueryWrapper<SkillNoticeTenant> tenantQueryWrapper = new QueryWrapper<>();
        tenantQueryWrapper.in("skill_id", idList);
        List<SkillNoticeTenant> tenantList = noticeTenantService.list(tenantQueryWrapper);
        //根据排产单分组
        Map<String, List<SkillNoticeTenant>> collect = tenantList.stream().collect(Collectors.groupingBy(SkillNoticeTenant::getSkillId));
        //获取所有租户
        CommonResult<List<Tenant>> tenantAllList = systemServiceClient.queryTenantAllList();
        Map<String, Tenant> tenantMap = tenantAllList.getData().stream().collect(Collectors.toMap(Tenant::getId, v -> v));
        //处理所有排产单租户数据转换为租户名称
        for (SkillNoticeTenant tenantTenant : tenantList) {
            tenantTenant.setUnit(tenantMap.get(tenantTenant.getUnit()).getTenantName());
        }
        //循环所有排产单数据 获取对应的车间信息
        for (SkillNotice notice : noticeList) {
            if (CollectionUtils.isNotEmpty(collect.get(notice.getId()))) {
                //获取执行单位数据
                String executableUnit = collect.get(notice.getId()).stream().map(SkillNoticeTenant::getUnit).collect(Collectors.joining(","));
                notice.setExecutableUnit(executableUnit);
            }
        }
    }
}
