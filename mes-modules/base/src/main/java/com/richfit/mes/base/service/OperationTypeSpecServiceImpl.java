package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.OperationTypeSpecMapper;
import com.richfit.mes.common.model.base.OperationTypeSpec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王瑞
 * @Description 工艺类别与质量资料
 */
@Service
public class OperationTypeSpecServiceImpl extends ServiceImpl<OperationTypeSpecMapper, OperationTypeSpec> implements OperationTypeSpecService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveOperationTypeSpec(List<OperationTypeSpec> operatiponTypeSpecs, String optTypeCode, String branchCode, String tenantId) {

        //1 先删除之前的配置
        Map parMap = new HashMap();
        parMap.put("tenant_id", tenantId);
        parMap.put("branch_code", branchCode);
        parMap.put("opt_type_code", optTypeCode);
        this.removeByMap(parMap);

        //2 重新保存新的配置
        for (int ii = 0; ii < operatiponTypeSpecs.size(); ii++) {
            this.save(operatiponTypeSpecs.get(ii));
        }
        
    }
}
