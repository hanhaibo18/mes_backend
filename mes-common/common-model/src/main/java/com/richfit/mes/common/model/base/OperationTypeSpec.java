package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author mafeng
 * @Description 工序类型与质量资料
 */
@Data
public class OperationTypeSpec extends BaseEntity<OperationTypeSpec> {

    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 
     */
    private String optType;
    /**
     * 
     */
    private String optTypeCode;
       /**
     * 
     */
    private String optTypeName;
     /**
     * 
     */
    private String propertyValue;
       /**
     * 
     */
    private String propertyName;

}
