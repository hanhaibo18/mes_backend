package com.kld.mes.dnc.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.kld.mes.dnc.provider.SystemServiceClient;
import com.kld.mes.dnc.service.ProductToBsWmsService;
import com.kld.mes.dnc.utils.AESUtil;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wcy
 * @date 2023/3/15 14:58
 */
@Slf4j
@Service
public class ProductToBsWmsServiceImpl implements ProductToBsWmsService {

    protected final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    SystemServiceClient systemServiceClient;

    private final String mesUploadAPiKey = "bsWms-url-jk";
    private final String mesUrlKey = "bsWms-url-key";
    private final String mesScddUploadKey = "bsWms-url-scdd-upload";
    private final String mesUrlTokenKey = "bsWms-url-token";
    private final String mesUrlQueryMaterialCountApiKey = "bsWms-url-query-material-count";

    private String mesUploadApi = "";
    private String mesToWmsApiKey = "";
    private String mesScddUploadApi = "";
    private String mesUrlToken = "";
    private String mesUrlQueryMaterialCountApi = "";

    private void init() {
        mesUploadApi = systemServiceClient.findItemParamByCode(mesUploadAPiKey).getData().getLabel();
        mesToWmsApiKey = systemServiceClient.findItemParamByCode(mesUrlKey).getData().getLabel();
        mesScddUploadApi = systemServiceClient.findItemParamByCode(mesScddUploadKey).getData().getLabel();
        mesUrlToken = systemServiceClient.findItemParamByCode(mesUrlTokenKey).getData().getLabel();
        mesUrlQueryMaterialCountApi = systemServiceClient.findItemParamByCode(mesUrlQueryMaterialCountApiKey).getData().getLabel();
    }

    @Override
    public ApplicationResult anApplicationForm(IngredientApplicationDto ingredientApplicationDto) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(ingredientApplicationDto);
        //加密后的16进制字符串
        String ingredientApplicationDtoEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(16);
        params.put("i_data", ingredientApplicationDtoEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    @Override
    public int queryMaterialCount(String materialNo) {
        init();
        //构造访问参数
        Map<String, Object> params = new HashMap<>(16);
        params.put("wstr", materialNo);
        params.put("page", 1);
        params.put("token", mesUrlToken);
        //调用接口
        String number = HttpUtil.get(mesUrlQueryMaterialCountApi, params);
        if (StrUtil.isBlank(number)) {
            return 0;
        }
        String replaceAll = number.replaceAll("\\ufeff", "");
        double value = Double.parseDouble(replaceAll);
        return (int) value;
    }

    @Override
    public Boolean sendRequest(Certificate cert) {
        init();
        String sqd = cert.getCertificateNo() + DateUtil.format(new Date(), "MMddHHmmss");
        String gc = SecurityUtils.getCurrentUser().getTenantErpCode();

        //如果产品编号是图号+“ ”+序列号形式，需要截取“ ”之后的部分
        String prodNo = "";
        String spitStr = " ";
        if (!"".equals(cert.getProductNo()) && cert.getProductNo().split(spitStr).length > 1) {
            prodNo = cert.getProductNo().split(spitStr)[1];
        }//////////
        String json = "{\"sqd\":\"" + sqd + "\",\"gc \":\"" + gc + "\",\"scdd\":\""
                + cert.getProductionOrder() + "\",\"materialNum\":\"" + cert.getMaterialNo() + "\",\"quantity\":"
                + cert.getNumber() + ",\"cp\":\"" + prodNo + "\",\"batchNum\":\""
                + cert.getBatchNo() + "\",\"hgz\":\"" + cert.getCertificateNo() + "\",\"swFlag\":\"1\"}";

        String url = mesUploadApi + AESUtil.encrypt(json, mesToWmsApiKey);

        ResponseEntity<JsonNode> resp = restTemplate.getForEntity(url, JsonNode.class);
        log.debug("resp status is [{}],body is [{}]", resp.getStatusCode(), resp.getBody());
        String retStatus = "";
        if (resp.getBody() != null && resp.getBody().get("retStatus") != null) {
            retStatus = resp.getBody().get("retStatus").asText();
            log.debug("retMsg is [{}]", resp.getBody().get("retMsg").asText());
        }

        return "Y".equals(retStatus);
    }
}
