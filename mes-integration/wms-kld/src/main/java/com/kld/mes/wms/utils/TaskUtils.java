package com.kld.mes.wms.utils;

import com.kld.mes.wms.provider.ProduceServiceClient;
import com.kld.mes.wms.provider.SystemServiceClient;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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


    private String userName = "";
    private String password = "";
    private String url = "";


    @Resource
    SystemServiceClient systemServiceClient;

    @Resource
    ProduceServiceClient produceServiceClient;


    // 添加定时任务
    @Scheduled(fixedDelay = 1000 * 10)//  执行完上次十秒后再次执行
    public void doTask() throws SQLException, ClassNotFoundException {
        if (StringUtils.isEmpty(userName)){
            for (String tenantId : tenantIds) {
                List<ItemParam> list = systemServiceClient.selectItemParamByCodeInner(code,"",tenantId,SecurityConstants.FROM_INNER).getData();
                url =  list.get(0).getLabel();
                password = list.get(1).getLabel();
                userName = list.get(2).getLabel();
                String date = produceServiceClient.getlastTime(SecurityConstants.FROM_INNER);
                jdbcMaterialOutView(userName, password, url , date);
            }
        }
        userName = "";
    }




    public void jdbcMaterialOutView(String userName, String password, String url, String time) throws ClassNotFoundException, SQLException {
        List<MaterialReceive> materialReceiveList = new ArrayList<>();
        // 1、注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 2、获取数据库连接对象
        Connection conn = DriverManager.getConnection("jdbc:mysql://"+url+"/bsj?serverTimezone=UTC&&user="+userName+"&&password="+password);
        // 3、定义sql
        String sql = null;
        if (StringUtils.isEmpty(time)){
            sql = "select * from v_mes_out_headers";
        } else {
            sql = "select * from v_mes_out_headers where CREATE_TIME >" +  "' "+ time + "'";
        }
        // 4、获取执行sql的对象
        Statement stmt = conn.createStatement();
        // 5、执行sql
        ResultSet rs = stmt.executeQuery(sql);
        // 6、处理结果
        while (rs.next()){
            // 获取数据 两种方式 根据列的索引获取 根据列名获取
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
        if (!ObjectUtils.isEmpty(materialReceiveList)){
            produceServiceClient.materialReceiveSaveBatch(materialReceiveList, SecurityConstants.FROM_INNER);
        }

        // 3、定义sql2
        String sql2 = null;
        if (StringUtils.isEmpty(time)) {
            sql2 = "select voh.OUT_NUM,voh.APLY_NUM,voh.CREATE_TIME,voh.WORK_CODE,vol.MATERIAL_NUM,vol.MATERIAL_DESC,vol.BATCH_NUM,vol.ORDER_QUANTITY,vol.QUANTITY,vol.UNIT from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM";
        } else {
            sql2 = "select voh.OUT_NUM,voh.APLY_NUM,voh.CREATE_TIME,voh.WORK_CODE,vol.MATERIAL_NUM,vol.MATERIAL_DESC,vol.BATCH_NUM,vol.ORDER_QUANTITY,vol.QUANTITY,vol.UNIT from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM WHERE voh.CREATE_TIME >" +  "' "+ time + "'";
        }
        // 4、获取执行sql的对象
        Statement stmt2 = conn.createStatement();
        ResultSet rs2;
        rs2 = stmt.executeQuery(sql);
        List<MaterialReceiveDetail> detailList = new ArrayList<>();
        while (rs2.next()) {
            // 获取数据 两种方式 根据列的索引获取 根据列名获取
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
        if (!ObjectUtils.isEmpty(detailList)){
            produceServiceClient.detailSaveBatch(detailList, SecurityConstants.FROM_INNER);
        }

        rs.close();
        rs2.close();

        stmt.close();
        conn.close();
    }

}
