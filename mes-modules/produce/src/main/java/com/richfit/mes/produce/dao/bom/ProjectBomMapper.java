package com.richfit.mes.produce.dao.bom;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.bom.ProduceProjectBom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 王瑞
 * @Description 产品BOM Mapper
 */
@Mapper
public interface ProjectBomMapper extends BaseMapper<ProduceProjectBom> {

    @Select("select * from base_project_bom where tenant_id = '${tenantId}' and branch_code = '${branchCode}' and drawing_no = '${drawingNo}' and work_plan_no = '${workNo}' and grade = 'H' limit 1")
    ProjectBom selectBomByDrawNoAndWorkNo(@Param("drawingNo") String drawingNo, @Param("workNo") String workNo, @Param("tenantId") String tenantId, @Param("branchCode") String branchCode);
}







