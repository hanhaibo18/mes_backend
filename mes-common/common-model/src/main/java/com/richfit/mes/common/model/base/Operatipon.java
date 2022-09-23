package com.richfit.mes.common.model.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

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

    /**
     * 是否绑定工序（用于前端判断是否可以修改编码和名称）
     * false：不可修改   true：可修改
     */
    @TableField(exist = false)
    public boolean isUpdate;
}
