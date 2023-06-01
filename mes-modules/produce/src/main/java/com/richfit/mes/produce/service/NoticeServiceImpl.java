package com.richfit.mes.produce.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.Notice;
import com.richfit.mes.common.model.produce.NoticeTenant;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.model.util.TimeUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.NoticeMapper;
import com.richfit.mes.produce.entity.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: NoticeServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 通知实体
 * @CreateTime: 2023年05月29日 18:24:00
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Resource
    private NoticeTenantService noticeTenantService;

    @Resource
    private NoticeMapper noticeMapper;

    @Override
    public IPage<Notice> queryPage(SalesSchedulingDto salesSchedulingDto) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(salesSchedulingDto.getProductionOrder()), "production_order", salesSchedulingDto.getProductionOrder());
        queryWrapper.eq(StrUtil.isNotBlank(salesSchedulingDto.getWorkNo()), "work_no", salesSchedulingDto.getWorkNo());
        queryWrapper.like(StrUtil.isNotBlank(salesSchedulingDto.getProduceName()), "produce_name", salesSchedulingDto.getProduceName());
        queryWrapper.eq(StrUtil.isNotBlank(salesSchedulingDto.getUserUnit()), "user_unit", salesSchedulingDto.getUserUnit());
        queryWrapper.eq(StrUtil.isNotBlank(salesSchedulingDto.getIssuingUnit()), "issuing_unit", salesSchedulingDto.getIssuingUnit());
        TimeUtil.queryStartTime(queryWrapper, salesSchedulingDto.getSalesSchedulingDateStart());
        TimeUtil.queryEndTime(queryWrapper, salesSchedulingDto.getSalesSchedulingDateEnd());
        OrderUtil.query(queryWrapper, salesSchedulingDto.getOrder(), salesSchedulingDto.getOrderCol());
        return this.page(new Page<>(salesSchedulingDto.getPage(), salesSchedulingDto.getSize()), queryWrapper);
    }

    @Override
    public Boolean acceptanceNotice(List<String> idList) {
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idList);
        updateWrapper.set("notification_state", 1);
        return this.update(updateWrapper);
    }

    @Override
    public Boolean noticeReturn(SendBackDto sendBackDto) {
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", sendBackDto.getIdList());
        updateWrapper.set("reason_return", sendBackDto.getReasonReturn());
        updateWrapper.set("notification_state", 2);
        return this.update(updateWrapper);
    }

    @Override
    public IPage<Notice> queryProductionSchedulingPage(ProductionSchedulingDto productionSchedulingDto) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(productionSchedulingDto.getProductionOrder()), "production_order", productionSchedulingDto.getProductionOrder());
        queryWrapper.eq(StrUtil.isNotBlank(productionSchedulingDto.getWorkNo()), "work_no", productionSchedulingDto.getWorkNo());
        queryWrapper.eq(StrUtil.isNotBlank(productionSchedulingDto.getProduceType()), "produce_type", productionSchedulingDto.getProduceType());
        queryWrapper.eq(StrUtil.isNotBlank(productionSchedulingDto.getSchedulingState()), "scheduling_state", productionSchedulingDto.getSchedulingState());
        queryWrapper.eq(StrUtil.isNotBlank(productionSchedulingDto.getNotificationType()), "notification_type", productionSchedulingDto.getNotificationType());
        queryWrapper.eq(StrUtil.isNotBlank(productionSchedulingDto.getProductionType()), "production_type", productionSchedulingDto.getProductionType());
        queryWrapper.eq("notification_state", "1");
        TimeUtil.queryStartTime(queryWrapper, productionSchedulingDto.getSalesSchedulingDateStart());
        TimeUtil.queryEndTime(queryWrapper, productionSchedulingDto.getSalesSchedulingDateEnd());
        OrderUtil.query(queryWrapper, productionSchedulingDto.getOrder(), productionSchedulingDto.getOrderCol());
        return this.page(new Page<>(productionSchedulingDto.getPage(), productionSchedulingDto.getSize()), queryWrapper);
    }

    @Override
    public Boolean updateProductionScheduling(Notice notice) {
        notice.setSchedulingState("1");
        return this.updateById(notice);
    }

    @Override
    public Boolean issueNotice(IssueNoticeDto issueNoticeDto) {
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", issueNoticeDto.getIdList());
        updateWrapper.set("designated_unit", issueNoticeDto.getDesignatedUnitList());
        updateWrapper.set("scheduling_state", 2);
        this.update(updateWrapper);
        List<NoticeTenant> noticeTenants = new ArrayList<>();
        //循环所有通知下发的ID
        for (String id : issueNoticeDto.getIdList()) {
            //循环执行单位
            for (String executableUnit : issueNoticeDto.getExecutableUnitList()) {
                NoticeTenant noticeTenant = new NoticeTenant();
                noticeTenant.setNoticeId(id);
                noticeTenant.setUnit(executableUnit);
                noticeTenants.add(noticeTenant);
            }
            for (String designatedUnit : issueNoticeDto.getDesignatedUnitList()) {
                NoticeTenant noticeTenant = new NoticeTenant();
                noticeTenant.setNoticeId(id);
                noticeTenant.setUnit(designatedUnit);
                noticeTenants.add(noticeTenant);
            }
        }
        return noticeTenantService.saveBatch(noticeTenants);
    }

    @Override
    public Boolean cancelProductionScheduling(List<String> idList) {
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idList);
        updateWrapper.set("scheduling_state", 3);
        return this.update(updateWrapper);
    }

    @Override
    public IPage<Notice> queryAcceptingPage(AcceptingDto acceptingDto) {
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("n.id = t.notice_id ");
        queryWrapper.eq(StrUtil.isNotBlank(acceptingDto.getProductionOrder()), "production_order", acceptingDto.getProductionOrder());
        queryWrapper.eq(StrUtil.isNotBlank(acceptingDto.getWorkNo()), "work_no", acceptingDto.getWorkNo());
        queryWrapper.eq(StrUtil.isNotBlank(acceptingDto.getProduceType()), "produce_type", acceptingDto.getProduceType());
        queryWrapper.eq(StrUtil.isNotBlank(acceptingDto.getProductionType()), "production_type", acceptingDto.getProductionType());
        queryWrapper.eq(StrUtil.isNotBlank(acceptingDto.getAcceptingState()), "accepting_state", acceptingDto.getAcceptingState());
        //查询通知状态为已接收的
        queryWrapper.eq("notification_state", "1");
        //查询排产状态为已下发的
        queryWrapper.eq("scheduling_state", "2");
        //查询落成单位 或 执行单位是本公司的数据
        queryWrapper.eq("unit", tenantId);
        TimeUtil.queryStartTime(queryWrapper, acceptingDto.getSalesSchedulingDateStart());
        TimeUtil.queryEndTime(queryWrapper, acceptingDto.getSalesSchedulingDateEnd());
        OrderUtil.query(queryWrapper, acceptingDto.getOrder(), acceptingDto.getOrderCol());
        IPage<Notice> noticePage = noticeMapper.queryAcceptingPage(new Page<>(acceptingDto.getPage(), acceptingDto.getSize()), queryWrapper);
        if (CollectionUtils.isEmpty(noticePage.getRecords())) {
            return new Page<>();
        }
        //获取所有排产单 车间数据
        List<String> idList = noticePage.getRecords().stream().map(Notice::getId).collect(Collectors.toList());
        QueryWrapper<NoticeTenant> tenantQueryWrapper = new QueryWrapper<>();
        tenantQueryWrapper.in("notice_id", idList);
        List<NoticeTenant> tenantList = noticeTenantService.list(tenantQueryWrapper);
        //根据排产单分组
        Map<String, List<NoticeTenant>> collect = tenantList.stream().collect(Collectors.groupingBy(NoticeTenant::getNoticeId));
        //循环所有排产单数据 获取对应的车间信息
        for (Notice notice : noticePage.getRecords()) {
            //获取执行单位数据
            String executableUnit = collect.get(notice.getId()).stream().filter(tenant -> tenant.getUnitType().equals("1")).map(NoticeTenant::getUnit).collect(Collectors.joining(","));
            //获取落成单位数据
            String designatedUnit = collect.get(notice.getId()).stream().filter(tenant -> tenant.getUnitType().equals("2")).map(NoticeTenant::getUnit).collect(Collectors.joining(","));
            notice.setExecutableUnit(executableUnit);
            notice.setDesignatedUnit(designatedUnit);
        }
        return noticePage;

    }

    @Override
    public Boolean updateAcceptingState(UpdateAcceptingStateDto updateAcceptingState) {
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", updateAcceptingState.getIdList());
        updateWrapper.set("accepting_state", updateAcceptingState.getState());
        return this.update(updateWrapper);
    }
}
