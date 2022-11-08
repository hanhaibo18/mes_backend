package com.kld.mes.wms.utils;

import com.kld.mes.wms.provider.ProduceServiceClient;
import com.kld.mes.wms.provider.SystemServiceClient;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.security.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODOA
 * @Author ang
 * @Date 2022/8/2 10:51
 */
@Component
@Slf4j
public class TaskUtils {

    private final String code = "MaterialOutView";

    @Value("${tenant.tenantIds}")
    private List<String> tenantIds;


    private String userNameKey = "MaterialOutView-userName";
    private String passWordKey = "MaterialOutView-password";
    private String urlKey = "MaterialOutView-url";


    @Autowired
    SystemServiceClient systemServiceClient;

    @Autowired
    ProduceServiceClient produceServiceClient;


    // 添加定时任务
    @Scheduled(fixedDelayString = "${timer.time-interval}")//  执行完上次十秒后再次执行
    public void doTask() throws SQLException, ClassNotFoundException {
        for (String tenantId : tenantIds) {
            //获取用户名
            String userName = systemServiceClient.findItemParamByCode(userNameKey, tenantId, SecurityConstants.FROM_INNER).getData().getLabel();
            //获取密码
            String password = systemServiceClient.findItemParamByCode(passWordKey, tenantId, SecurityConstants.FROM_INNER).getData().getLabel();
            //获取地址
            String url = systemServiceClient.findItemParamByCode(urlKey, tenantId, SecurityConstants.FROM_INNER).getData().getLabel();
            String date = produceServiceClient.getlastTime(tenantId, SecurityConstants.FROM_INNER);
            jdbcMaterialOutView(userName, password, url, date);
        }
    }


    public void jdbcMaterialOutView(String userName, String password, String url, String time) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://" + url + "/bsj?serverTimezone=UTC&&user=" + userName + "&&password=" + password);
        saveMaterialReceive(time, conn);
        saveMaterialReceiveDetail(time, conn);
        conn.close();
    }

    public void saveMaterialReceive(String time, Connection conn) throws SQLException {
        List<MaterialReceive> materialReceiveList = new ArrayList<>();
        String sql = null;
        if (StringUtils.isEmpty(time)) {
            //查所有
            sql = "select * from v_mes_out_headers";
        } else {
            //查上次最后一条时间之后所有
            sql = "select * from v_mes_out_headers where CREATE_TIME >" + "' " + time + "'";
        }
        Statement stmt = conn.createStatement();
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
            materialReceiveList.add(materialReceive);
        }
        if (!ObjectUtils.isEmpty(materialReceiveList)) {
            //保存物料接收
            produceServiceClient.materialReceiveSaveBatch(materialReceiveList, SecurityConstants.FROM_INNER);
        }
        rs.close();
        stmt.close();
    }

    public void saveMaterialReceiveDetail(String time, Connection conn) throws SQLException {
        String sql2 = null;
        if (StringUtils.isEmpty(time)) {
            //查所有
            sql2 = "select voh.OUT_NUM,voh.APLY_NUM,voh.CREATE_TIME,voh.WORK_CODE,vol.MATERIAL_NUM,vol.MATERIAL_DESC,vol.BATCH_NUM,vol.ORDER_QUANTITY,vol.QUANTITY,vol.UNIT from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM";
        } else {
            //查上次最后一条时间之后所有
            sql2 = "select voh.OUT_NUM,voh.APLY_NUM,voh.CREATE_TIME,voh.WORK_CODE,vol.MATERIAL_NUM,vol.MATERIAL_DESC,vol.BATCH_NUM,vol.ORDER_QUANTITY,vol.QUANTITY,vol.UNIT from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM WHERE voh.CREATE_TIME >" + "' " + time + "'";
        }
        Statement stmt2 = conn.createStatement();
        ResultSet rs2;
        rs2 = stmt2.executeQuery(sql2);
        List<MaterialReceiveDetail> detailList = new ArrayList<>();
        while (rs2.next()) {
            String outNum = rs2.getString("OUT_NUM");
            String aplyNum = rs2.getString("APLY_NUM");
            String materialNum = rs2.getString("MATERIAL_NUM");
            String name = rs2.getString("MATERIAL_DESC");
            String batchNum = rs2.getString("BATCH_NUM");
            int orderQuantity = rs2.getInt("ORDER_QUANTITY");
            int quantity = rs2.getInt("QUANTITY");
            String unit = rs2.getString("UNIT");

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
            detailList.add(materialReceiveDetail);
        }
        if (!ObjectUtils.isEmpty(detailList)) {
            //保存物料接收详情
            produceServiceClient.detailSaveBatch(detailList, SecurityConstants.FROM_INNER);
        }
        rs2.close();
        stmt2.close();
    }

}
