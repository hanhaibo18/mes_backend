package com.richfit.mes.produce.entity;

import com.richfit.mes.common.core.base.BasePageDto;
import lombok.Data;

/**
 * @Author: GaoLiang
 * @Date: 2020/10/16 10:27
 */
@Data
public class KanbanDto extends BasePageDto<KanbanDto> {

    private String tenantId;
    private String branchCode;
    private String yearStart;
    private String yearEnd;
    private String monthStart;
    private String monthEnd;

    private String drawNoType;

}
