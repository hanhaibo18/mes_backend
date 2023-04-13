package com.kld.mes.wms.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kld.mes.wms.utils.AESUtil;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.wms.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Component
@Data
@ConfigurationProperties(prefix = "wms")
public class ProductToWmsThreeService {

    protected final RestTemplate restTemplate = new RestTemplate();

    /**
     * ApiKey
     */
    private String mesToWmsApiKey;

    /**
     * url
     */
    private String mesToWmsUrl;


    // MES物料基础数据同步接口
    public ApplicationResult materialBasisInterface(List<MaterialBasis> materialBasisList) {
        Map<String, Object> params = convertArrayInput(materialBasisList);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/uploadMat").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS报检单上传MES(待接口)
    public ApplicationResult reverseInspectionDocUploadInterface(ReverseInspectionDocUpload reverseInspectionDocUpload) {
        Map<String, Object> params = convertInput(reverseInspectionDocUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES报检单驳回WMS(待接口)
    public ApplicationResult rejectInspectionDocInterface(RejectInspectionDoc rejectInspectionDoc) {
        Map<String, Object> params = convertInput(rejectInspectionDoc);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES报检单质检结果上传WMS(待接口)
    public ApplicationResult inspectionDocUploadInterface(InspectionDocUpload inspectionDocUpload) {
        Map<String, Object> params = convertInput(inspectionDocUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES申请单上传WMS（已上线）
    public ApplicationResult applyListUploadInterface(List<ApplyListUpload> applyListUpload) {
        Map<String, Object> params = convertArrayInput(applyListUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/uploadApply").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS入库信息上传MES(待接口)
    public ApplicationResult reverseInputDatabaseUploadInterface(ReverseInputDatabaseUpload reverseInputDatabaseUpload) {
        Map<String, Object> params = convertInput(reverseInputDatabaseUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS入库信息冲销上传MES(待接口)
    public ApplicationResult reverseInputDatabaseCoverUploadInterface(ReverseInputDatabaseCoverUpload reverseInputDatabaseCoverUpload) {
        Map<String, Object> params = convertInput(reverseInputDatabaseCoverUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;

    }

    // MES领料单上传WMS(待接口)
    public ApplicationResult materialRequisitionUploadInterface(MaterialRequisitionUpload materialRequisitionUpload) {
        Map<String, Object> params = convertInput(materialRequisitionUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS领料单关闭上传MES(待接口)
    public ApplicationResult reverseMaterialRequisitionCloseUploadInterface(ReverseMaterialRequisitionCloseUpload reverseMaterialRequisitionCloseUpload) {
        Map<String, Object> params = convertInput(reverseMaterialRequisitionCloseUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES领料单撤回上传WMS(待接口)
    public ApplicationResult materialRequisitionRecallInterface(MaterialRequisitionRecall materialRequisitionRecall) {
        Map<String, Object> params = convertInput(materialRequisitionRecall);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS出库信息上传MES(待接口)
    public ApplicationResult reverseOutputDatabaseUploadInterface(ReverseOutputDatabaseUpload reverseOutputDatabaseUpload) {
        Map<String, Object> params = convertInput(reverseOutputDatabaseUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // WMS出库信息冲销上传MES(待接口)
    public ApplicationResult reverseOutputDatabaseCoverUploadInterface(ReverseOutputDatabaseCoverUpload reverseOutputDatabaseCoverUpload) {
        Map<String, Object> params = convertInput(reverseOutputDatabaseCoverUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES计划清单锁定/解锁物资库存上传WMS(待接口)
    public ApplicationResult systemUploadInterface(SystemUpload systemUpload) {
        Map<String, Object> params = convertInput(systemUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        ApplicationResult applicationResult = JSONUtil.toBean(s, ApplicationResult.class);
        return applicationResult;
    }

    // MES实时查询WMS库存
    public List<InventoryReturn> inventoryQueryInterface(InventoryQuery inventoryQuery) {
        Map<String, Object> params = convertInput(inventoryQuery);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/getInventory").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        JSONObject jsonObject = JSON.parseObject(s);
        String data = jsonObject.get("data").toString();
        return JSONObject.parseArray(data, InventoryReturn.class);
    }

    /**
     * 加密json数组
     * @param o
     * @return
     */
    private Map<String, Object> convertArrayInput(Object o) {
        //转换json串
        String jsonStr = JSONArray.parseArray(JSON.toJSONString(o)).toString();
        //加密后的16进制字符串
        String encryptString = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", encryptString);
        return params;
    }

    /**
     * 加密json串
     * @param o
     * @return
     */
    private Map<String, Object> convertInput(Object o) {
        //转换json串
        String jsonStr = JSONUtil.toJsonStr(o);
        //加密后的16进制字符串
        String encryptString = AESUtil.encrypt(jsonStr, mesToWmsApiKey);
        //传参
        Map<String, Object> params = new HashMap<>(3);
        params.put("i_data", encryptString);
        return params;
    }

}
