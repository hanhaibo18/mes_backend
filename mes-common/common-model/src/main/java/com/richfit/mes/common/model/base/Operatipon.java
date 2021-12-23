package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @author 马峰
 * @Description 工艺
 */
@Data
public class Operatipon extends BaseEntity<Operatipon> {

    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 机构编码
     */
    private String branchCode;
    /**
     * 工艺图号
     */
    private String optCode;
    /**
     * 名称
     */
    private String optName;
    /**
     * 类型
     */
    private int optType;
    /**
     * 图标
     */
    private String optIcon;
    /**
     * 状态
     */
    private int status;
     /**
     * 类型
     */
    private int optOrder;
}
