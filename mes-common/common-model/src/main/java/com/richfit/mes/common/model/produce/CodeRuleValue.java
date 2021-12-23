package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 编码规则字段模块
 *
 * @author 马峰
 * @since 2020-09-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class CodeRuleValue extends BaseEntity<CodeRuleValue> {

    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;
    /**
     * 规则项ID
     */
    public String itemId;
   
    /**
     * 输入项值
     */
    public String inputValue;
    /**
     *  流水号值
     */
    public String snValue;
    
}
