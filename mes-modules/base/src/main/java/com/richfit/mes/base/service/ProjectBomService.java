package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.ProjectBom;

import java.util.List;

/**
 * @author 侯欣雨
 * @Description 项目BOM服务
 */
public interface ProjectBomService extends IService<ProjectBom> {

    /**
     * 功能描述: 删除BOM
     *
     * @param workPlanNo 工作号
     * @param tenantId   租户
     * @param branchCode 公司
     * @Author: xinYu.hou
     * @return: boolean
     **/
    boolean deleteBom(String workPlanNo, String tenantId, String branchCode);

    /**
     * 功能描述: 修改BOM
     *
     * @param projectBom 项目BOM对象
     * @Author: xinYu.hou
     * @return: boolean
     **/
    boolean updateBom(ProjectBom projectBom);


    /**
     * 功能描述: 分页查询列表
     *
     * @param drawingNo   图号
     * @param projectName 项目名称
     * @param prodDesc    零部件名称
     * @param state       状态
     * @param tenantId    租户
     * @param branchCode  公司
     * @param order       排序方式
     * @param orderCol    排序字段
     * @param page        页码
     * @param limit       数量
     * @Author: xinYu.hou
     * @Date: 2022/5/31 0:38
     * @return: IPage<ProjectBom>
     **/
    IPage<ProjectBom> getProjectBomPage(String drawingNo, String projectName, String prodDesc, String state, String tenantId, String branchCode, String order, String orderCol, int page, int limit);

    /**
     * 功能描述:
     *
     * @param drawingNo  图号
     * @param tenantId   租户Id
     * @param branchCode 车间
     * @Author: xinYu.hou
     * @Date: 2022/6/1 7:51
     * @return: List<ProjectBom>
     **/
    List<ProjectBom> getProjectBomList(String drawingNo, String tenantId, String branchCode);

    //TODO: 零件增删改查接口,提供对外的根据项目BOM查询条件 查询所有零件

    /**
     * 功能描述: 根据工作号查询项目BOM零件
     *
     * @param workPlanNo
     * @param tenantId
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/6/2 7:25
     * @return: List<ProjectBom>
     **/
    List<ProjectBom> getProjectBomPartList(String workPlanNo, String tenantId, String branchCode);

    /**
     * 功能描述:提供给第三方查询 根据ID查询
     *
     * @param id
     * @param tenantId
     * @param branchCode
     * @Author: xinYu.hou
     * @return: List<ProjectBom>
     **/
    List<ProjectBom> getProjectBomPartByIdList(String id, String tenantId, String branchCode);

    /**
     * 功能描述: 根据项目名称和工作号查询 项目BOM
     *
     * @param workPlanNo
     * @param projectName
     * @param tenantId
     * @param branchCode
     * @Author: xinYu.hou
     * @return: List<ProjectBom>
     **/
    List<ProjectBom> getPartList(String workPlanNo, String projectName, String tenantId, String branchCode);

    /**
     * 功能描述: 删除零件BOM
     *
     * @param id
     * @Author: xinYu.hou
     * @return: boolean
     **/
    boolean deletePart(String id);

    /**
     * 功能描述: 新增零件BOM
     *
     * @param projectBom
     * @Author: xinYu.hou
     * @return: boolean
     **/
    boolean saveBom(ProjectBom projectBom);
}
