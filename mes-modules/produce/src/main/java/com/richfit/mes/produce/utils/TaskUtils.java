package com.richfit.mes.produce.utils;

import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.MaterialReceiveDetailService;
import com.richfit.mes.produce.service.MaterialReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    private final String userNameKey = "MaterialOutView-userName";
    private final String passwordKey = "MaterialOutView-passowrd";


    private String userName = "";
    private String password = "";

    @Autowired
    MaterialReceiveService materialReceiveService;

    @Autowired
    SystemServiceClient systemServiceClient;


    @Resource
    MaterialReceiveDetailService materialReceiveDetailService;

    public void init(){
        userName = systemServiceClient.findItemParamByCode(userNameKey).getData().getLabel();
        password = systemServiceClient.findItemParamByCode(passwordKey).getData().getLabel();
    }


    // 添加定时任务
    @Scheduled(cron = "0 24 17 * * ?") // cron 表达式，每10分 执行
    public void doTask() throws SQLException, ClassNotFoundException {


        Date date = materialReceiveService.getlastTime();
        List<MaterialReceive> materialReceiveList = jdbcQuickstart(userName, password, String.valueOf(date));
        List<MaterialReceiveDetail> receiveDetails = new ArrayList<>();
        materialReceiveList.forEach(i -> {
            receiveDetails.addAll(i.getReceiveDetails());
        });
        materialReceiveService.saveBatch(materialReceiveList);
        materialReceiveDetailService.saveBatch(receiveDetails);
    }


    public List<MaterialReceive> jdbcQuickstart(String userName, String password, String time) throws ClassNotFoundException, SQLException {
        // 1、注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 2、获取数据库连接对象
        Connection conn = DriverManager.getConnection("jdbc:mysql://11.11.209.206:8001/bsj?serverTimezone=UTC&&user="+userName+"&&password="+password);
        // 3、定义sql
        String sql = "select * from v_mes_out_headers where CREATE_TIME > "+time;
        // 4、获取执行sql的对象
        Statement stmt = conn.createStatement();
        // 5、执行sql
        ResultSet rs = stmt.executeQuery(sql);
        // 6、处理结果
        List<MaterialReceive> materialReceiveList = new ArrayList<>();
        while (rs.next()){
            // 获取数据 两种方式 根据列的索引获取 根据列名获取
            String outNum = rs.getString("OUT_NUM");
            String aplyNum = rs.getString("APLY_NUM");
            String createTime = rs.getString("CREATE_TIME");
            MaterialReceive materialReceive = new MaterialReceive();
            materialReceive.setDelieveryNo(outNum);
            materialReceive.setAplyNum(aplyNum);
            materialReceive.setOutboundDate(createTime);
            materialReceiveList.add(materialReceive);
        }
        materialReceiveList.forEach(i ->{
            String sql2 = "select * from v_mes_out_lines  where OUT_NUM = " + i.getDelieveryNo() +" AND APLY_NUM =" + i.getAplyNum();
            try {
                ResultSet rs2 = stmt.executeQuery(sql2);
                List<MaterialReceiveDetail> detailList = new ArrayList<>();
                while (rs2.next()){
                    // 获取数据 两种方式 根据列的索引获取 根据列名获取
                    String outNum = rs2.getString("OUT_NUM");
                    String aplyNum = rs2.getString("APLY_NUM");
                    String materialNum = rs2.getString("MATERIAL_NUM");
                    String name = rs2.getString("MATERIAL_DESC");
                    String batchNum = rs2.getString("BATCH_NUM");
                    String orderQuantity = rs2.getString("ORDER_QUANTITY");
                    String quantity = rs2.getString("QUANTITY");
                    String unit = rs2.getString("UNIT");

                    MaterialReceiveDetail materialReceiveDetail = new MaterialReceiveDetail();
                    materialReceiveDetail.setDelieveryNo(outNum);
                    materialReceiveDetail.setAplyNum(aplyNum);
                    materialReceiveDetail.setMaterialNum(materialNum);
                    materialReceiveDetail.setName(name);
                    materialReceiveDetail.setBatchNum(batchNum);
                    materialReceiveDetail.setOrderQuantity(orderQuantity);
                    materialReceiveDetail.setQuantity(quantity);
                    materialReceiveDetail.setUnit(unit);
                    detailList.add(materialReceiveDetail);
                    i.setReceiveDetails(detailList);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        // 7、关闭资源
        rs.close();
        stmt.close();
        conn.close();

        return  materialReceiveList;
    }
}
