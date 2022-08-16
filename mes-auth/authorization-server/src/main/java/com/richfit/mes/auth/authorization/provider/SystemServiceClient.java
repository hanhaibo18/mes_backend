package com.richfit.mes.auth.authorization.provider;

import com.richfit.mes.auth.authorization.provider.fallback.SystemServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.security.constant.SecurityConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

/**
 * @author sun
 * @Description system service
 */
@FeignClient(name = "system-service", decode404 = true, fallback = SystemServiceClientFallbackImpl.class)
public interface SystemServiceClient {

    @GetMapping(value = "/api/sys/user")
    CommonResult<TenantUser> getUserByUniqueId(@RequestParam("uniqueId") String uniqueId, @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping(value = "/api/sys/role/user/{userId}")
    CommonResult<Set<Role>> queryRolesByUserId(@PathVariable("userId") String userId, @RequestHeader(SecurityConstants.FROM) String from);
}
