package com.kld.mes.wms.utils;

import com.kld.mes.wms.provider.ProduceServiceClient;
import com.kld.mes.wms.provider.SystemServiceClient;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.produce.dto.MaterialReceiveDto;
import com.richfit.mes.common.security.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODOA
 * @Author ang
 * @Date 2022/8/2 10:51
 * @EditDescription 修改查询配送视图流程，查询加入数据过大1000条分页功能，整合接口保证业务一致性等
 * @EidtAuthor zhiqiang.lu
 * @EditDate 2022/12/26
 */
@Component
@Slf4j
public class TaskUtils {

    @Value("${tenant.tenantIds}")
    private List<String> tenantIds;


    private String userNameKey = "MaterialOutView-userName";
    private String passWordKey = "MaterialOutView-password";
    private String urlKey = "MaterialOutView-url";


    @Resource
    SystemServiceClient systemServiceClient;

    @Resource
    ProduceServiceClient produceServiceClient;


    // 添加定时任务
    @Scheduled(fixedDelayString = "${timer.time-interval}")//  执行完上次十秒后再次执行
    public void doTask() throws SQLException, ClassNotFoundException {
        System.out.println("------------------------");
        System.out.println("发起物料接收");
        for (String tenantId : tenantIds) {
            try {
                //获取用户名
                String userName = systemServiceClient.findItemParamByCode(userNameKey, tenantId, SecurityConstants.FROM_INNER).getData().getLabel();
                //获取密码
                String password = systemServiceClient.findItemParamByCode(passWordKey, tenantId, SecurityConstants.FROM_INNER).getData().getLabel();
                //获取地址
                String url = systemServiceClient.findItemParamByCode(urlKey, tenantId, SecurityConstants.FROM_INNER).getData().getLabel();
                //获取工厂code
                String code = systemServiceClient.tenantByIdInner(tenantId, SecurityConstants.FROM_INNER).getData().getTenantErpCode();
                System.out.println("code:" + code);
                //获取变更时间
                String date = produceServiceClient.getlastTime(tenantId, SecurityConstants.FROM_INNER);
                jdbcMaterialOutView(userName, password, url, code, date);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("自动同步物料接收出现异常 [{}]", "租户id:" + tenantId + ":" + e.getMessage());
            }
        }
    }

