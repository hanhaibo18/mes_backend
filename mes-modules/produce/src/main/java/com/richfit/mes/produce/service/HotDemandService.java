package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.HotDemand;
import org.springframework.web.multipart.MultipartFile;

public interface HotDemandService extends IService<HotDemand> {
    CommonResult importDemand(MultipartFile file, String branchCode);
}
