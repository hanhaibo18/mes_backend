package com.richfit.mes.produce.service;

import com.richfit.mes.common.model.produce.Action;

/**
 * @author renzewen
 * @Description 探伤记录实现类
 */
public interface RecordStragegy {

    Boolean updateAuditInfo(String id,String isAudit,String auditRemark);

    String getType();

}
