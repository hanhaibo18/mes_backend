package com.richfit.mes.produce.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.produce.ProduceRoleOperation;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.ProduceRoleOperationService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: ProcessUtil.java
 * @Author: Hou XinYu
 * @Description: 用于工序过滤工具
 * @CreateTime: 2022年11月14日 14:06:00
 */
public class ProcessFiltrationUtil {

    public static void filtration(QueryWrapper queryWrapper, SystemServiceClient client, ProduceRoleOperationService role) {
        //增加工序过滤
        CommonResult<TenantUserVo> result = client.queryByUserId(SecurityUtils.getCurrentUser().getUserId());
        QueryWrapper<ProduceRoleOperation> queryWrapperRole = new QueryWrapper<>();
        List<String> roleId = result.getData().getRoleList().stream().map(BaseEntity::getId).collect(Collectors.toList());
        queryWrapperRole.in("role_id", roleId);
        List<ProduceRoleOperation> operationList = role.list(queryWrapperRole);
        Set<String> set = operationList.stream().map(ProduceRoleOperation::getOperationId).collect(Collectors.toSet());
        if (!set.isEmpty()) {
            queryWrapper.in("operatipon_id", set);
        }
    }
}
