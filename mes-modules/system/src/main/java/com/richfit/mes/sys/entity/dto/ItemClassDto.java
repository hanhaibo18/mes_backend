package com.richfit.mes.sys.entity.dto;


import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.sys.ItemClass;
import lombok.Data;

/**
 *
 * @author llh
 */
@Data
public class ItemClassDto extends BaseEntity<ItemClass> {

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
