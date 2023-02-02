package com.richfit.mes.common.model.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

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

    private static final long serialVersionUID = -8902787615609016290L;
    private String tenantId;

    /**
     * 编码
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

    @TableField(exist = false)
    @ApiModelProperty(value = "字典参数内容")
    private List<ItemParam> itemParamList = new ArrayList<>();
}
