package com.richfit.mes.produce.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.produce.dao.PhysChemOrderMapper;
import com.richfit.mes.produce.entity.phyChemTestVo.PhyChemTaskVo;
import com.richfit.mes.produce.utils.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author renzewen
 * @Description 理化检验委托单
 */
@Slf4j
@Service
public class PhysChemOrderServiceImpl extends ServiceImpl<PhysChemOrderMapper, PhysChemOrder> implements PhysChemOrderService {


    /**
     * 查询委托单列表
     * @param phyChemTaskVo
     */
    @Override
    public IPage<PhysChemOrder> selectOrderList(PhyChemTaskVo phyChemTaskVo){
        QueryWrapper<PhysChemOrder> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(phyChemTaskVo.getStartTime())){
            queryWrapper.gt("modify_time",phyChemTaskVo.getStartTime());
        }
        if(!StringUtils.isEmpty(phyChemTaskVo.getEndTime())){
            queryWrapper.lt("modify_time",phyChemTaskVo.getEndTime());
        }
        if(!StringUtils.isEmpty(phyChemTaskVo.getBatchNo())){
            queryWrapper.likeLeft("batch_no",phyChemTaskVo.getBatchNo());
        }
        if(!StringUtils.isEmpty(phyChemTaskVo.getStatus())){
            queryWrapper.eq("status",phyChemTaskVo.getStatus());
        }
        //排序
        if(!StringUtils.isEmpty(phyChemTaskVo.getOrderCol()) && !StringUtils.isEmpty(phyChemTaskVo.getOrder())){
            OrderUtil.query(queryWrapper,phyChemTaskVo.getOrderCol(),phyChemTaskVo.getOrder());
        }

        //queryWrapper.eq("consignor", SecurityUtils.getCurrentUser().getUserId());
        return this.page(new Page<>(phyChemTaskVo.getPage(), phyChemTaskVo.getLimit()), queryWrapper);
    }
}
