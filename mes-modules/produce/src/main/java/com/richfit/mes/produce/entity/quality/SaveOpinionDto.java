package com.richfit.mes.produce.entity.quality;

import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: SaveOpinionDto.java
 * @Author: Hou XinYu
 * @Description: 新增意见
 * @CreateTime: 2022年11月03日 09:47:00
 */
@Data
public class SaveOpinionDto {
    @ApiModelProperty(value = "Id", dataType = "String")
    private String id;
    @ApiModelProperty(value = "意见Id", dataType = "String")
    private String opinionId;
    @ApiModelProperty(value = "意见", dataType = "String")
    private String opinion;
    @ApiModelProperty(value = "检验人员", dataType = "List<TenantUserVo>")
    private List<TenantUserVo> userList;
}
