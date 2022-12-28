package com.richfit.mes.common.model.produce.dto;

import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MaterialReceiveDto.java
 * @Author: Hou XinYu
 * @Description: 物料接受保存
 * @CreateTime: 2022年12月26日 16:08:00
 */
@Data
public class MaterialReceiveDto {
    private List<MaterialReceive> received = new ArrayList<>();
    private List<MaterialReceiveDetail> detailList = new ArrayList<>();
}
