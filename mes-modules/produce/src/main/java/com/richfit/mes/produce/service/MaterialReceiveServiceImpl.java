package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.RequestNote;
import com.richfit.mes.produce.dao.MaterialReceiveMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * @Description TODO
 * @Author ang
 * @Date 2022/7/29 17:55
 */
@Slf4j
@Service
public class MaterialReceiveServiceImpl extends ServiceImpl<MaterialReceiveMapper, MaterialReceive> implements MaterialReceiveService {

    @Autowired
    MaterialReceiveMapper materialReceiveMapper;

    @Autowired
    RequestNoteService requestNoteService;

    @Override
    public String getlastTime(String tenantId) {
        Date date = materialReceiveMapper.getlastTime(tenantId);
        if (null != date) {
            return date.toString();
        }
        return null;
    }

    @Override
    public Page<MaterialReceive> getPage(Page<MaterialReceive> materialReceivePage, QueryWrapper<MaterialReceive> queryWrapper) {
        return materialReceiveMapper.getPage(materialReceivePage, queryWrapper);
    }

    @Override
    public Boolean saveMaterialReceiveList(List<MaterialReceive> materialReceiveList) {
        String deliveryNo = materialReceiveList.get(0).getDeliveryNo();
        String aplyNum = materialReceiveList.get(0).getAplyNum();

        QueryWrapper<MaterialReceive> queryWrapper = new QueryWrapper();
        queryWrapper.eq("delivery_no", deliveryNo);
        List<MaterialReceive> list = this.list(queryWrapper);
        if (list.size() > 0) {
            materialReceiveList = null;
        } else {
            QueryWrapper<RequestNote> wrapper = new QueryWrapper();
            wrapper.eq("request_note_number", aplyNum);
            List<RequestNote> requestNotes = requestNoteService.list(wrapper);
            if (!requestNotes.isEmpty()) {
                for (MaterialReceive materialReceive : list) {
                    materialReceive.setBranchCode(requestNotes.get(0).getBranchCode());
                    materialReceive.setTenantId(requestNotes.get(0).getTenantId());
                }
            }
        }
        return this.saveBatch(materialReceiveList);
    }
}
