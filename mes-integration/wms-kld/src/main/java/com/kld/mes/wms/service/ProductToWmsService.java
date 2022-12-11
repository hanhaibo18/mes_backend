package com.kld.mes.wms.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.kld.mes.wms.provider.SystemServiceClient;
import com.kld.mes.wms.utils.AESUtil;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.common.security.util.SecurityUtils;
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

    //接口格式，详见条码-mes接口文档
    public Boolean sendRequest(Certificate cert) {

        if (StringUtils.isEmpty(mesUploadApi)) {
            init();
        }

        String sqd = cert.getCertificateNo() + DateUtil.format(new Date(), "MMddHHmmss");
        String gc = SecurityUtils.getCurrentUser().getTenantErpCode();

        //如果产品编号是图号+“ ”+序列号形式，需要截取“ ”之后的部分
        String prodNo = "";
        String spitStr = " ";
        if (!"".equals(cert.getProductNo()) && cert.getProductNo().split(spitStr).length > 1) {
            prodNo = cert.getProductNo().split(spitStr)[1];
        }
        String json = "{\"sqd\":\"" + sqd + "\",\"gc\":\"" + gc + "\",\"scdd\":\""
                + cert.getProductionOrder() + "\",\"materialNum\":\"" + cert.getMaterialNo()
                + "\",\"quantity\":" + cert.getNumber() + ",\"cp\":\"" + prodNo + "\",\"batchNum\":\"\",\"hgz\":\""
                + cert.getCertificateNo() + "\",\"swFlag\":\"1\"}";

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

    //泵业查询接口
    public int queryMaterialCount(String materialNo) {
        if (StringUtils.isEmpty(mesUrlQueryMaterialCountApi)) {
            init();
        }
        //构造访问参数
        Map<String, Object> params = new HashMap<>(3);
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

    //配料申请单上传接口
    public ApplicationResult anApplicationForm(IngredientApplicationDto ingredientApplicationDto) throws Exception {
        if (StringUtils.isEmpty(mesScddUploadApi)) {
            init();
        }
        //转换json串
        ingredientApplicationDto.setGc("X088");
        String jsonStr = JSONUtil.toJsonStr(ingredientApplicationDto);
        //加密后的16进制字符串
        String ingredientApplicationDtoEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", ingredientApplicationDtoEncrpy);
        //调用上传接口
        String s = HttpUtil.post(mesScddUploadApi, params, 120000);
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        applicationResult.setEncryption(ingredientApplicationDtoEncrpy);
        return applicationResult;
    }


}
