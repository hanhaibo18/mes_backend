package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.MaterialReceiveDetailMapper;
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
public class MaterialReceiveDetailServiceImpl extends ServiceImpl<MaterialReceiveDetailMapper, MaterialReceiveDetail> implements MaterialReceiveDetailService {

    @Resource
    MaterialReceiveDetailMapper materialReceiveDetailMapper;

    @Resource
    MaterialReceiveService materialReceiveService;

    @Resource
    MaterialReceiveDetailService materialReceiveDetailService;

    @Resource
    LineStoreService lineStoreService;

    @Override
    public List<MaterialReceiveDetail> getReceiveDetail(QueryWrapper<MaterialReceiveDetail> queryWrapper) {
        return materialReceiveDetailMapper.getReceiveDetail(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateState(String deliveryNo, String branchCode) {
        QueryWrapper<MaterialReceive> wrapperMaterialReceive = new QueryWrapper<>();
        wrapperMaterialReceive.eq("delivery_no", deliveryNo);
        wrapperMaterialReceive.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        wrapperMaterialReceive.eq("state", "0");
        List<MaterialReceive> materialReceiveList = materialReceiveService.list(wrapperMaterialReceive);
        if (materialReceiveList == null && materialReceiveList.size() == 0) {
            throw new GlobalException("当前选择的数据异常，或者选择的已经被接收，没有找到该数据，请刷新页面重试!", ResultCode.FAILED);
        }
        QueryWrapper<MaterialReceiveDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("delivery_no", deliveryNo);
        wrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<MaterialReceiveDetail> list = materialReceiveDetailService.list(wrapper);
        boolean b = lineStoreService.addStoreByWmsSend(list, branchCode);
        if (b) {
            UpdateWrapper<MaterialReceive> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("state", 1);
            updateWrapper.eq("delivery_no", list.get(0).getDeliveryNo());
            updateWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            materialReceiveService.update(updateWrapper);
            UpdateWrapper<MaterialReceiveDetail> detailWrapper = new UpdateWrapper<>();
            detailWrapper.set("state", 1);
            detailWrapper.eq("delivery_no", list.get(0).getDeliveryNo());
            detailWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            this.update(detailWrapper);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean saveDetailList(List<MaterialReceiveDetail> detailList) {
        for (MaterialReceiveDetail materialReceiveDetail : detailList) {
            QueryWrapper<MaterialReceiveDetail> queryWrapper = new QueryWrapper();
            queryWrapper.eq("delivery_no", materialReceiveDetail.getDeliveryNo());
            List<MaterialReceiveDetail> list = this.list(queryWrapper);
            if (!list.isEmpty()) {
                detailList.remove(materialReceiveDetail);
            }
        }
        return this.saveBatch(detailList);
    }
}
