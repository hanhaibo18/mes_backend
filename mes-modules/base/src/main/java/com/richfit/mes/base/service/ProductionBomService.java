package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProductionBom;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author 王瑞
 * @Description 产品BOM服务
 */
public interface ProductionBomService extends IService<ProductionBom> {

    IPage<ProductionBom> getProductionBomByPage(Page<ProductionBom> page, QueryWrapper<ProductionBom> query);

    IPage<ProductionBom> getProductionBomHistory(Page<ProductionBom> page, QueryWrapper<ProductionBom> query);

    boolean saveByList(List<ProductionBom> list);

    List<ProductionBom> getProductionBomList(@Param(Constants.WRAPPER) Wrapper<ProductionBom> query);

    boolean updateStatus(ProductionBom bom);

    /**
     * 功能描述: 查询零件列表
     *
     * @param drawingNo  图号
     * @param tenantId   租户
     * @param branchCode 公司
     * @Author: xinYu.hou
     * @return: List<ProductionBom>
     **/
    List<ProductionBom> getProductionBomByDrawingNoList(String drawingNo, String tenantId, String branchCode);

    /**
     * 功能描述: 发布BOM
     *
     * @param id          BOM_ID
     * @param workPlanNo  工作号
     * @param projectName 项目名称
     * @param tenantId    租户
     * @param branchCode  公司
     * @Author: xinYu.hou
     * @return: boolean
     **/
    CommonResult<Boolean> issueBom(String id, String workPlanNo, String projectName, String tenantId, String branchCode);


    /**
     * 功能描述: 删除BOM
     *
     * @param drawingNo  图号
     * @param tenantId   租户
     * @param branchCode 公司
     * @Author: xinYu.hou
     * @return: boolean
     **/
    boolean deleteBom(String drawingNo, String tenantId, String branchCode);

    /**
     * 功能描述: 修改BOM
     *
     * @param productionBom 产品BOM对象
     * @Author: xinYu.hou
     * @Date: 2022/5/30 3:33
     * @return: boolean
     **/
    boolean updateBom(ProductionBom productionBom);

    /**
     * 功能描述: 分页查询列表
     *
     * @param drawingNo  图号
     * @param tenantId   租户
     * @param branchCode 公司
     * @param order      排序方式
     * @param orderCol   排序字段
     * @param page       页码
     * @param limit      数量
     * @Author: xinYu.hou
     * @Date: 2022/5/31 0:38
     * @return: IPage<ProductionBom>
     **/
    IPage<ProductionBom> getProductionBomPage(String drawingNo, String tenantId, String branchCode, String order, String orderCol, int page, int limit);

    /**
     * 功能描述: 导出BOM
     *
     * @param idList
     * @param rsp
     * @Author: xinYu.hou
     * @Date: 2022/7/22 10:39
     * @return: void
     **/
    void exportExcel(List<String> idList, HttpServletResponse rsp);

    CommonResult newImportExcel(@RequestParam("file") MultipartFile file, String branchCode) throws IOException;

    /**
     * 功能描述: ERP导出BOM
     *
     * @param id
     * @param rsp
     * @Author: renzewen
     * @Date: 2022/8/18 10:39
     * @return: void
     **/
    void exportExcelERP(String id, HttpServletResponse rsp);
}
