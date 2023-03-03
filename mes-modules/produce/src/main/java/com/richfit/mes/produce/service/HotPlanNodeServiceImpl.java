package com.richfit.mes.produce.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.HotDemand;
import com.richfit.mes.common.model.produce.HotPlanNode;
import com.richfit.mes.common.model.produce.PlanOptWarning;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.HotDemandMapper;
import com.richfit.mes.produce.dao.HotPlanNodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class HotPlanNodeServiceImpl extends ServiceImpl<HotPlanNodeMapper, HotPlanNode> implements HotPlanNodeService{
    @Resource
    HotPlanNodeService hotPlanNodeService;

    @Resource
    HotDemandMapper hotDemandMapper;

    /**
     * 根据需求id查询关键工序计划节点列表
     * @param demandId
     * @return
     */
    @Override
    public List<HotPlanNode> getByDemandId(String demandId) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<HotPlanNode> queryWrapper=new QueryWrapper<>();
        //queryWrapper.eq("branch_code",branchCode);
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.eq("demand_id", demandId);
        List<HotPlanNode> list = hotPlanNodeService.list(queryWrapper);
        return list;
    }

    /**
     * 根据计划id查询毛坯需求的关键工序计划
     * @param planId
     * @return
     */
    @Override
    public List<HotPlanNode> getByPlanNodeByPlanId(String planId) {
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("plan_id",planId);
        HotDemand hotDemand = hotDemandMapper.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(hotDemand)){
            throw  new GlobalException("计划没有匹配到毛坯需求数据", ResultCode.FAILED);
        }
        List<HotPlanNode> byDemandId = this.getByDemandId(hotDemand.getId());

        Date date = new Date();
        long betweenDay = 101;
        for (HotPlanNode planNode : byDemandId) {
            //计算剩余天数
            if (!ObjectUtil.isEmpty(planNode.getFinishTime())) {
                long d = DateUtil.between(planNode.getFinishTime(), date, DateUnit.DAY);
                if (planNode.getFinishTime().getTime() < date.getTime()) {
                    planNode.setDays(-d);
                } else {
                    planNode.setDays(d);
                }
            }
            //预警状态赋值
            if (!ObjectUtil.isEmpty(planNode.getFinishTime())) {
                if (betweenDay > planNode.getDays()) {
                    betweenDay = planNode.getDays();
                }
                if (betweenDay >= 3) {
                    planNode.setAlarmStatus(1);
                } else if (betweenDay >= 0 && betweenDay < 3) {
                    planNode.setAlarmStatus(2);
                } else {
                    planNode.setAlarmStatus(3);
                }
            }
        }
        return byDemandId;
    }






}
