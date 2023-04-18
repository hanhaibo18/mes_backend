package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.TrackAssembly;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author 王瑞
 * @Description 跟单Mapper
 */
@Mapper
public interface TrackHeadMapper extends BaseMapper<TrackHead> {

    IPage<TrackHead> selectTrackHeadRouter(IPage<TrackHead> page, @Param(Constants.WRAPPER) Wrapper<TrackHead> query);

    IPage<TrackHead> selectTrackHeadCurrentRouter(IPage<TrackHead> page, @Param(Constants.WRAPPER) Wrapper<TrackHead> query);

    /**
     * 功能描述: 根据计划Id查询 所做物品数量
     *
     * @param workPlanId
     * @Author: xinYu.hou
     * @Date: 2022/4/20 15:06
     * @return: Integer
     **/
    @Select("SELECT sum(number) FROM produce_track_head WHERE work_plan_id = #{workPlanId}")
    Integer selectTrackHeadNumber(@Param("workPlanId") String workPlanId);

    /**
     * 功能描述: 根据合格证编号和图号查询展示信息
     *
     * @param page
     * @param queryWrapper
     * @Author: xinYu.hou
     * @Date: 2022/4/25 13:57
     * @return: IPage<IncomingMaterialVO>
     **/
    @Select("SELECT\n" +
            "\thead.id,\n" +
            "\thead.material_certificate_no,\n" +
            "\thead.product_no,\n" +
            "\thead.work_no,\n" +
            "\thead.product_name,\n" +
            "\thead.batch_no,\n" +
            "\thead.drawing_no,\n" +
            "\thead.production_order,\n" +
            "\thead.material_name,\n" +
            "\thead.replace_material,\n" +
            "\thead.test_bar_type,\n" +
            "\thead.test_bar_number,\n" +
            "\thead.weight,\n" +
            "\thead.number,\n" +
            "\tcert.operation,\n" +
            "\tcert.next_opt,\n" +
            "\tcert.branch_code,\n" +
            "\tcert.remark\n" +
            "FROM\n" +
            "\tproduce_track_head head\n" +
            "\tLEFT JOIN produce_certificate cert ON cert.certificate_no = head.material_certificate_no ${ew.customSqlSegment}")
    IPage<IncomingMaterialVO> queryIncomingMaterialPage(IPage<IncomingMaterialVO> page, @Param(Constants.WRAPPER) QueryWrapper<IncomingMaterialVO> queryWrapper);

    /**
     * 功能描述: 查询工作清单
     *
     * @param page
     * @param queryWrapper
     * @Author: xinYu.hou
     * @Date: 2022/5/8 6:27
     * @return: IPage<IncomingMaterialVO>
     **/
    @Select("SELECT\n" +
            "\thead.priority,\n" +
            "\thead.work_no,\n" +
            "\thead.drawing_no,\n" +
            "\thead.product_name,\n" +
            "\thead.number,\n" +
            "\thead.test_bar_type,\n" +
            "\titem.opt_name,\n" +
            "\titem.next_opt_sequence,\n" +
            "\thead.weight,\n" +
            "\thead.track_no \n" +
            "FROM\n" +
            "\tproduce_track_item item\n" +
            "\tLEFT JOIN produce_track_head head ON head.track_no = item.track_head_id ${ew.customSqlSegment}")
    IPage<WorkDetailedListVo> queryWorkDetailedList(IPage<IncomingMaterialVO> page, @Param(Constants.WRAPPER) QueryWrapper<WorkDetailedListVo> queryWrapper);

    /**
     * 功能描述: 查询跟踪列表
     *
     * @param page
     * @param queryWrapper
     * @Author: xinYu.hou
     * @Date: 2022/5/8 7:37
     * @return: IPage<TailAfterVo>
     **/
    @Select("SELECT\n" +
            "\thead.track_no,\n" +
            "\thead.product_name,\n" +
            "\thead.drawing_no,\n" +
            "\thead.product_no,\n" +
            "\thead.work_no,\n" +
            "\thead.material_no,\n" +
            "\thead.number,\n" +
            "\thead.weight,\n" +
            "\thead.texture,\n" +
            "\thead.track_type,\n" +
            "\thead.work_plan_no,\n" +
            "\thead.remark,\n" +
            "\thead.replace_material \n" +
            "FROM\n" +
            "\tproduce_track_head head")
    IPage<TailAfterVo> queryTailAfterList(IPage<QueryTailAfterDto> page, @Param(Constants.WRAPPER) QueryWrapper<TrackHead> queryWrapper);

    List<TrackHead> queryListByCertId(@Param("certId") String certId);

