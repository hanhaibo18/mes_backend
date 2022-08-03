package com.kld.mes.erp.service;

import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 向ERP推送工艺
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
 */
public interface RouterService {

    public boolean push(@ApiParam(value = "工序列表") @RequestBody List<Sequence> sequences,
                        @ApiParam(value = "erp代号") @RequestParam String erpCode,
                        @ApiParam(value = "图号") @RequestParam String routeNo);

}
