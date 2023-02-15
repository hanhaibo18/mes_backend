package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.produce.HotModelStore;
import com.richfit.mes.common.model.produce.HotModelStoreExportExcelVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.HotModelStoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class HotModelStoreServiceImpl extends ServiceImpl<HotModelStoreMapper, HotModelStore> implements HotModelStoreService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult importExcel(MultipartFile file, String tenantId) {
        String[] fieldNames = {"modelName", "modelType", "normalNum", "modelDrawingNo", "locationNo", "scrapNum", "modelRemark","version"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<HotModelStoreExportExcelVo> list = ExcelUtils.importExcel(excelFile, HotModelStoreExportExcelVo.class, fieldNames, 1, 0, 0, tempName.toString());
            //校验数据
            String message = checkExportInfo(list);
            if (!StringUtils.isNullOrEmpty(message)) {
                return CommonResult.failed("导入失败原因如下：</br>" + message);
            }
            List<HotModelStore> hotModelStores = new ArrayList<>();
            for (HotModelStoreExportExcelVo hotModelStoreExportExcelVo : list) {
                HotModelStore hotModelStore = new HotModelStore();
                BeanUtils.copyProperties(hotModelStoreExportExcelVo, hotModelStore);

                if (hotModelStoreExportExcelVo.getModelType() != null) {
                    switch (hotModelStoreExportExcelVo.getModelType()) {
                        case "一次性":
                            hotModelStore.setModelType(0);
                            break;
                        case "重复性":
                            hotModelStore.setModelType(1);
                            break;
                        default:
                            hotModelStore.setModelType(null);
                            break;
                    }
                }
                String normalNum = hotModelStoreExportExcelVo.getNormalNum();
                String scrapNum = hotModelStoreExportExcelVo.getScrapNum();
                hotModelStore.setTenantId(tenantId);
                hotModelStore.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                hotModelStore.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                hotModelStore.setCreateTime(new Date());
                hotModelStore.setModifyTime(new Date());
                try {
                    Integer normalNumber = Integer.valueOf(normalNum);
                    Integer scrapNumber = Integer.valueOf(scrapNum);
                    hotModelStore.setNormalNum(normalNumber);
                    hotModelStore.setScrapNum(scrapNumber);
                    hotModelStores.add(hotModelStore);
                } catch (NumberFormatException e) {
                    hotModelStores.add(hotModelStore);
                    continue;
                }
            }
            boolean t = this.saveBatch(hotModelStores);
            if (t) {
                return CommonResult.success("导入成功");
            } else {
                return CommonResult.success("导入失败");
            }
        } catch (Exception e) {
            return CommonResult.failed();
        }
    }

    private String checkExportInfo(List<HotModelStoreExportExcelVo> list) {
        StringBuilder message = new StringBuilder();
        for (HotModelStoreExportExcelVo hotModelStore : list) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(hotModelStore.getModelDrawingNo())) {
                return message.append("模型图号不能为空</br>").toString();
            }
            QueryWrapper<HotModelStore> hotModelStoreQueryWrapper = new QueryWrapper<>();
            hotModelStoreQueryWrapper.eq("model_drawing_no", hotModelStore.getModelDrawingNo());
            hotModelStoreQueryWrapper.eq("version",hotModelStore.getVersion());
            //根据图号和版本查重
            List<HotModelStore> list1 = this.list(hotModelStoreQueryWrapper);
            if (CollectionUtils.isNotEmpty(list1)) {
                message.append("图号：" + hotModelStore.getModelDrawingNo() + "在系统中存在重复数据，无法插入</br>");
            }
        }
        return message.toString();
    }
}
