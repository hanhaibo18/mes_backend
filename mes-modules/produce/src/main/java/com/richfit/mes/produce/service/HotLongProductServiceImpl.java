package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.produce.HotLongProduct;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.HotLongProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: 张盘石
 * @Date: 2022/11/08
 */
@Slf4j
@Service
public class HotLongProductServiceImpl extends ServiceImpl<HotLongProductMapper, HotLongProduct> implements HotLongProductService {

    @Override
    public CommonResult importExcel(MultipartFile file, String tenantId) {
        String[] fieldNames = {"productName", "productDrawingNo", "version"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<HotLongProduct> list = ExcelUtils.importExcel(excelFile, HotLongProduct.class, fieldNames, 1, 0, 0, tempName.toString());
            //校验数据
            String message = checkExportInfo(list);
            if (!StringUtils.isNullOrEmpty(message)) {
                return CommonResult.failed("导入失败原因如下：</br>" + message);
            }
            List<HotLongProduct> hotLongProducts = new ArrayList<>();
            for (HotLongProduct hotLongProduct : list) {
                HotLongProduct hotLongProductcopy = new HotLongProduct();
                BeanUtils.copyProperties(hotLongProduct, hotLongProductcopy);

                hotLongProductcopy.setTenantId(tenantId);
                hotLongProductcopy.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                hotLongProductcopy.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                hotLongProductcopy.setCreateTime(new Date());
                hotLongProductcopy.setModifyTime(new Date());
                hotLongProducts.add(hotLongProductcopy);
            }
            boolean t = this.saveBatch(hotLongProducts);
            if (t) {
                return CommonResult.success("导入成功");
            } else {
                return CommonResult.success("导入失败");
            }
        } catch (Exception e) {
            return CommonResult.failed();
        }
    }

    private String checkExportInfo(List<HotLongProduct> list) {
        StringBuilder message = new StringBuilder();
        for (HotLongProduct hotLongProduct : list) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(hotLongProduct.getProductDrawingNo())) {
                return message.append("图号不能为空</br>").toString();
            }

//            if (org.apache.commons.lang3.StringUtils.isEmpty(hotLongProduct.getVersion())) {
//                return message.append("版本号不能为空</br>").toString();
//            }
            QueryWrapper<HotLongProduct> hotLongProductQueryWrapper = new QueryWrapper<>();
            hotLongProductQueryWrapper.eq("product_drawing_no", hotLongProduct.getProductDrawingNo());
           // hotLongProductQueryWrapper.eq("version", hotLongProduct.getVersion());
            List<HotLongProduct> list1 = this.list(hotLongProductQueryWrapper);
            if (CollectionUtils.isNotEmpty(list1)) {
//                message.append("图号：" + hotLongProduct.getProductDrawingNo() + "版本号为：" + hotLongProduct.getVersion() + "在系统中存在重复数据，无法插入</br>");
                message.append("图号：" + hotLongProduct.getProductDrawingNo() + "在系统中存在重复数据，无法插入</br>");
            }
        }
        return message.toString();
    }
}
