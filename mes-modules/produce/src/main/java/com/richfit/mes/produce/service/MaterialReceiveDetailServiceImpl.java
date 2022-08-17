package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.produce.RequestNote;
import com.richfit.mes.produce.dao.MaterialReceiveDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description TODO
 * @Author ang
 * @Date 2022/8/2 9:20
 */
@Service
@Transactional
public class MaterialReceiveDetailServiceImpl extends ServiceImpl<MaterialReceiveDetailMapper, MaterialReceiveDetail> implements MaterialReceiveDetailService{

    @Resource
    MaterialReceiveDetailMapper materialReceiveDetailMapper;

    @Resource
    MaterialReceiveService materialReceiveService;

    @Override
    public List<MaterialReceiveDetail> getReceiveDetail(QueryWrapper<MaterialReceiveDetail> queryWrapper) {
        return materialReceiveDetailMapper.getReceiveDetail(queryWrapper);
    }

    @Override
    public Boolean updateState(List<MaterialReceiveDetail> list) {
        list.forEach(i -> {
            //已配料情况
            UpdateWrapper<MaterialReceive> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("state",1);
            updateWrapper.eq("delivery_no",i.getDeliveryNo());
            materialReceiveService.update(updateWrapper);
            UpdateWrapper<MaterialReceiveDetail> detailWrapper = new UpdateWrapper<>();
            detailWrapper.set("state",1);
            detailWrapper.eq("delivery_no",i.getDeliveryNo());
            this.update(detailWrapper);
        });
        return true;
    }

    @Override
    public Boolean saveDetailList(List<MaterialReceiveDetail> detailList) {
        for (MaterialReceiveDetail materialReceiveDetail : detailList) {
            QueryWrapper<MaterialReceiveDetail> queryWrapper = new QueryWrapper();
            queryWrapper.eq("delivery_no", materialReceiveDetail);
            List<MaterialReceiveDetail> list = this.list(queryWrapper);
            if (list.size()>0){
                detailList.remove(materialReceiveDetail);
            }
        }
        return this.saveBatch(detailList);
    }
}
