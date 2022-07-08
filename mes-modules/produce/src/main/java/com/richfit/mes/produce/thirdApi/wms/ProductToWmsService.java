package com.richfit.mes.produce.thirdApi.wms;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.utils.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * 生产交库接口
 *
 * @Author: GaoLiang
 * @Date: 2022/6/30 17:15
 */
@Slf4j
@Service
public class ProductToWmsService {

    protected final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    SystemServiceClient systemServiceClient;

    private final String mesUploadAPiKey = "wms-url-jk";
    private final String mesUrlKey = "wms-url-key";

    private String mesUploadAPi = "";
    private String mesToWmsApiKey = "";

    private void init() {
        mesUploadAPi = systemServiceClient.findItemParamByCode(mesUploadAPiKey).getData().getLabel();
        mesToWmsApiKey = systemServiceClient.findItemParamByCode(mesUrlKey).getData().getLabel();
    }

    //接口格式，详见条码-mes接口文档
    public Boolean sendRequest(Certificate cert) {

        if (StringUtils.isEmpty(mesUploadAPi)) {
            init();
        }

        String sqd = cert.getCertificateNo() + DateUtil.format(new Date(), "MMddHHmmss");
        String gc = SecurityUtils.getCurrentUser().getTenantErpCode();

        String prod_No = "";
        if (!cert.getProductNo().equals("") && cert.getProductNo().split(" ").length > 1) {
            prod_No = cert.getProductNo().split(" ")[1];
        }

        String json = "{\"sqd\":\"" + sqd + "\",\"gc\":\"" + gc + "\",\"scdd\":\""
                + cert.getProductNo() + "\",\"materialNum\":\"" + cert.getMaterialNo()
                + "\",\"quantity\":" + cert.getNumber() + ",\"cp\":\"" + prod_No + "\",\"batchNum\":\"\",\"hgz\":\""
                + cert.getCertificateNo() + "\",\"swFlag\":\"1\"}";

        String url = mesUploadAPi + AESUtil.encrypt(json, mesToWmsApiKey);

        ResponseEntity<JsonNode> resp = restTemplate.getForEntity(url, JsonNode.class);
        log.debug("resp status is [{}],body is [{}]", resp.getStatusCode(), resp.getBody());
        String retStatus = resp.getBody().get("retStatus").asText();
        log.debug("retMsg is [{}]", resp.getBody().get("retMsg").asText());

        return "Y".equals(retStatus);
    }


}
