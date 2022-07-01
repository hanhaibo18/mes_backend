package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.produce.TrackCheckDetail;
import com.richfit.mes.common.model.sys.Attachment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: QueryQualityTestingResultVo.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年06月23日 17:41:00
 */
@Data
public class QueryQualityTestingResultVo {

    @ApiModelProperty(value = "质检详细信息", dataType = "List<TrackCheckDetail>")
    private List<TrackCheckDetail> trackCheckDetailList;
    @ApiModelProperty(value = "质检文件信息", dataType = "List<Attachment>")
    private List<Attachment> attachmentList;
}
