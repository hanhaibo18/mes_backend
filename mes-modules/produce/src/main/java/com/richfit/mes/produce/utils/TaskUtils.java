package com.richfit.mes.produce.utils;

import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.annotation.Inner;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.MaterialReceiveDetailService;
import com.richfit.mes.produce.service.MaterialReceiveService;
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

    @Autowired
    MaterialReceiveService materialReceiveService;

    @Resource
    SystemServiceClient systemServiceClient;


    @Resource
    MaterialReceiveDetailService materialReceiveDetailService;

    public void init(){
        List<ItemParam> list = systemServiceClient.selectItemClass(code,"",SecurityConstants.FROM_INNER).getData();
        password = list.get(0).getLabel();
        userName = list.get(1).getLabel();
    }


    // 添加定时任务
    @Scheduled(cron = "0 0/10 * * * ?") // cron 表达式，每10分 执行
    @Inner
    public void doTask() throws SQLException, ClassNotFoundException {
        if (StringUtils.isEmpty(userName)){
            init();
        }
        Date date = materialReceiveService.getlastTime();
        List<MaterialReceive> materialReceiveList = jdbcQuickstart(userName, password, date);
        List<MaterialReceiveDetail> receiveDetails = jdbcQuickstart2(userName, password, date);
        materialReceiveService.saveBatch(materialReceiveList);
        materialReceiveDetailService.saveBatch(receiveDetails);
    }

    public void test() throws SQLException, ClassNotFoundException {
        if (StringUtils.isEmpty(userName)){
            init();
        }
        Date date = materialReceiveService.getlastTime();
        List<MaterialReceive> materialReceiveList = jdbcQuickstart(userName, password, date);
        List<MaterialReceiveDetail> receiveDetails = new ArrayList<>();
        materialReceiveList.forEach(i -> {
            receiveDetails.addAll(i.getReceiveDetails());
        });
        materialReceiveService.saveBatch(materialReceiveList);
        materialReceiveDetailService.saveBatch(receiveDetails);
    }


    public List<MaterialReceive> jdbcQuickstart(String userName, String password, Date time) throws ClassNotFoundException, SQLException {
        List<MaterialReceive> materialReceiveList = new ArrayList<>();
        // 1、注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 2、获取数据库连接对象
        Connection conn = DriverManager.getConnection("jdbc:mysql://11.11.209.206:8001/bsj?serverTimezone=UTC&&user="+userName+"&&password="+password);
        // 3、定义sql
        String sql = null;
        if (StringUtils.isEmpty(time)){
            sql = "select * from v_mes_out_headers";
        } else {
            sql = "select * from v_mes_out_headers where CREATE_TIME > "+time;
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
        // 7、关闭资源
        rs.close();
        stmt.close();
        conn.close();

        return  materialReceiveList;
    }

    public List<MaterialReceiveDetail> jdbcQuickstart2(String userName, String password, Date time) throws ClassNotFoundException, SQLException {
        // 1、注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 2、获取数据库连接对象
        Connection conn = DriverManager.getConnection("jdbc:mysql://11.11.209.206:8001/bsj?serverTimezone=UTC&&user=" + userName + "&&password=" + password);
        // 3、定义sql
        String sql = null;
        if (StringUtils.isEmpty(time)) {
            sql = "select voh.OUT_NUM,voh.APLY_NUM,voh.CREATE_TIME,voh.WORK_CODE,vol.MATERIAL_NUM,vol.MATERIAL_DESC,vol.BATCH_NUM,vol.ORDER_QUANTITY,vol.QUANTITY,vol.UNIT from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM";
        } else {
            sql = "select voh.OUT_NUM,voh.APLY_NUM,voh.CREATE_TIME,voh.WORK_CODE,vol.MATERIAL_NUM,vol.MATERIAL_DESC,vol.BATCH_NUM,vol.ORDER_QUANTITY,vol.QUANTITY,vol.UNIT from v_mes_out_lines vol LEFT JOIN v_mes_out_headers  voh ON  vol.APLY_NUM = voh.APLY_NUM WHERE voh.CREATE_TIME >" + time;
        }
        // 4、获取执行sql的对象
        Statement stmt = conn.createStatement();
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
            String orderQuantity = rs2.getString("ORDER_QUANTITY");
            String quantity = rs2.getString("QUANTITY");
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
        // 7、关闭资源
        rs2.close();
        stmt.close();
        conn.close();

        return detailList;
    }
}
