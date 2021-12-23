package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 字典分类
 * </p>
 *
 * @author 王瑞
 * @since 2020-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class ItemClass extends BaseEntity<ItemClass> {

    private String tenantId;

    /**
     * 编码
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

}
