package com.kld.mes.wms.utils;

import com.kld.mes.wms.provider.SystemServiceClient;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.annotation.Inner;
import com.richfit.mes.common.security.constant.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Author ang
 * @Date 2022/8/2 10:51
 */
@Component
public class TaskUtils {

    private final String code = "MaterialOutView";


    private String userName = "";
    private String password = "";
    private String url = "";

    @Resource
    SystemServiceClient systemServiceClient;

    public void init(){
        List<ItemParam> list = systemServiceClient.selectItemClass(code,"",SecurityConstants.FROM_INNER).getData();
        url =  list.get(0).getLabel();
        password = list.get(1).getLabel();
        userName = list.get(2).getLabel();
    }


    // 添加定时任务
    @Scheduled(cron = "0 0/10 * * * ?") // cron 表达式，每10分 执行
    @Inner
    public void doTask() throws SQLException, ClassNotFoundException {
        if (StringUtils.isEmpty(userName)){
            init();
        }
        String date = systemServiceClient.getlastTime();
        jdbcMaterialOutView(userName, password, url , date);
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
        systemServiceClient.materialReceiveSaveBatch(materialReceiveList);
        rs.close();

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
            detailList.add(materialReceiveDetail);
        }
        systemServiceClient.detailSaveBatch(detailList);
        rs2.close();

        stmt.close();
        conn.close();
    }

}
