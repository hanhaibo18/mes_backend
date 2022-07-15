package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @author 马峰
 * @Description 工序检查表
 */
@Data
public class RouterCheck extends BaseEntity<RouterCheck> {

    /**
     * 租户ID
     */
    private String tenantId;

      /**
     * 机构编码
     */
    private String branchCode;

    /**
     * 工序外键
     */
    private String sequenceId;

    /**
     * 工艺ID
     */
    private String routerId;

    /**
     * 描述
     */
    private String remark;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 规则类型 0-人工判断 1-枚举 2-区间 3-允差
     */
    private String type;

        /**
     * 状态
     */
    private String status;

        /**
     * 顺序
     */
    private int checkOrder;

        /**
     * 单位
     */
    private String unit;

        /**
     * 方法 枚举-技术要求，检查内容，注意事项，质量资料
     */
    private String method;

        /**
     * 错误提示
     */
    private String errorMsg;

        /**
     * 是否为空
     */
    private int isEmpty;

    /**
     * 默认值
     */
    private String defualtValue;

    /**
     * 创建人
     */
    private String createBy;

        /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

        /**
     * 修改时间
     */
    private Date modifyTime;

    private String drawingNo;
    private String propertySymbol;
    private String propertyNum;
    private String propertyRule;
    private String propertyUplimit;
    private String propertyLowerlimit;
    private String propertyTestmethod;
    private String propertyInputtype;
    private String propertyDefaultvalue;
    private String propertyDatatype;
    private String propertyIndicatorname;
    private String propertyObjectname;
    private String propertyField;
    private String propertyUnit;

}
