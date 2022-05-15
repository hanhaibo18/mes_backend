package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.OperationTypeSpec;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 王瑞
 * @Description 工艺类别与质量资料
 */
public interface OperationTypeSpecService extends IService<OperationTypeSpec> {


    public void batchSaveOperationTypeSpec(@RequestBody List<OperationTypeSpec> operatiponTypeSpecs, String optTypeCode, String branchCode, String tenantId);

}
