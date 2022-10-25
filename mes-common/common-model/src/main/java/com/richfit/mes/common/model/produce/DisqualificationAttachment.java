package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * produce_disqualification_attachment
 *
 * @author hou xinyu
 */
@Data
public class DisqualificationAttachment extends BaseEntity<DisqualificationAttachment> {
    @ApiModelProperty(value = "不合格品ID", dataType = "Date")
    private String disqualificationId;

    @ApiModelProperty(value = "文件ID", dataType = "Date")
    private String fileId;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称", dataType = "Date")
    private String fileName;

    @ApiModelProperty(value = "质检人", dataType = "Date")
    private String userName;

    @ApiModelProperty(value = "质检时间", dataType = "Date")
    private Date checkTime;

    /**
     * 工厂代码
     */
    @ApiModelProperty(value = "工厂代码", dataType = "Date")
    private String branchCode;

    /**
     * 租户Id
     */
    @ApiModelProperty(value = "租户Id", dataType = "Date")
    private String tenantId;

    private static final long serialVersionUID = 1L;
}
