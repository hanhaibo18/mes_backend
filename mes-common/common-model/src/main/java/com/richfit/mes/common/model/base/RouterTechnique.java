package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @author 王瑞
 * @Description 组织机构
 */
@Data
public class RouterTechnique extends BaseEntity<RouterTechnique> {

    private static final long serialVersionUID = -5801273490970600632L;
    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称", dataType = "String")
    private String projectName;

    /**
     * 工艺di
     */
    @ApiModelProperty(value = "工艺di", dataType = "String")
    private String routerId;


}
