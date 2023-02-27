package com.richfit.mes.base.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.ProductMapper;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 物料服务
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductService productService;

    @Override
    public IPage<Product> selectProduct(Page<Product> page, QueryWrapper<Product> query) {
        return productMapper.selectProduct(page, query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importMaterialByExcle(MultipartFile file, String tenantId, String branchCode) {
        //封装证件信息实体类
        java.lang.reflect.Field[] fields = Product.class.getDeclaredFields();
        //封装证件信息实体类
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<Product> list = ExcelUtils.importExcel(excelFile, Product.class, fieldNames, 1, 0, 0, tempName.toString());
            FileUtils.delete(excelFile);

            List<String> materialNoListIn = list.stream().map(Product::getMaterialNo).collect(Collectors.toList());
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tenant_id", tenantId);
            queryWrapper.eq("branch_code", branchCode);
            List<Product> productList = productMapper.selectList(queryWrapper);
            List<String> materialNoList = productList.stream().map(Product::getMaterialNo).collect(Collectors.toList());
            materialNoList.retainAll(materialNoListIn);
            if (!materialNoList.isEmpty()) {
                return "以下物料编码已存在:" + JSONObject.toJSONString(materialNoList);
            }
            boolean result = productService.saveBatch(list);
            if (!result){
                throw new GlobalException("excel批量导入失败",ResultCode.FAILED);
            }


        } catch (Exception e) {
            log.error("excel导入失败",e);
            return "failed";
        }
        return "success";
    }
}
