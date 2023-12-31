package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.NormalizeDehydroRecord;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;

import java.util.List;

/**
 * 正火去氢工序控制记录(ProduceNormalizeDehydroRecord)表服务接口
 *
 * @author makejava
 * @since 2023-03-23 14:18:24
 */
public interface NormalizeDehydroRecordService extends IService<NormalizeDehydroRecord> {


    Boolean saveNormalizeDehydroRecord(NormalizeDehydroRecord record);

    void synchronizationRecordStatus(String furnaceId, String status);

    boolean updateNormalizeDehydroRecord(NormalizeDehydroRecord normalizeDehydroRecord);

    NormalizeDehydroRecord getById(String id);

    boolean isBzz(TenantUserDetails currentUser);
}

