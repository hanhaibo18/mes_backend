package com.kld.mes.material.service;



import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kld.mes.material.dao.PhysChemOrderInnerMapper;
import com.richfit.mes.common.model.produce.PhysChemOrderInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author renzewen
 * @Description 理化检验委托单
 */
@Slf4j
@Service
public class PhysChemOrderInnerServiceImpl extends ServiceImpl<PhysChemOrderInnerMapper, PhysChemOrderInner> implements PhysChemOrderInnerService {

    /**
     * 修改委托单状态
     * @param reportNo
     * @param reportStatus
     * @return
     */
   @Override
   public Boolean changeOrderSatus(String reportNo, String reportStatus){
       //修改委托单状态
       UpdateWrapper<PhysChemOrderInner> updateWrapper = new UpdateWrapper<>();
       updateWrapper.eq("report_no",reportNo)
               .set("report_status",reportStatus)
               .set("modify_time",DateUtil.date());
       return this.update(updateWrapper);
   }

    /**
     * 修改委托单同步状态
     * @param reportNo
     * @param syncStatus
     * @return
     */
    @Override
    public Boolean changeOrderSyncSatus(String reportNo, String syncStatus){
        //修改委托单状态
        UpdateWrapper<PhysChemOrderInner> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("report_no",reportNo)
                .set("sync_status",syncStatus)
                .set("sync_time", DateUtil.date())
                .set("modify_time",DateUtil.date());
        return this.update(updateWrapper);
    }


    /**
     * 获取实验数据
     * @param reportNos
     * @return
     */
   @Override
   public List<PhysChemOrderInner> synResultInfos(List<String> reportNos){
       List<PhysChemOrderInner> physChemOrderInners = new ArrayList<>();
       if(reportNos.size()>0){
           QueryWrapper<PhysChemOrderInner> queryWrapper = new QueryWrapper<>();
           queryWrapper.in("report_no",reportNos);
           physChemOrderInners = this.list(queryWrapper);
       }
       return physChemOrderInners;
   }

    /**
     * 根据委托单号获取数据
     * @param groupIds
     * @return
     */
    @Override
    public List<PhysChemOrderInner> getInnerListByGroupIds(List<String> groupIds){
        List<PhysChemOrderInner> physChemOrderInners = new ArrayList<>();
        if(groupIds.size()>0){
            QueryWrapper<PhysChemOrderInner> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("group_id",groupIds);
            physChemOrderInners = this.list(queryWrapper);
        }
        return physChemOrderInners;
    }

    /**
     * 根据炉批号获取最近的委托单数据
     * @param batchNo
     * @return
     */
   @Override
   public List<PhysChemOrderInner> getListByBatchNo(String batchNo){
       return this.list(new QueryWrapper<PhysChemOrderInner>().eq("batch_no", batchNo).orderByDesc("modify_time"));
   }





}
