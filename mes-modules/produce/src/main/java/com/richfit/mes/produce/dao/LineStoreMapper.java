package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.store.LineStoreSum;
import com.richfit.mes.common.model.produce.store.LineStoreSumZp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author 王瑞
 * @Description 线边库Mapper
 */
@Mapper
public interface LineStoreMapper extends BaseMapper<LineStore> {

    IPage<LineStoreSum> selectGroup(IPage<LineStore> page, @Param(Constants.WRAPPER) Wrapper<LineStore> query);

    IPage<LineStore> selectLineStoreByProduce(IPage<LineStore> page, @Param(Constants.WRAPPER) Wrapper<LineStore> query);

    List<LineStoreSumZp> selectStoreNumForAssembly(@Param("param") Map parMap);

    List<LineStoreSumZp> selectDeliveryNumber(@Param("param") Map parMap);

    List<LineStoreSumZp> selectRequireNum(@Param("param") Map parMap);

    List<LineStoreSumZp> selectAssemblyNum(@Param("param") Map parMap);

    /**
     * 功能描述: 通过物料号码查询物料库存合计数量
     *
     * @param materialNo 物料号码
     * @Author: zhiqiang.lu
     * @Date: 2022/7/11 11:37
     **/
    @Select("select sum(number-use_num) as total from produce_line_store where material_no=#{materialNo} and branch_code=#{branchCode} and tenant_id = #{tenantId} and status='0'")
    Integer selectTotalNum(@Param("materialNo") String materialNo, @Param("branchCode") String branchCode, @Param("tenantId") String tenantId);
}
