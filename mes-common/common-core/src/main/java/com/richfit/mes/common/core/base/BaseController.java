package com.richfit.mes.common.core.base;

import com.richfit.mes.common.core.exception.GlobalException;

/**
 * @author sun
 * @Description 基础Controller
 */
public abstract class BaseController {

    public final static String TENANT_ROLE_CODE = "role_tenant_admin";

    public GlobalException handleException(Exception e) {


        return null;
    }
}
