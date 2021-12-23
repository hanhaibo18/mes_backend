package com.richfit.mes.auth.authorization.provider.fallback;

import com.richfit.mes.auth.authorization.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.TenantUser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author sun
 * @Description 系统服务断路器实现
 */
@Slf4j
@Component
public class SystemServiceClientFallbackImpl implements SystemServiceClient {

    @Setter
    private Throwable cause;

    @Override
    public CommonResult<TenantUser> getUserByUniqueId(String uniqueId) {
        log.error("feign 查询用户信息失败:{}", uniqueId, cause);
        return null;
    }

    @Override
    public CommonResult<Set<Role>> queryRolesByUserId(String userId) {
        return CommonResult.success(new HashSet<Role>());
    }
}
