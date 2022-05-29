package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.ProductionBom;
import org.apache.ibatis.annotations.Param;

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
    boolean issueBom(String id, String workPlanNo, String projectName, String tenantId, String branchCode);


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
}
