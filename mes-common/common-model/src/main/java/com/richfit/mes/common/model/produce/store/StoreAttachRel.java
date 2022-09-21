package com.richfit.mes.common.model.produce.store;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: GaoLiang
 * @Date: 2022/6/7 14:48
 */
@Data
public class StoreAttachRel extends BaseEntity<StoreAttachRel> {

    private static final long serialVersionUID = -2675974869135643360L;
    @ApiModelProperty(value = "入库料单Id")
    private String lineStoreId;

    @ApiModelProperty(value = "附件记录Id")
    private String attachmentId;

    @ApiModelProperty(value = "租户Id")
    private String tenantId;

    @ApiModelProperty(value = "分公司编码")
    private String branchCode;

    @ApiModelProperty(value = "上传的质量字量类型")
    private String type;
}
