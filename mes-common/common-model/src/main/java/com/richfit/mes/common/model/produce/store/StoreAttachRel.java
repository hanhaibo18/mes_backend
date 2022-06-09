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

    @ApiModelProperty(value = "入库料单Id")
    private String lineStoreId;

    @ApiModelProperty(value = "附件记录Id")
    private String attachmentId;

    @ApiModelProperty(value = "租户Id")
    private String tenantId;

    @ApiModelProperty(value = "分公司编码")
    private String branchCode;
}
