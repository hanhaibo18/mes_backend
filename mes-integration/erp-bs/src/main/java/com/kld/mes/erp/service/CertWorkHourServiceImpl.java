package com.kld.mes.erp.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kld.mes.erp.provider.ProduceServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/21 14:59
 */
@Slf4j
@Service
public class CertWorkHourServiceImpl implements CertWorkHourService {


    @Value("${interface.erp.work-hour-sync}")
    private String URL;

    @Autowired
    private ProduceServiceClient produceServiceClient;


    @Override
    public boolean sendWorkHour(List<TrackItem> trackItemList, String erpCode, String orderNo,String materialNo, int qty, String unit) {

        try {
            JSONArray retHour = new JSONArray();
            String[] split = orderNo.split("-");
            String xc005 = split[0];                            //单别
            String xc006 = split[1];                            //单号

            //遍历跟单工序表
            for (int i = 0; i < trackItemList.size(); i++) {
                String operationId = trackItemList.get(i).getOperatiponId();            //每道跟单工序对应的工序字典表id----工序编吗
                int optSequence = trackItemList.get(i).getOptSequence();                //工序顺序
                double prepareEndHours = trackItemList.get(i).getPrepareEndHours();     //准结工时
                double singlePieceHours = trackItemList.get(i).getSinglePieceHours();   //单件工时

                String id = trackItemList.get(i).getId();                           //主键 对应报工表
                CommonResult<List<TrackComplete>> trackCompletes = produceServiceClient.trackCompleteFindByTiId(id, SecurityConstants.FROM_INNER);

                trackCompletes.getData().forEach(x -> {                             //遍历该跟单-该产品工序-报工人工时

                    String branchCode = x.getBranchCode();                          //员工所属部门
                    String userId = x.getUserId();                                  //员工编码
                    String deviceId = x.getDeviceId();                              //设备编码
                    double actualFixHours = x.getActualFixHours()==null?0:x.getActualFixHours();                    //实用固定机时
                    double actualNomalHours = x.getActualNomalHours()==null?0:x.getActualNomalHours();              //实用变动机时（正常班）
                    double actualOverHours = x.getActualOverHours()==null?0:x.getActualOverHours();                 //实用变动机时（加班）
                    double completedFixHours = x.getCompletedFixHours()==null?0:x.getCompletedFixHours();           //完成固定机时
                    double completedChangeHours = x.getCompletedChangeHours()==null?0:x.getCompletedChangeHours();  //完成变动机时
                    double singleAddHours = x.getSingleAddHours()==null?0:x.getSingleAddHours();                    //单件补付机时
                    double auxiliaryHours = x.getAuxiliaryHours()==null?0:x.getAuxiliaryHours();                    //辅助工时

                    //设置请求参数
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("XB007", branchCode);                 //员工所属部门----车间branchCode
                    jsonObject.put("XC003", userId);                     //员工编码
                    jsonObject.put("XC004", deviceId);                   //设备编码
                    jsonObject.put("XC005", xc005);                      //单别
                    jsonObject.put("XC006", xc006);                      //单号
                    jsonObject.put("XC007", operationId);                //每道跟单工序对应的工序字典表id----工序编吗
                    jsonObject.put("XC008", optSequence);                //工序顺序
                    jsonObject.put("XC010", qty);                        //合格证中数量（单件---合格产品数，批次---最终质检合格产品个数）
                    jsonObject.put("XC016", materialNo);                 //物料编码--品号
                    jsonObject.put("XC021", prepareEndHours);            //准结工时
                    jsonObject.put("XC022", singlePieceHours);           //单件工时
                    jsonObject.put("XC023", actualFixHours);             //实用固定机时
                    jsonObject.put("XC024", actualNomalHours);           //实用变动机时（正常班）
                    jsonObject.put("XC025", actualOverHours);            //实用变动机时（加班）
                    jsonObject.put("XC026", completedFixHours);          //完成固定机时
                    jsonObject.put("XC027", completedChangeHours);       //完成变动机时
                    jsonObject.put("XC028", singleAddHours);             //单件补付机时
                    jsonObject.put("XC031", auxiliaryHours);             //辅助工时
                    retHour.add(jsonObject);
                });
            }

            log.info("跟单推送{}"+retHour);

            //发送post请求
            String postResult = HttpRequest.post(URL)
                    //设置请求头
                    .header("Content-Type", "application/json")
                    //这两个请求头是项目需要加的，可以省略
                    //.header("Authorization","Basic dG9u1ZF91aV9zZWNyZXQ=")
                    //.header("tenant","MD")
                    .body(retHour.toString())
                    .execute()
                    .body();
            log.info("工时推送返回值打印{}", postResult);

            JSONObject jsonObject = JSONObject.parseObject(postResult);
            String retStatus = jsonObject.getString("retStatus");
            if ("200".equals(retStatus)) {
                return true;
            }
        } catch (Exception e) {
            log.error("工时推送异常错误{}", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 生成4位的工序号
     *
     * @param item
     * @param i
     * @return
     */
    private String getVornr(TrackItem item, int i) {

        Assert.notNull(item, "item 不应该为null");

        String s;

        int optSequence = item.getOptSequence();

        if (optSequence > 0) {
            s = "000" + optSequence;
            s = s.substring(s.length() - 4, 4);
        } else {
            if (i < 9) {
                s = "00" + (i + 1) + "0";
            } else {
                s = "0" + (i + 1) + "0";
            }
        }

        return s;
    }

    public static void main(String[] args) {
        JSONArray retHour = new JSONArray();
        for (int i = 0;i<2;i++){
            //设置请求参数
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("XB007", "branchCode");                 //员工所属部门
            jsonObject.put("XC003", "userId");                     //员工编码
            jsonObject.put("XC004", "deviceId");                   //设备编码
            jsonObject.put("XC005", "xc005");                      //单别
            jsonObject.put("XC006", "xc006");                      //单号
            jsonObject.put("XC007", "operationId");                //每道跟单工序对应的工序字典表id----工序编吗
            jsonObject.put("XC008", "optSequence");                //工序顺序
            jsonObject.put("XC010", "qty");                        //合格证中数量（单件---合格跟单数，批次---最终质检合格产品个数）
            jsonObject.put("XC016", "materialNo");                 //物料编码--品号
            jsonObject.put("XC021", "prepareEndHours");            //准结工时
            jsonObject.put("XC022", "singlePieceHours");           //单件工时
            jsonObject.put("XC023", "actualFixHours");             //实用固定机时
            jsonObject.put("XC024", "actualNomalHours");           //实用变动机时（正常班）
            jsonObject.put("XC025", "actualOverHours");            //实用变动机时（加班）
            jsonObject.put("XC026", "completedFixHours");          //完成固定机时
            jsonObject.put("XC027", "completedChangeHours");       //完成变动机时
            jsonObject.put("XC028", "singleAddHours");             //单件补付机时
            jsonObject.put("XC031", "auxiliaryHours");             //辅助工时
            retHour.add(jsonObject);
        }
    }
}
