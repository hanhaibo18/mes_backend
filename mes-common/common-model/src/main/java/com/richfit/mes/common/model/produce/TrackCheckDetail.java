package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author 马峰
 * @Description 工序质检表
 */
@Data
public class TrackCheckDetail extends BaseEntity<TrackCheckDetail> {

    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;
    /**
     * 跟单工序项ID
     */
    protected String tiId;
    /**
     * 规则ID
     */
    protected String checkId;
    /**
     * 质检类型 规则类型 0-人工判断 1-枚举 2-区间 3-允差
     */
    protected String checkType;
    /**
     * 质检结果 0-不合格 1-合格
     */
    protected int result;
    /**
     * 检查名称
     */
    protected String checkName;
    /**
     * 检查方法
     */
    protected String checkMethod;
    /**
     * 填写值
     */
    protected String value;

    /**
     * 填写说明
     */
    protected String remark;
   

}
