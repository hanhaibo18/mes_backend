package com.kld.mes.erp.service;

import com.kld.mes.erp.utils.ColumnsConfig;
import com.kld.mes.erp.utils.SqlServerConnect;
import com.richfit.mes.common.model.produce.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: mes-backend
 * @description: ERP订单服务实现类
 * @author: fengxy
 * @create: 2022年9月16日11:39:48
 **/
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private SqlServerConnect sqlServerConnect;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Override
    public List<Order> getErpCode(String erpCode, String selectDate, String controller, String orderNo) {

        try {
            List<Order> orders = new ArrayList<>();
            //查询SQL
            String sql = "select " + ColumnsConfig.orderSync + " from ERPMOCTA where TA003  >= '" + selectDate.replaceAll("-","") + "'";
            //sqlserver结果返回
            List<Map<String, Object>> maps = sqlServerConnect.executeQuery(sql, ColumnsConfig.orderSync);

            for (Map<String, Object> map : maps) {
                //公司
                String company = MapUtils.getString(map, "COMPANY");
                //工单单别
                String ta001 = MapUtils.getString(map,"TA001");
                //工单单号
                String ta002 = MapUtils.getString(map,"TA002");
                //开单日期
                String ta003 = MapUtils.getString(map,"TA003");
                //产品品号
                String ta006 = MapUtils.getString(map,"TA006");
                //预计产量
                String ta015 = MapUtils.getString(map,"TA015");
                //审核者
                String ta041 = MapUtils.getString(map,"TA041");
                //预计开工
                String ta009 = MapUtils.getString(map,"TA009");
                //预计完工
                String ta010 = MapUtils.getString(map,"TA010");
                //品名
                String mb002 = MapUtils.getString(map,"MB002");

                Order p = new Order();
                p.setInChargeOrg(company);
                p.setMaterialCode(ta006);
                p.setOrderSn(ta001 + "-" + ta002);
                p.setController(ta041);
                if (StringUtils.isNotBlank(ta015))
                    p.setOrderNum((int) Double.parseDouble(ta015));
                if (StringUtils.isNotBlank(ta009)) {
                    p.setStartTime(sdf.parse(ta009));
                }
                if (StringUtils.isNotBlank(ta010)) {
                    p.setEndTime(sdf.parse(ta010));
                }
                p.setMaterialDesc(mb002);
                orders.add(p);
            }
            log.info("ERP工单查询成功------");
            return orders;
        } catch (Exception e) {
            log.error("工单接口异常: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
