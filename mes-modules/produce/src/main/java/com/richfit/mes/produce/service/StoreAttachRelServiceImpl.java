package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    //保存料单对应的附件记录(增加类型)
    @Override
    public boolean batchSaveStoreFileNew(String storeId, String branchCode, List<StoreAttachRel> fileList) {

        for (StoreAttachRel file : fileList) {
            StoreAttachRel rel = new StoreAttachRel();
            rel.setLineStoreId(storeId);
            rel.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            rel.setBranchCode(branchCode);
            rel.setAttachmentId(file.getId());
            rel.setType(file.getType());
            this.save(rel);
        }
        return true;
    }

    //更新料单资料文件
    @Override
    public boolean updateStoreFile(String storeId, String branchCode, List<StoreAttachRel> fileList) {

        QueryWrapper<StoreAttachRel> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("line_store_id",storeId);
        this.remove(queryWrapper);
        for (StoreAttachRel file : fileList) {
            StoreAttachRel rel = new StoreAttachRel();
            rel.setLineStoreId(storeId);
            rel.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            rel.setBranchCode(branchCode);
            rel.setAttachmentId(file.getId());
            rel.setType(file.getType());
            this.save(rel);
        }
        return true;
    }
}
