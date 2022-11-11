package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.HotModelStore;
import org.springframework.web.multipart.MultipartFile;

public interface HotModelStoreService extends IService<HotModelStore> {
    public CommonResult importExcel(MultipartFile file, String tenantId);

}
