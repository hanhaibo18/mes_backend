package com.kld.mes.erp.service;

import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.kld.mes.erp.entity.router.Zc80Ppif026;
import com.kld.mes.erp.entity.router.Zc80Ppif026Response;
import com.kld.mes.erp.entity.router.Zc80Ppif026S1;
import com.kld.mes.erp.entity.router.Zc80Ppif026T1;
import com.kld.mes.erp.provider.BaseServiceClient;
import com.kld.mes.erp.utils.WsTemplateFactory;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public Zc80Ppif026Response push(List<Router> routers) throws Exception {
        Zc80Ppif026 zc80Ppif026 = generateRequestBody(routers);
        //获取调用服务接口类实例
        WebServiceTemplate webServiceTemplate = wsTemplateFactory.generateTemplate(packageName);
        //发起接口调用
        Zc80Ppif026Response zc80Ppif026Response = (Zc80Ppif026Response) webServiceTemplate.marshalSendAndReceive(URL, zc80Ppif026);
        return zc80Ppif026Response;
    }

    private Zc80Ppif026 generateRequestBody(List<Router> routers) throws Exception {
        Zc80Ppif026 zc80Ppif026 = new Zc80Ppif026();
        for (Router router : routers) {
            CommonResult<Map> result = baseServiceClient.push(router);
            if (result.getStatus() != 200) {
                throw new Exception(result.getMessage());
            }
            Map data = result.getData();
            List<Sequence> sequences = JSON.parseArray(JSON.toJSONString(data.get("sequences")), Sequence.class);
            List<Product> products = JSON.parseArray(JSON.toJSONString(data.get("products")), Product.class);
            String erp = data.get("erp").toString();
            if (CollectionUtils.isEmpty(products)) {
                throw new Exception(router.getRouterNo() + ":当前图号没有找到成品的物料信息，请核对后在同步");
            }
            Product product = products.get(0);
            if (sequences != null && sequences.size() > 0) {
                Zc80Ppif026T1 t1 = new Zc80Ppif026T1();
                List<Zc80Ppif026S1> s1List = new ArrayList<>();
                int index = 0;
                for (Sequence sequence : sequences) {
                    Zc80Ppif026S1 zc80Ppif026S1 = new Zc80Ppif026S1();
                    zc80Ppif026S1.setWerks(erp);
                    String materialNo = product.getMaterialNo();
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
                    String unit = product.getUnit() != null ? product.getUnit() : "";
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

    public static String postForObject(String url, String soapRequest) {
        //构造http请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("text/xml;charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<>(soapRequest, headers);
        RestTemplateBuilder builder = new RestTemplateBuilder();
        //设置链接超时时间
        builder.setConnectTimeout(Duration.ofMinutes(1));
        builder.setReadTimeout(Duration.ofMinutes(1));
        RestTemplate restTemplate = builder.basicAuthentication("zbzz_esb", "ZBZZOSBinterface1").build();
        //返回结果
        String resultStr = restTemplate.postForObject(url, formEntity, String.class);
        //转换返回结果中的特殊字符，返回的结果中会将xml转义，此处需要反转移
        String tmpStr = StringEscapeUtils.unescapeXml(resultStr);
        log.info("获取数据转换结束");
        //获取工厂ID
        return tmpStr;
    }

    public static void main(String[] args) {
        String url = "http://emaip.erp.cnpc:80/ZBZZ/MES/ZC80_PPIF026/service/PS/PS_ZC80_PPIF026";
        String s = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:soap:functions:mc-style\"><SOAP-ENV:Header/><SOAP-ENV:Body><urn:Zc80Ppif026><IInput><item><Werk>BOMCO_ZS_JJCJ</Werk><Matnr>909001356</Matnr><Ktext>护罩-下料工艺规程复制</Ktext><Verwe>1</Verwe><Statu>4</Statu><Datuv>2023-04-07</Datuv><Vornr>0001</Vornr><Steus>ZP01</Steus><Ltxa1>下料</Ltxa1><Bmsch>1</Bmsch><Meins/><Vgw01>0.0</Vgw01><Vge01>MIN</Vge01><Vgw03>0</Vgw03><Vge03>MIN</Vge03><Vgw04>0</Vgw04><Vge04>MIN</Vge04></item><item><Werk>BOMCO_ZS_JJCJ</Werk><Matnr>909001356</Matnr><Ktext>护罩-下料工艺规程复制</Ktext><Verwe>1</Verwe><Statu>4</Statu><Datuv>2023-04-07</Datuv><Vornr>0002</Vornr><Steus>ZP01</Steus><Ltxa1>煨弯</Ltxa1><Bmsch>1</Bmsch><Meins/><Vgw01>0.0</Vgw01><Vge01>MIN</Vge01><Vgw03>0</Vgw03><Vge03>MIN</Vge03><Vgw04>0</Vgw04><Vge04>MIN</Vge04></item></IInput></urn:Zc80Ppif026></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        System.out.println(postForObject(url, s));
    }

    /**
     * 把JSONObject转成xml
     *
     * @param json JSONObject数据
     * @return 转换后的xml
     */
    public static String jsonToXml(JSONObject json) {
        Map tmpMap = json.toJavaObject(Map.class);
        return XmlUtil.mapToXmlStr(tmpMap, false);
    }
}
