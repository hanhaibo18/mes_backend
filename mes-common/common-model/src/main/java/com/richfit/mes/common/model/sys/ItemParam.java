package com.richfit.mes.common.model.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 字典参数
 * </p>
 *
 * @author 王瑞
 * @since 2020-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class ItemParam extends BaseEntity<ItemParam> {

    private static final long serialVersionUID = 6196172312945451861L;
    private String tenantId;

    /**
     * 编码序号
     */
    @TableField(exist = false)
    private String ruleNo;

    /**
     * 分类ID
     */
    private String classId;

    /**
     * 编码
     */
    private String code;

    /**
     * 参数名称
     */
    private String label;

    /**
     * 参数类型 0整数型 1浮点型 2布尔型
     */
    private String type;

    /**
     * 单位
     */
    private String unit;

    /**
     * 排序
     */
    private Integer orderNum;


    /**
     * 同code的value
     */
    @TableField(exist = false)
    private String value;

}
