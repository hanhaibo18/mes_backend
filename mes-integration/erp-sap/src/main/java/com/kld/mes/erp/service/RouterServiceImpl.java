package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024Response;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024S1;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024T1;
import com.kld.mes.erp.utils.WsTemplateFactory;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 向ERP推送工艺
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
 */
@Slf4j
@Service
public class RouterServiceImpl implements RouterService {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${interface.erp.work-hour-sync}")
    private String URL;

    @Autowired
    private WsTemplateFactory wsTemplateFactory;

    private final String packageName = "com.kld.mes.erp.entity.certWorkHour";

    @Override
    public boolean push(@ApiParam(value = "工序列表") @RequestBody List<Sequence> sequences,
                        @ApiParam(value = "erp代号") @RequestParam String erpCode,
                        @ApiParam(value = "图号") @RequestParam String routeNo) {

        // todo 待完成
        return false;
    }


}
