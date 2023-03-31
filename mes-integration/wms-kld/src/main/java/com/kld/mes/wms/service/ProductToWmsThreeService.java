package com.kld.mes.wms.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kld.mes.wms.provider.SystemServiceClient;
import com.kld.mes.wms.utils.AESUtil;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.wms.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProductToWmsThreeService {

    protected final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    SystemServiceClient systemServiceClient;

    /**
     * 密钥
     */
    private final String mesUrlKey = "wms-url-key";
    private final String wmsUrlUploadMat = "wms-url-upload-mat";

    private String mesToWmsApiKey = "";
    private String mesScddUploadApi = "";
    private String mesUploadMatApi = "";

    private void init() {

        mesToWmsApiKey = systemServiceClient.findItemParamByCode(mesUrlKey).getData().getLabel();
        mesUploadMatApi = systemServiceClient.findItemParamByCode(wmsUrlUploadMat).getData().getLabel();
    }


    // MES物料基础数据同步接口
    public ApplicationResult materialBasisInterface(List<MaterialBasis> materialBasisList) {
        init();
        //转换json数组
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(materialBasisList));
        //加密后的16进制字符串
        String materialBasisEncrpy = AESUtil.encrypt(jsonArray.toString(), mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", materialBasisEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesUploadMatApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS报检单上传MES
    public ApplicationResult reverseInspectionDocUploadInterface(ReverseInspectionDocUpload reverseInspectionDocUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(reverseInspectionDocUpload);
        //加密后的16进制字符串
        String reverseInspectionDocUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", reverseInspectionDocUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES报检单驳回WMS
    public ApplicationResult rejectInspectionDocInterface(RejectInspectionDoc rejectInspectionDoc) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(rejectInspectionDoc);
        //加密后的16进制字符串
        String rejectInspectionDocEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", rejectInspectionDocEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES报检单质检结果上传WMS
    public ApplicationResult inspectionDocUploadInterface(InspectionDocUpload inspectionDocUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(inspectionDocUpload);
        //加密后的16进制字符串
        String inspectionDocUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", inspectionDocUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES申请单上传WMS（已上线）
    public ApplicationResult applyListUploadInterface(ApplyListUpload applyListUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(applyListUpload);
        //加密后的16进制字符串
        String applyListUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", applyListUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS入库信息上传MES
    public ApplicationResult reverseInputDatabaseUploadInterface(ReverseInputDatabaseUpload reverseInputDatabaseUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(reverseInputDatabaseUpload);
        //加密后的16进制字符串
        String reverseInputDatabaseUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", reverseInputDatabaseUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS入库信息冲销上传MES
    public ApplicationResult reverseInputDatabaseCoverUploadInterface(ReverseInputDatabaseCoverUpload reverseInputDatabaseCoverUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(reverseInputDatabaseCoverUpload);
        //加密后的16进制字符串
        String reverseInputDatabaseCoverUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", reverseInputDatabaseCoverUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;

    }

    // MES领料单上传WMS
    public ApplicationResult materialRequisitionUploadInterface(MaterialRequisitionUpload materialRequisitionUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(materialRequisitionUpload);
        //加密后的16进制字符串
        String materialRequisitionUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", materialRequisitionUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS领料单关闭上传MES
    public ApplicationResult reverseMaterialRequisitionCloseUploadInterface(ReverseMaterialRequisitionCloseUpload reverseMaterialRequisitionCloseUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(reverseMaterialRequisitionCloseUpload);
        //加密后的16进制字符串
        String reverseMaterialRequisitionCloseUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", reverseMaterialRequisitionCloseUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES领料单撤回上传WMS
    public ApplicationResult materialRequisitionRecallInterface(MaterialRequisitionRecall materialRequisitionRecall) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(materialRequisitionRecall);
        //加密后的16进制字符串
        String materialRequisitionRecallEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", materialRequisitionRecallEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS出库信息上传MES
    public ApplicationResult reverseOutputDatabaseUploadInterface(ReverseOutputDatabaseUpload reverseOutputDatabaseUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(reverseOutputDatabaseUpload);
        //加密后的16进制字符串
        String reverseOutputDatabaseUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", reverseOutputDatabaseUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS出库信息冲销上传MES
    public ApplicationResult reverseOutputDatabaseCoverUploadInterface(ReverseOutputDatabaseCoverUpload reverseOutputDatabaseCoverUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(reverseOutputDatabaseCoverUpload);
        //加密后的16进制字符串
        String reverseOutputDatabaseCoverUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", reverseOutputDatabaseCoverUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES计划清单锁定/解锁物资库存上传WMS
    public ApplicationResult systemUploadInterface(SystemUpload systemUpload) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(systemUpload);
        //加密后的16进制字符串
        String systemUploadEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", systemUploadEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES实时查询WMS库存
    public ApplicationResult inventoryQueryInterface(InventoryQuery inventoryQuery) {
        init();
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(inventoryQuery);
        //加密后的16进制字符串
        String inventoryQueryEncrpy = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", inventoryQueryEncrpy);
        //调用上传接口
        String s = HttpRequest.post(mesScddUploadApi).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

}
