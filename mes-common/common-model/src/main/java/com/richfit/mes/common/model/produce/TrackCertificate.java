package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王瑞
 * @Description 跟单工序合格证
 */
@Data
public class TrackCertificate {

    @ApiModelProperty(value = "Id")
    @TableId(type = IdType.ASSIGN_UUID)
    private String Id;

    @ApiModelProperty(value = "合格证ID")
    private String certificateId;

    @ApiModelProperty(value = "合格证类型 0 工序 1 完工")
    private String certificateType;

    @ApiModelProperty(value = "跟单ID")
    private String thId;

    @ApiModelProperty(value = "跟单工序ID")
    private String tiId;

    @ApiModelProperty(value = "产品编号")
    @TableField(exist = false)
    private String productNo;

    @ApiModelProperty(value = "炉批号")
    @TableField(exist = false)
    private String batchNo;

    @ApiModelProperty(value = "数量")
    @TableField(exist = false)
    private Integer number;

}
