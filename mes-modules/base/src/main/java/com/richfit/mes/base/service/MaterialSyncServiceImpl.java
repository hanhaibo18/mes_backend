package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.ProductMapper;
import com.richfit.mes.base.entity.MaterialSyncDto;
import com.richfit.mes.base.provider.ErpServiceClient;
import com.richfit.mes.base.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: MaterialSyncServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月10日 09:07:00
 */
@Slf4j
@Service
@EnableScheduling
public class MaterialSyncServiceImpl extends ServiceImpl<ProductMapper, Product> implements MaterialSyncService {
    
    @Resource
    private MaterialSyncService materialSyncService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Autowired
    private ErpServiceClient erpServiceClient;

    @Override
    public List<Product> queryProductSync(MaterialSyncDto materialSyncDto) {
        return erpServiceClient.getMaterial(materialSyncDto.getDate(), materialSyncDto.getCode()).getData();
    }

    /**
     * 功能描述: 同步选中物料数据
     *
     * @param productList
     * @Author: xinYu.hou
     * @Date: 2022/2/10 16:00
     * @return: CommonResult<Boolean>
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveProductSync(List<Product> productList) {
        boolean data = false;
        String message = "操作失败";
        for (Product product : productList) {
            //同步时数据存在空格，会导致查不到图号
            if (StringUtils.isNotBlank(product.getMaterialNo())) {
                product.setMaterialNo(product.getMaterialNo().trim());
            }
            product.setDrawingNo(product.getDrawingNo().trim());
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("material_no", product.getMaterialNo());
            boolean remove = materialSyncService.remove(queryWrapper);
            boolean save = materialSyncService.save(product);
            if (save) {
                data = true;
                message = "操作成功!";
            }
        }
        return CommonResult.success(data, message);
    }

    /**
     * 功能描述: 定时同步物料数据
     *
     * @Author: xinYu.hou
     * @Date: 2022/2/10 16:01
     * @return: CommonResult<Boolean>
     **/
    @Override
    @Scheduled(cron = "${time.material}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveTimingProductSync() {
        boolean data = false;
        //获取所有工厂信息
        MaterialSyncDto materialSyncDto = new MaterialSyncDto();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        materialSyncDto.setDate(format.format(date));
        CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "", SecurityConstants.FROM_INNER);
        for (ItemParam itemParam : listCommonResult.getData()) {
            materialSyncDto.setCode(itemParam.getCode());
            List<Product> productList = materialSyncService.queryProductSync(materialSyncDto);
            for (Product product : productList) {
                product.setCreateBy("System");
                product.setModifyBy("System");
                product.setCreateTime(date);
                product.setModifyTime(date);
                product.setBranchCode(itemParam.getLabel());
                product.setTenantId(itemParam.getTenantId());
                QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("material_no", product.getMaterialNo());
                boolean remove = materialSyncService.remove(queryWrapper);
                boolean save = materialSyncService.save(product);
                if (remove && save) {
                    data = true;
                }
            }
        }
        return CommonResult.success(data);
    }
}
