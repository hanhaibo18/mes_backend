package com.richfit.mes.produce.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.produce.dao.PhysChemOrderMapper;
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
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @param batchNo
     * @return
     */
    @Override
    public IPage<PhysChemOrder> selectOrderList(int page, int size, String startTime, String endTime, String batchNo,String status){
        QueryWrapper<PhysChemOrder> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(startTime)){
            queryWrapper.gt("modify_time",startTime);
        }
        if(!StringUtils.isEmpty(endTime)){
            queryWrapper.lt("modify_time",endTime);
        }
        if(!StringUtils.isEmpty(batchNo)){
            queryWrapper.likeLeft("batch_no",batchNo);
        }
        if(!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }
        //queryWrapper.eq("consignor", SecurityUtils.getCurrentUser().getUserId());
        return this.page(new Page<>(page, size), queryWrapper);
    }
}
