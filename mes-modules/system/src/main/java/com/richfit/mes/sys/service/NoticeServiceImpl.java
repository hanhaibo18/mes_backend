package com.richfit.mes.sys.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Notice;
import com.richfit.mes.common.model.sys.NoticeTenant;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.sys.dao.NoticeMapper;
import com.richfit.mes.sys.entity.dto.IssueNoticeDto;
import com.richfit.mes.sys.entity.dto.ProductionSchedulingDto;
import com.richfit.mes.sys.entity.dto.SalesSchedulingDto;
import com.richfit.mes.sys.entity.dto.SendBackDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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

    @Override
    public IPage<Notice> queryPage(SalesSchedulingDto salesSchedulingDto) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(salesSchedulingDto.getProductionOrder()), "production_order", salesSchedulingDto.getProductionOrder());
        queryWrapper.eq(StrUtil.isNotBlank(salesSchedulingDto.getWorkNo()), "work_no", salesSchedulingDto.getWorkNo());
        queryWrapper.like(StrUtil.isNotBlank(salesSchedulingDto.getProduceName()), "produce_name", salesSchedulingDto.getProduceName());
        queryWrapper.eq(StrUtil.isNotBlank(salesSchedulingDto.getUserUnit()), "user_unit", salesSchedulingDto.getUserUnit());
        queryWrapper.eq(StrUtil.isNotBlank(salesSchedulingDto.getIssuingUnit()), "issuing_unit", salesSchedulingDto.getIssuingUnit());
        try {
            //开始时间
            if (StrUtil.isNotBlank(salesSchedulingDto.getSalesSchedulingDateStart())) {
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + salesSchedulingDto.getSalesSchedulingDateStart() + " 00:00:00')");
            }
            //结束时间
            if (StrUtil.isNotBlank(salesSchedulingDto.getSalesSchedulingDateEnd())) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(salesSchedulingDto.getSalesSchedulingDateEnd()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
            }
        } catch (Exception e) {
            throw new GlobalException("时间格式处理错误", ResultCode.FAILED);
        }
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
        try {
            //开始时间
            if (StrUtil.isNotBlank(productionSchedulingDto.getSalesSchedulingDateStart())) {
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + productionSchedulingDto.getSalesSchedulingDateStart() + " 00:00:00')");
            }
            //结束时间
            if (StrUtil.isNotBlank(productionSchedulingDto.getSalesSchedulingDateEnd())) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(productionSchedulingDto.getSalesSchedulingDateEnd()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
            }
        } catch (Exception e) {
            throw new GlobalException("时间格式处理错误", ResultCode.FAILED);
        }
        queryWrapper.eq("notification_state", "1");
        OrderUtil.query(queryWrapper, productionSchedulingDto.getOrder(), productionSchedulingDto.getOrderCol());
        return this.page(new Page<>(productionSchedulingDto.getPage(), productionSchedulingDto.getSize()), queryWrapper);
    }

    @Override
    public Boolean updateProductionScheduling(Notice notice) {
        return this.updateById(notice);
    }

    @Override
    public Boolean issueNotice(IssueNoticeDto issueNoticeDto) {
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", issueNoticeDto.getIdList());
        updateWrapper.set("designated_unit", issueNoticeDto.getDesignatedUnit());
        updateWrapper.set("scheduling_state", 2);
        this.update(updateWrapper);
        List<NoticeTenant> noticeTenants = new ArrayList<>();
        //循环所有通知下发的ID
        for (String id : issueNoticeDto.getIdList()) {
            //循环执行单位
            for (String executableUnit : issueNoticeDto.getExecutableUnitList()) {
                NoticeTenant noticeTenant = new NoticeTenant();
                noticeTenant.setNoticeId(id);
                noticeTenant.setExecutableUnit(executableUnit);
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
}
