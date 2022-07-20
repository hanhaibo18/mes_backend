package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.base.OperationDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * @author 王瑞
 * @Description 工艺设备关联Mapper
 */
@Mapper
public interface OperationDeviceMapper extends BaseMapper<OperationDevice> {
    /**
     * 功能描述: 通过物料号码查询物料库存合计数量
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/19 11:37
     **/
    @SelectProvider(type = OperationDeviceMapper.class, method = "operationDevice")
    List<Map> selectTotalNum(@Param("deviceId") String deviceId, @Param("optName") String optName, @Param("optType") String optType);


    default String operationDevice(Map<String, String> params) {
        String where = "";
        if (!StringUtils.isNullOrEmpty(params.get("deviceId"))) {
            where += "device_id = " + params.get("deviceId");
        }
        return new SQL()
                .SELECT("*")
                .FROM("v_operation_device")
                .WHERE(where)
                .toString();
    }
}
