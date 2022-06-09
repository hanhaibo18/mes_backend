package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.store.StoreAttachRel;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.StoreAttachRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/6/7 14:54
 */
@Transactional
@Service
public class StoreAttachRelServiceImpl extends ServiceImpl<StoreAttachRelMapper, StoreAttachRel> implements StoreAttachRelService {

    @Autowired
    public StoreAttachRelMapper storeAttachRelMapper;


    //保存料单对应的附件记录
    @Override
    public boolean batchSaveStoreFile(String storeId, String branchCode, List<String> fileIds) {

        for (String fileId : fileIds) {
            StoreAttachRel rel = new StoreAttachRel();
            rel.setLineStoreId(storeId);
            rel.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            rel.setBranchCode(branchCode);
            rel.setAttachmentId(fileId);
            this.save(rel);
        }

        return true;
    }
}
