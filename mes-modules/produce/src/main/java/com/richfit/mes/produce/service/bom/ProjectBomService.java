package com.richfit.mes.produce.service.bom;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.produce.bom.ProduceProjectBom;

import java.util.List;

/**
 * @author zhiqiang.lu
 * @Description 项目BOM服务
 */
public interface ProjectBomService extends IService<ProduceProjectBom> {
    List<ProduceProjectBom> getProjectBomList(String workPlanNo, String drawingNo, String tenantId, String branchCode);

    List<ProduceProjectBom> getProjectBomPartList(String workPlanNo, String drawingNo, String tenantId, String branchCode);

    /**
     * 功能描述:根据ID查询零件
     *
     * @param id
     * @Author: zhiqiang.lu
     * @return: List<ProjectBom>
     **/
    List<ProduceProjectBom> getProjectBomPartByIdList(String id);
}
