package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 异常管理
 *
 * @author 马峰
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class Abnormal extends BaseEntity<Abnormal> {

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
     * 名称
     */
    public String name;
    /**
     * 级别 ，0-一般 1=严重 2=紧急
     */
    public int level;
    /**
     * 是否安灯呼叫 ，0-否 1=是
     */
    public int isAndon;
    /**
     * 状态 状态 1-打开 0-关闭
     */
    public int status;
    /**
     * 关闭方式 0-正常关闭 1-忽略
     */
    public int closeType;
    /**
     * 所属工位
     */
    public String siteId;
    /**
     * 所属产线
     */
    public String orgId;
    /**
     * 创建人
     */
    private String submitBy;
    /**
     * 创建时间
     */
    private Date submitTime;
    /**
     * 创建人
     */
    private String closeBy;
    /**
     * 创建时间
     */
    private Date closeTime;
}
