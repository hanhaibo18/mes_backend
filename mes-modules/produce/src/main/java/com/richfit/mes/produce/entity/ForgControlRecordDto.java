package com.richfit.mes.produce.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.model.produce.ForgControlRecord;
import lombok.Data;

/**
 * @author HanHaiBo
 * @date 2023/4/25 10:18
 */
@Data
public class ForgControlRecordDto {
    private IPage<ForgControlRecord> iPage;
    private String barForge;
    private String remark;
}
