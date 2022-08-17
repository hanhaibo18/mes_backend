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

import java.util.List;


/**
 * @Description TODO
 * @Author ang
 * @Date 2022/7/29 17:55
 */
@Slf4j
@Service
public class MaterialReceiveServiceImpl extends ServiceImpl<MaterialReceiveMapper, MaterialReceive> implements MaterialReceiveService{

    @Autowired
    MaterialReceiveMapper materialReceiveMapper;

    @Autowired
    RequestNoteService requestNoteService;

    @Override
    public String getlastTime() {
       return materialReceiveMapper.getlastTime();
    }

    @Override
    public Page<MaterialReceive> getPage(Page<MaterialReceive> materialReceivePage, QueryWrapper<MaterialReceive> queryWrapper) {
        return materialReceiveMapper.getPage(materialReceivePage,queryWrapper);
    }

    @Override
    public Boolean saveMaterialReceiveList(List<MaterialReceive> materialReceiveList) {
        materialReceiveList.forEach(i ->{
            QueryWrapper<MaterialReceive> queryWrapper = new QueryWrapper();
            queryWrapper.eq("delivery_no", i.getDeliveryNo());
            List<MaterialReceive> list1 = this.list(queryWrapper);
            if (list1.size()>0){
                materialReceiveList.remove(i);
            }
            QueryWrapper<RequestNote> wrapper = new QueryWrapper();
            wrapper.eq("request_note_number",i.getAplyNum());
            List<RequestNote> list = requestNoteService.list(wrapper);
            if (!list.isEmpty()){
                i.setBranchCode(list.get(0).getBranchCode());
                i.setTenantId(list.get(0).getTenantId());
            }
        });
        return this.saveBatch(materialReceiveList);
    }
}
