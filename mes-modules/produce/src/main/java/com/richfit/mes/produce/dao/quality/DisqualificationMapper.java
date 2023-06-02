package com.richfit.mes.produce.dao.quality;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;
import com.richfit.mes.produce.entity.quality.DisqualificationResultVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: xinYu.hou
 * @Date: 2022/9/29 15:09
 **/
@Mapper
public interface DisqualificationMapper extends BaseMapper<Disqualification> {

    /**
     * 功能描述: 查询
     *
     * @param tiId
     * @Author: xinYu.hou
     * @Date: 2022/10/17 9:53
     * @return: DisqualificationItemVo
     **/
    @Select("SELECT * FROM produce_disqualification dis WHERE dis.track_item_id = #{tiId}")
    DisqualificationItemVo queryDisqualificationByItemId(String tiId);

    @Select("SELECT dis.* FROM produce_disqualification dis LEFT JOIN produce_disqualification_final_result final ON dis.id = final.id ${ew.customSqlSegment}")
    IPage<Disqualification> query(@Param("page") Page page, @Param(Constants.WRAPPER) Wrapper<Disqualification> wrapper);

    @Select("SELECT \n" +
            "\tdis.create_time,\n" +
            "\tdis.branch_code,\n" +
            "\tdis.process_sheet_no,\n" +
            "\tdis.work_no,\n" +
            "\tdis.product_name,\n" +
            "\tdis.part_name,\n" +
            "\tdis.part_drawing_no,\n" +
            "\tdis.product_no,\n" +
            "\tdis.part_materials,\n" +
            "\tdis.number,\n" +
            "\tdis.track_no,\n" +
            "\tdis.track_head_type,\n" +
            "\tdis.heat_number,\n" +
            "\tdis.quality_check_by,\n" +
            "\tdis.disqualification_type,\n" +
            "\tdis.close_time,\n" +
            "\tfinal.disqualification_condition,\n" +
            "\tfinal.discover_tenant, \n" +
            "\tfinal.disqualification_name, \n" +
            "\tfinal.discover_branch, \n" +
            "\tfinal.unit_responsibility_within, \n" +
            "\tfinal.unit_responsibility_outside, \n" +
            "\tfinal.total_weight, \n" +
            "\tfinal.quality_name, \n" +
            "\tfinal.unit_treatment_one, \n" +
            "\tfinal.unit_treatment_two, \n" +
            "\tfinal.discover_item, \n" +
            "\tfinal.discard_time,\n" +
            "\tfinal.reuse_time, \n" +
            "\tfinal.accept_deviation, \n" +
            "\tfinal.repair_qualified, \n" +
            "\tfinal.scrap, \n" +
            "\tfinal.sales_return_loss, \n" +
            "\tfinal.treatment_one_name,\n" +
            "\tfinal.treatment_two_name,\n" +
            "\tfinal.responsibility_name, \n" +
            "\tfinal.technology_name, \n" +
            "\tfinal.sales_return,\n" +
            "\tfinal.quality_control_opinion,\n" +
            "\tfinal.unit_treatment_one_opinion,\n" +
            "\tfinal.unit_treatment_two_opinion,\n" +
            "\tfinal.responsibility_opinion\n" +
            "\tFROM \n" +
            "\tproduce_disqualification dis\n" +
            "\tLEFT JOIN produce_disqualification_final_result final ON dis.id = final.id ${ew.customSqlSegment}")
    List<DisqualificationResultVo> queryDisqualificationResult(@Param(Constants.WRAPPER) Wrapper<DisqualificationResultVo> wrapper);

}
