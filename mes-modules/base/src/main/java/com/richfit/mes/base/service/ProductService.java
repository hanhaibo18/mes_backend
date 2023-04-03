package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 王瑞
 * @Description 物料服务
 */
public interface ProductService extends IService<Product> {

    IPage<Product> selectProduct(Page<Product> page, QueryWrapper<Product> query);

    String importMaterialByExcle(MultipartFile file, String tenantId, String branchCode);

    /**
     * 物料管理  导入Excel文件
     * @param file
     * @return
     */
    CommonResult importMaterialExcel(MultipartFile file);

    /**
     * 同步选中物料数据物料到wms
     * @param ids
     * @return
     */
    CommonResult<Boolean> saveWmsSync(List<String> ids);
}
