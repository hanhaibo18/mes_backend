package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

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
public class CodeRuleItemTemp extends BaseEntity<CodeRuleItemTemp> {

    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;
    /**
     * 规则ID
     */
    public String codeRuleId;
    /**
     * 序号
     */
    public int orderNo;
    /**
     * 0-常量 1-日期 2-流水号 3-用户输入 4-GUID 5-业务字段
     */
    public String type;
    /**
     * 常量值
     */
    public String constant;
    /**
     * 日期格式
     */
    public String dateFormat;
    /**
     * 日期格式
     */
    public String bussinessColumn;
    /**
     * 流水号初始大小
     */
    public String snDefault;
    /**
     * 流水号自增大小
     */
    public String snStep;
    /**
     * 流水号重置依赖 $year-按年重置 $month按月重置 $date按日期重置 $quarter 按季度重置 按最大值重置
     */
    public String snResetDependency;
      /**
     * 流水号日期
     */
    public Date snCurrentDate;
        /**
     * 流水号值
     */
    public String snCurrentValue;
    /**
     * 流水号当前值
     */
    public String checkType;
    /**
     * 正则表达式
     */
    public String checkRegex;
    /**
     * 前缀字符
     */
    public String prefixChar;         
            
    /**
     * 后缀字符
     */
    public String suffixChar;
    /**
     * 最大长度
     */
    public String maxLength;
    /**
     * 补齐方向 0-向左补齐 1-向右补齐
     */
    public String compDirect;
    /**
     * 补齐字符
     */
    public String compChar;
     /**
     * 宽度
     */
    public String width;
  
}
