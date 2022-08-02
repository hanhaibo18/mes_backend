package com.kld.mes.wms.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.kld.mes.wms.provider.SystemServiceClient;
import com.kld.mes.wms.utils.AESUtil;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private final String mesScddUploadKey = "wms-url-scdd-upload";
    private final String mesUrlTokenKey = "wms-url-token";
    private final String mesUrlQueryMaterialCountApiKey = "wms-url-query-material-count";

    private String mesUploadAPi = "";
    private String mesToWmsApiKey = "";
    private String mesScddUploadApi = "";
    private String mesUrlToken = "";
    private String mesUrlQueryMaterialCountApi = "";

    private void init() {
        mesUploadAPi = systemServiceClient.findItemParamByCode(mesUploadAPiKey).getData().getLabel();
        mesToWmsApiKey = systemServiceClient.findItemParamByCode(mesUrlKey).getData().getLabel();
        mesScddUploadApi = systemServiceClient.findItemParamByCode(mesScddUploadKey).getData().getLabel();
        mesUrlToken = systemServiceClient.findItemParamByCode(mesUrlTokenKey).getData().getLabel();
        mesUrlQueryMaterialCountApi = systemServiceClient.findItemParamByCode(mesUrlQueryMaterialCountApiKey).getData().getLabel();
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

    //泵业查询接口
    public int queryMaterialCount(String materialNo) {
        if (StringUtils.isEmpty(mesUrlQueryMaterialCountApi)) {
            init();
        }
        //构造访问参数
        Map<String, Object> params = new HashMap<>(3);
        params.put("wstr", materialNo);
        params.put("page", 1);
        params.put("token",mesUrlToken);
        //调用接口
        String number = HttpUtil.get(mesUrlQueryMaterialCountApi, params);
        if (StringUtil.isNullOrEmpty(number)) {
            return 0;
        }
        String replaceAll = number.replaceAll("\\ufeff", "");
        double value = Double.parseDouble(replaceAll);
        return (int) value;
    }

    //配料申请单上传接口
    public Boolean anApplicationForm(IngredientApplicationDto ingredientApplicationDto) throws Exception {

        if (StringUtils.isEmpty(mesScddUploadApi)) {
            init();
        }

        //转换json串
        String jsonStr = JSONUtil.toJsonStr(ingredientApplicationDto);
        //加密后的16进制字符串
        String ingredientApplicationDtoEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", ingredientApplicationDtoEncrpy);
        //调用上传接口
        String s = HttpUtil.get(mesScddUploadApi, params, 1200000);
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        //上传成功返回Y
        return "Y".equals(applicationResult.getRetStatus());
    }


}