    public void jdbcMaterialOutView(String userName, String password, String url, String code, String time) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://" + url + "/bsj?serverTimezone=UTC&&user=" + userName + "&&password=" + password);
        Statement stmt = conn.createStatement();
        List<MaterialReceive> materialReceiveList = saveMaterialReceive(code, time, stmt);
        List<MaterialReceiveDetail> materialReceiveDetailList = saveMaterialReceiveDetail(code, time, stmt);
        System.out.println("materialReceiveList:" + materialReceiveList.size());
        System.out.println("materialReceiveDetailList:" + materialReceiveDetailList.size());
        MaterialReceiveDto materialReceiveDto = new MaterialReceiveDto();
        materialReceiveDto.setReceived(materialReceiveList);
        materialReceiveDto.setDetailList(materialReceiveDetailList);
        produceServiceClient.materialReceiveSaveBatchList(materialReceiveDto, SecurityConstants.FROM_INNER);
        stmt.close();
        conn.close();
    }

    public List<MaterialReceive> saveMaterialReceive(String code, String time, Statement stmt) throws Exception {
        try {
            List<MaterialReceive> materialReceiveList = new ArrayList<>();
            int total = 0;
            int pageSize = 1000;
            String sql = null;
            String totalSql = null;
            if (StringUtils.isEmpty(time)) {
                //查所有
                totalSql = "select count(APLY_NUM) as total from v_mes_out_headers where work_code ='" + code + "'";
            } else {
                //查上次最后一条时间之后所有
                totalSql = "select count(APLY_NUM) as total from v_mes_out_headers where work_code ='" + code + "' and CREATE_TIME >" + "' " + time + "'";
            }
            ResultSet totalRs = stmt.executeQuery(totalSql);
            while (totalRs.next()) {
                total = totalRs.getInt("total");
            }

            for (int page = 0; total > page * pageSize; page++) {
                if (StringUtils.isEmpty(time)) {
                    //查所有
                    sql = "select * from v_mes_out_headers where work_code ='" + code + "' order by CREATE_TIME desc limit " + page * pageSize + ",1000";
                } else {
                    //查上次最后一条时间之后所有
                    sql = "select * from v_mes_out_headers where work_code ='" + code + "' and CREATE_TIME >" + "' " + time + "' order by CREATE_TIME desc limit " + page * pageSize + ",1000";
                }
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String outNum = rs.getString("OUT_NUM");
                    String aplyNum = rs.getString("APLY_NUM");
                    String createTime = rs.getString("CREATE_TIME");
                    MaterialReceive materialReceive = new MaterialReceive();
                    materialReceive.setDeliveryNo(outNum);
                    materialReceive.setAplyNum(aplyNum);
                    materialReceive.setOutboundDate(createTime);
                    materialReceive.setState("0");
                    materialReceive.setErpCode(code);
                    materialReceiveList.add(materialReceive);
                }
//                if (!ObjectUtils.isEmpty(materialReceiveList)) {
//                    //保存物料接收
//                    boolean flag = produceServiceClient.materialReceiveSaveBatch(materialReceiveList, SecurityConstants.FROM_INNER);
//                }
                rs.close();
            }
            return materialReceiveList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public List<MaterialReceiveDetail> saveMaterialReceiveDetail(String code, String time, Statement stmt) throws Exception {
        try {
            List<MaterialReceiveDetail> detailList = new ArrayList<>();
            int total = 0;
            int pageSize = 1000;
            String sql = null;
            String totalSql = null;

            if (StringUtils.isEmpty(time)) {
                //查所有
                totalSql = "select count(vol.APLY_NUM) as total from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM where work_code ='" + code + "'";
            } else {
                //查上次最后一条时间之后所有
                totalSql = "select count(vol.APLY_NUM) as total from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM where work_code ='" + code + "' and voh.CREATE_TIME >" + "'" + time + "' order by voh.CREATE_TIME desc LIMIT 1000";
            }
            ResultSet totalRs = stmt.executeQuery(totalSql);
            while (totalRs.next()) {
                total = totalRs.getInt("total");
            }
            for (int page = 0; total > page * pageSize; page++) {
                if (StringUtils.isEmpty(time)) {
                    //查所有
                    sql = "select voh.OUT_NUM,voh.APLY_NUM,voh.CREATE_TIME,vol.MATERIAL_NUM,vol.MATERIAL_DESC,vol.BATCH_NUM,vol.ORDER_QUANTITY,vol.QUANTITY,vol.UNIT from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM  where work_code ='" + code + "'  order by voh.CREATE_TIME desc LIMIT " + page * pageSize + ",1000";
                } else {
                    //查上次最后一条时间之后所有
                    sql = "select voh.OUT_NUM,voh.APLY_NUM,voh.CREATE_TIME,vol.MATERIAL_NUM,vol.MATERIAL_DESC,vol.BATCH_NUM,vol.ORDER_QUANTITY,vol.QUANTITY,vol.UNIT from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM  where work_code ='" + code + "' and voh.CREATE_TIME >" + "'" + time + "' order by voh.CREATE_TIME desc LIMIT " + page * pageSize + ",1000";
                }
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String outNum = rs.getString("OUT_NUM");
                    String aplyNum = rs.getString("APLY_NUM");
                    String materialNum = rs.getString("MATERIAL_NUM");
                    String name = rs.getString("MATERIAL_DESC");
                    String batchNum = rs.getString("BATCH_NUM");
                    int orderQuantity = rs.getInt("ORDER_QUANTITY");
                    int quantity = rs.getInt("QUANTITY");
                    String unit = rs.getString("UNIT");

                    MaterialReceiveDetail materialReceiveDetail = new MaterialReceiveDetail();
                    materialReceiveDetail.setDeliveryNo(outNum);
                    materialReceiveDetail.setAplyNum(aplyNum);
                    materialReceiveDetail.setMaterialNum(materialNum);
                    materialReceiveDetail.setName(name);
                    materialReceiveDetail.setBatchNum(batchNum);
                    materialReceiveDetail.setOrderQuantity(orderQuantity);
                    materialReceiveDetail.setQuantity(quantity);
                    materialReceiveDetail.setUnit(unit);
                    materialReceiveDetail.setState("0");
                    //根据申请单和物料号查询图号
//                    List<RequestNoteDetail> requestNoteDetailList = produceServiceClient.queryRequestNoteDetailDetails(materialNum, aplyNum, SecurityConstants.FROM_INNER);
//                    if (!ObjectUtils.isEmpty(requestNoteDetailList)) {
//                        materialReceiveDetail.setDrawingNo(requestNoteDetailList.get(0).getDrawingNo());
//                    }
                    detailList.add(materialReceiveDetail);
                }
//                if (!ObjectUtils.isEmpty(detailList)) {
//                    //保存物料接收详情
//                    boolean flag = produceServiceClient.detailSaveBatch(detailList, SecurityConstants.FROM_INNER);
//                }
                rs.close();
            }
            return detailList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
