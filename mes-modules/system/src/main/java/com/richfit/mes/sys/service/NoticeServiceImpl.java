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
import com.richfit.mes.sys.dao.NoticeMapper;
import com.richfit.mes.sys.entity.dto.SalesSchedulingDto;
import com.richfit.mes.sys.entity.dto.SendBackDto;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
                queryWrapper.apply("UNIX_TIMESTAMP(dis.modify_time) >= UNIX_TIMESTAMP('" + salesSchedulingDto.getSalesSchedulingDateStart() + " 00:00:00')");
            }
            //结束时间
            if (StrUtil.isNotBlank(salesSchedulingDto.getSalesSchedulingDateEnd())) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(salesSchedulingDto.getSalesSchedulingDateEnd()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(dis.modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
            }
        } catch (Exception e) {
            throw new GlobalException("时间格式处理错误", ResultCode.FAILED);
        }
        return this.page(new Page<>(salesSchedulingDto.getPage(), salesSchedulingDto.getSize()), queryWrapper);
    }

    @Override
    public Boolean acceptanceNotice(List<String> idList) {
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", idList);
        updateWrapper.set("notification_status", 1);
        return this.update(updateWrapper);
    }

    @Override
    public Boolean noticeReturn(SendBackDto sendBackDto) {
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", sendBackDto.getIdList());
        updateWrapper.set("reason_return", sendBackDto.getReasonReturn());
        updateWrapper.set("notification_status", 2);
        return this.update(updateWrapper);
    }
}