    @Select("")
    IPage<TrackHead> queryBomList(Page<Object> objectPage, List<TrackHead> trackHeadList);

    IPage<TrackHead> selectTrackHeadAndFlow(IPage<TrackHead> page, @Param(Constants.WRAPPER) Wrapper<TrackHead> query);


    /**
     * 功能描述: 跟单入库品总计查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/8/10 15:06
     **/
    @Select("select drawing_no, count(*),sum(number-use_num) as number FROM v_produce_track_store where type = '1' and ${drawNoSql} GROUP BY drawing_no;")
    List<Map> selectTrackStoreCount(String drawNoSql);


    /**
     * 功能描述: 根据id查询跟单
     **/
    @Select("SELECT pth.* ,ppb.project_name FROM produce_track_head pth LEFT JOIN produce_project_bom ppb on ppb.id = pth.project_bom_id WHERE pth.id = #{id}")
    TrackHead selecProjectNametById(@Param("id") String id);


    /**
     * 功能描述: 跟单台账查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/12/29 17:06
     **/
    @Select("<script>" +
            " select * from v_produce_track_store_info where 1=1 " +
//            " <if test='startDate != null and startDate != \"\"'> " +
//            "  and create_time &gt;= #{startDate} " +
//            " </if> " +
//            " <if test='endDate != null and endDate != \"\"'> " +
//            "  and create_time &lt;= #{endDate} " +
//            " </if> " +
            " <if test='productNo != null and productNo != \"\"'> " +
            "  and product_no like concat('%',#{productNo},'%') " +
            " </if> " +
            " <if test='trackNo != null and trackNo != \"\"'> " +
            " and replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like concat('%',#{trackNo},'%') " +
            " </if> " +
            " <if test='workNo != null and workNo != \"\"'> " +
            "  and work_no like concat('%',#{workNo},'%') " +
            " </if> " +
            " <if test='drawingNo != null and drawingNo != \"\"'> " +
            "  and ${drawingNo} " +
            " </if> " +
            " <if test='branchCode != null and branchCode != \"\"'> " +
            "  and branch_code = #{branchCode} " +
            " </if> " +
            " <if test='tenantId != null and tenantId != \"\"'> " +
            "  and tenant_id = #{tenantId} " +
            " </if> " +
            "</script>")
    List<TrackHead> selectTrackHeadAccount(TeackHeadDto trackHead);

    /**
     * 功能描述: 根据跟单id查询跟单返回DTO对象
     *
     * @param trackHeadId
     * @Author: xinYu.hou
     * @Date: 2023/2/20 15:47
     * @return: TrackHeadMoldDto
     **/
    @Select("SELECT * FROM produce_track_head WHERE id = #{trackHeadId}")
    TrackHeadPublicDto queryDtoById(@Param("trackHeadId") String trackHeadId);

    /**
     * 功能描述:视图分页查询
     *
     * @param page
     * @param query
     * @Author: xinYu.hou
     * @Date: 2023/2/22 16:19
     * @return: IPage<TrackHeadPublicDto>
     **/
    @Select("SELECT * FROM v_produce_track_head ${ew.customSqlSegment}")
    IPage<TrackHeadPublicVo> queryPage(IPage<TrackHead> page, @Param(Constants.WRAPPER) Wrapper<TrackHead> query);

    /**
     * 功能描述: 修改顺序错误问题
     *
     * @Author: xinYu.hou
     * @Date: 2023/4/13 17:27
     * @return: List<String>
     **/
    @Select("SELECT track_head_id from produce_track_item WHERE original_opt_sequence + 10 <> next_opt_sequence AND next_opt_sequence <> 0 AND tenant_id like '%002%' GROUP BY track_head_id")
    List<String> queryTrackId();

    @Select("SELECT DISTINCT(pth.id) FROM produce_track_assembly pta JOIN produce_track_head pth ON pta.track_head_id = pth.id AND pth.tenant_id = '12345678901234567890123456789002' AND pth.classes = 2 WHERE pth.project_bom_id IS NULL")
    List<String> selectIdWithoutProjectBom();

    @Select("SELECT * from produce_track_head where id in (${ids})")
    List<TrackHead> selectByIds(@Param("ids") String ids);

    @Select("SELECT pta.*,pth.work_no work_no,pth.product_name product_name  FROM produce_track_head pth JOIN produce_track_assembly pta ON pth.Id = pta.track_head_id and pth.tenant_id = '12345678901234567890123456789002' AND pth.classes = 2 and pth.id in (${ids})")
    List<TrackAssembly> selectAssemblyByTrackHeadIds(@Param("ids") String ids);
}
