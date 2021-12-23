package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 异常管理配置
 *
 * @author 马峰
 * @since 2020-09-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class AbnormalConfig extends BaseEntity<AbnormalConfig> {

    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;
    /**
     * 异常类型
     */
    public String type;
    /**
     * 异常类型名称
     */
    public String name;
    /**
     * 异常数量
     */
    public int sum;
    /**
     * 0-按XY显示 1-按TABLE单元格布局
     */
    public int stype;
    /**
     * 背景，BASE64数据
     */
    private String sback;
    /**
     *
     */
    public int x;
    /**
     *
     */
    public int y;
    /**
     * 所属工位
     */
    public String siteId;
    /**
     * 所属产线
     */
    public String orgId;
}
