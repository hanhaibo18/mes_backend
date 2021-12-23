package com.richfit.mes.common.security.handler;

import com.richfit.mes.common.core.handle.BaseMetaObjectHandler;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.stereotype.Component;

/**
 * @author sun
 * @Description mybatis-plus自定义填充公共字段
 */
@Component
public class SecurityMetaObjectHandler extends BaseMetaObjectHandler {
    @Override
    protected String getCurrentUsername() {
        if(SecurityUtils.getCurrentUser()!=null){
            return SecurityUtils.getCurrentUser().getUsername();
        }
        return null;
    }
}
