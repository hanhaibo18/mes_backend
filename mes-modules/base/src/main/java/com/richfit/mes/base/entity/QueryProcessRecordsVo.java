package com.richfit.mes.base.entity;

import com.richfit.mes.common.model.base.Sequence;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: queryProcessRecordsVo.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年06月24日 09:25:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryProcessRecordsVo {
    @ApiModelProperty(value = "工序列表(旧)", dataType = "List")
    List<Sequence> oldList;
    @ApiModelProperty(value = "工序列表(新)", dataType = "List")
    List<Sequence> newList;
}
