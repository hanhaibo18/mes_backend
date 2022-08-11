package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024Response;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024S1;
import com.kld.mes.erp.entity.certWorkHour.Zc80Ppif024T1;
import com.kld.mes.erp.entity.order.WERKS;
import com.kld.mes.erp.entity.order.ZC80PPIF009;
import com.kld.mes.erp.entity.router.Zc80Ppif026;
import com.kld.mes.erp.entity.router.Zc80Ppif026Response;
import com.kld.mes.erp.entity.router.Zc80Ppif026S1;
import com.kld.mes.erp.entity.router.Zc80Ppif026T1;
import com.kld.mes.erp.provider.BaseServiceClient;
import com.kld.mes.erp.utils.WsTemplateFactory;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.constant.SecurityConstants;
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

    @Value("${interface.erp.router}")
    private String URL;

    @Autowired
    private WsTemplateFactory wsTemplateFactory;

    @Autowired
    private BaseServiceClient baseServiceClient;

    private final String packageName = "com.kld.mes.erp.entity.router";

    @Override
    public boolean push(List<Router> routers) {

        Zc80Ppif026 zc80Ppif026 = generateRequestBody(routers);

        //获取调用服务接口类实例
        WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate(packageName);

        //发起接口调用
        Zc80Ppif026Response zc80Ppif026Response = (Zc80Ppif026Response) webServiceTemplate
                .marshalSendAndReceive(URL, zc80Ppif026);

        return zc80Ppif026Response.getEMes().equals("完成");
    }

    private Zc80Ppif026 generateRequestBody(List<Router> routers) {
        Zc80Ppif026 zc80Ppif026 = new Zc80Ppif026();

        for (Router router : routers) {
            List<Sequence> sequences = baseServiceClient.getByRouterNo(router.getRouterNo(), router.getBranchCode(), router.getTenantId(), null, SecurityConstants.FROM_INNER).getData();

            if (sequences != null && sequences.size() > 0) {
                Zc80Ppif026T1 t1 = new Zc80Ppif026T1();
                List<Zc80Ppif026S1> s1List = new ArrayList<>();
                int index = 0;
                for (Sequence sequence: sequences) {
                    Zc80Ppif026S1 zc80Ppif026S1 = new Zc80Ppif026S1();
                    zc80Ppif026S1.setWerks(router.getBranchCode());
                    String materialNo = router.getProductMaterialNo() != null ? router.getProductMaterialNo() : "";
                    zc80Ppif026S1.setMatnr(materialNo);
                    zc80Ppif026S1.setVerwe("1");
                    zc80Ppif026S1.setStatu("4");
                    zc80Ppif026S1.setDatuv(sdf.format(new Date()));
                    zc80Ppif026S1.setLtxa1(sequence.getOptName());
                    zc80Ppif026S1.setKtext(router.getRouterName());

                    String vornr = "";

                    if (sequence.getOptOrder() != 0) {
                        vornr = "000" + sequence.getOptOrder();
                        vornr = vornr.substring(vornr.length() - 4, 4);
                    } else {
                        if (index < 9) {
                            vornr = "00" + (index + 1) + "0";
                        } else if (Integer.parseInt(sequence.getTechnologySequence()) >= 9) {
                            vornr = "0" + (index + 1) + "0";
                        }
                    }

                    zc80Ppif026S1.setVornr(vornr);
                    zc80Ppif026S1.setSteus("ZP01");
                    zc80Ppif026S1.setBmsch(BigDecimal.valueOf(1));
                    String unit = router.getUnit() != null ? router.getUnit() : "";
                    zc80Ppif026S1.setMeins(unit);
                    zc80Ppif026S1.setVgw01(BigDecimal.valueOf((sequence.getPrepareEndHours() + sequence.getSinglePieceHours()) * 60));
                    zc80Ppif026S1.setVge01("MIN");
                    zc80Ppif026S1.setVgw03(BigDecimal.valueOf(0));
                    zc80Ppif026S1.setVge03("MIN");
                    zc80Ppif026S1.setVgw04(BigDecimal.valueOf(0));
                    zc80Ppif026S1.setVge04("MIN");
                    s1List.add(zc80Ppif026S1);
                    index++;

                }
                t1.setItem(s1List);
                zc80Ppif026.setIInput(t1);
            }
        }

        return zc80Ppif026;
    }

}
