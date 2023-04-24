package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.store.StoreAttachRel;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/6/7 14:53
 */
public interface StoreAttachRelService extends IService<StoreAttachRel> {

    public boolean batchSaveStoreFile(String storeId, String branchCode, List<String> fileIds);
    public boolean batchSaveStoreFileNew(String storeId, String branchCode, List<StoreAttachRel> fileList);

    //更新料单资料文件
    boolean updateStoreFile(String storeId, String branchCode, List<StoreAttachRel> fileList);
}
