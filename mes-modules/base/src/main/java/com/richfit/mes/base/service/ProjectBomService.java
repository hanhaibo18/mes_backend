package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProjectBom;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * @param drawingNo  图号
     * @Author: xinYu.hou
     * @return: boolean
     **/
    boolean deleteBom(String id, String workPlanNo, String tenantId, String branchCode, String drawingNo);

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
    IPage<ProjectBom> getProjectBomPage(String drawingNo, String projectName, String prodDesc, String workPlanNo, String state, String tenantId, String branchCode, String order, String orderCol, String publishState, int page, int limit);

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


    /**
     * 功能描述: 根据工作号查询项目BOM零件
     *
     * @param workPlanNo
     * @param tenantId
     * @param branchCode
     * @param drawingNo
     * @Author: xinYu.hou
     * @Date: 2022/6/2 7:25
     * @return: List<ProjectBom>
     **/
    List<ProjectBom> getProjectBomPartList(String workPlanNo, String drawingNo, String tenantId, String branchCode);

    /**
     * 功能描述:提供给第三方查询 根据ID查询
     *
     * @param id
     * @Author: xinYu.hou
     * @return: List<ProjectBom>
     **/
    List<ProjectBom> getProjectBomPartByIdList(String id);

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

    /**
     * 功能描述:
     *
     * @param partId 零件ID
     * @param bomId  项目Id
     * @Author: xinYu.hou
     * @Date: 2022/6/7 16:03
     * @return: boolean
     **/
    boolean relevancePart(String partId, String bomId);

    /**
     * 功能描述: 根据零件Id获取名称
     *
     * @param partId
     * @Author: xinYu.hou
     * @Date: 2022/6/8 14:53
     * @return: Map<String, String>
     **/
    CommonResult<Map<String, String>> getPartName(String partId);

    /**
     * 功能描述: 查询并返回能生成编号的对象
     *
     * @param workPlanNo
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/7/21 18:33
     * @return: Boolean
     **/
    ProjectBom queryBom(String workPlanNo, String branchCode);

    /**
     * 功能描述: 导出BOM
     *
     * @param idList
     * @param rsp
     * @Author: xinYu.hou
     * @Date: 2022/7/21 9:58
     * @return: void
     **/
    void exportExcel(List<String> idList, HttpServletResponse rsp) throws IOException;

    Boolean publishBom(List<String> ids, Integer publishState);
}
