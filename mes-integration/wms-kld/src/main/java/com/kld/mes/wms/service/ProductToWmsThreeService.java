package com.kld.mes.wms.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kld.mes.wms.utils.AESUtil;
import com.richfit.mes.common.model.produce.WmsResult;
import com.richfit.mes.common.model.wms.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProductToWmsThreeService {

    /**
     * ApiKey
     */
    @Value("${wms.mesToWmsApiKey}")
    private String mesToWmsApiKey;

    /**
     * url
     */
    @Value("${wms.mesToWmsUrl}")
    private String mesToWmsUrl;


    // MES物料基础数据同步接口
    public WmsResult materialBasis(List<MaterialBasis> materialBasisList) {
        Map<String, Object> params = convertArrayInput(materialBasisList);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/uploadMat").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        WmsResult applicationResult = JSONUtil.toBean(s, WmsResult.class);
        return applicationResult;
    }

    // MES报检单驳回WMS
    public WmsResult rejectInspectionDoc(RejectInspectionDoc rejectInspectionDoc) {
        Map<String, Object> params = convertInput(rejectInspectionDoc);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/returnIns").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        WmsResult applicationResult = JSONUtil.toBean(s, WmsResult.class);
        return applicationResult;
    }

    // MES报检单质检结果上传WMS
    public WmsResult inspectionDocUpload(InspectionDocUpload inspectionDocUpload) {
        Map<String, Object> params = convertInput(inspectionDocUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/uploadInsResult").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        WmsResult applicationResult = JSONUtil.toBean(s, WmsResult.class);
        return applicationResult;
    }

    // MES申请单上传WMS（已上线）
    public WmsResult applyListUpload(List<ApplyListUpload> applyListUploads) {
        Map<String, Object> params = convertToInput(applyListUploads);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/uploadApply").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        WmsResult applicationResult = JSONUtil.toBean(s, WmsResult.class);
        return applicationResult;
    }


    // MES领料单上传WMS
    public WmsResult materialRequisitionUpload(List<MaterialRequisitionUpload> materialRequisitionUploads) {
        Map<String, Object> params = convertToInput(materialRequisitionUploads);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/uploadOutApply").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        WmsResult applicationResult = JSONUtil.toBean(s, WmsResult.class);
        return applicationResult;
    }

    // MES领料单撤回上传WMS
    public WmsResult materialRequisitionRecall(MaterialRequisitionRecall materialRequisitionRecall) {
        Map<String, Object> params = convertInput(materialRequisitionRecall);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/returnOutApply").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        WmsResult applicationResult = JSONUtil.toBean(s, WmsResult.class);
        return applicationResult;
    }

    // MES计划清单锁定/解锁物资库存上传WMS(待接口)
    public WmsResult systemUpload(SystemUpload systemUpload) {
        Map<String, Object> params = convertInput(systemUpload);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl).contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        WmsResult applicationResult = JSONUtil.toBean(s, WmsResult.class);
        return applicationResult;
    }

    // MES实时查询WMS库存
    public List<InventoryReturn> inventoryQuery(InventoryQuery inventoryQuery) {
        Map<String, Object> params = convertInput(inventoryQuery);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/getInventory").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        WmsResult applicationResult = JSONUtil.toBean(s, WmsResult.class);
        if (applicationResult.getRetStatus().equals("N")) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(s);
        String data = String.valueOf(jsonObject.get("data"));
        return JSONObject.parseArray(data, InventoryReturn.class);
    }

    // MES接收/拒绝WMS出库单
    public WmsResult acceptIssueNote(IssueNote issueNote) {
        Map<String, Object> params = convertInput(issueNote);
        //调用上传接口
        String s = HttpRequest.post(mesToWmsUrl + "/receiveTransOut").contentType("application/x-www-form-urlencoded;charset=UTF-8").charset("UTF-8").form(params).execute().body();
        WmsResult applicationResult = JSONUtil.toBean(s, WmsResult.class);
        return applicationResult;
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
     * 加密json数组
     * @param list
     * @return
     */
    private Map<String, Object> convertToInput(List list) {
        //转换json串
        String jsonStr = JSONArray.parseArray(JSON.toJSONString(list)).toString();
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
