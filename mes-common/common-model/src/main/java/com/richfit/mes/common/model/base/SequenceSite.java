package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @author 马峰
 * @Description 工序与工位关联
 */
@Data
public class SequenceSite extends BaseEntity<SequenceSite> {

    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 设备编码
     */
    private String sequenceId;
    /**
     * 工位、班组ID
     */
    private String siteId;
    /**
     * 工位、班组CODE
     */
    private String siteCode;
     /**
     * 工位、班组CODE
     */
    private String siteName;
    /**
     * 是否默认
     */
    private int isDefault;
    /**
     * 顺序号
     */
    private int orderNo;
    
}
