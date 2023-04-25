package com.richfit.mes.base.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
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
import com.richfit.mes.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @ClassName: MaterialSyncServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 物料同步
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

    @Value("${time.execute:false}")
    private Boolean execute;

    @Override
    public List<Product> queryProductSync(MaterialSyncDto materialSyncDto) {
        return erpServiceClient.getMaterial(materialSyncDto.getDate(), materialSyncDto.getCode(), SecurityConstants.FROM_INNER).getData();
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
        //获取到所有物料号
        List<String> materialNoList = productList.stream().map(Product::getMaterialNo).collect(Collectors.toList());
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("material_no", materialNoList);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<Product> list = materialSyncService.list(queryWrapper);
        //删除物料信息
        materialSyncService.remove(queryWrapper);
        //转化成MAP 方便获取id
        Map<String, Product> productMap = list.stream().collect(Collectors.toMap(Product::getMaterialNo, product -> product, (value1, value2) -> value2));
        for (Product product : productList) {
            //同步时数据存在空格，会导致查不到图号
            if (StringUtils.isNotBlank(product.getMaterialNo())) {
                product.setMaterialNo(product.getMaterialNo().trim());
            }
            product.setDrawingNo(product.getDrawingNo().trim());
            //同步开关字段 Autosyns  值为空 或者为 y  默认同步
            if (ObjectUtil.isEmpty(product.getAutosyns()) || product.getAutosyns().equals("null") || product.getAutosyns().equals("y")) {
                //根据物料号查询到物料实体
                if (null != productMap.get(product.getMaterialNo())) {
                    //存入该物料号以前的id
                    product.setId(productMap.get(product.getMaterialNo()).getId());
                } else {
                    //没有重新生成uuid
                    product.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                }
                boolean save = materialSyncService.save(product);
                if (save) {
                    data = true;
                    message = "操作成功!";
                }
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
    @Scheduled(cron = "0 0/10 * * * ? ")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveTimingProductSync() {
        if (execute) {
            //获取所有工厂信息
            MaterialSyncDto materialSyncDto = new MaterialSyncDto();
            CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "", SecurityConstants.FROM_INNER);
            //获取当天的时间
            String date = DateUtil.today();
            for (ItemParam itemParam : listCommonResult.getData()) {
                materialSyncDto.setCode(itemParam.getCode());
                log.debug("工厂代码：" + itemParam.getCode() + "开始同步");
                //同步前七天（包括今天）
                materialSyncDto.setDate(date);
                List<Product> productList = materialSyncService.queryProductSync(materialSyncDto);
                //获取到所有物料号
                List<String> materialNoList = productList.stream().map(Product::getMaterialNo).collect(Collectors.toList());
                QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
                queryWrapper.in("material_no", materialNoList);
                queryWrapper.eq("tenant_id", itemParam.getTenantId());
                List<Product> list = materialSyncService.list(queryWrapper);
                //删除物料信息
                materialSyncService.remove(queryWrapper);
                //转化成MAP 方便获取id
                Map<String, Product> productMap = list.stream().collect(Collectors.toMap(Product::getMaterialNo, product -> product, (value1, value2) -> value2));
                log.debug("日期：" + date + ",同步" + productList.size() + "条数据");
                for (Product product : productList) {
                    product.setCreateBy("System");
                    product.setModifyBy("System");
                    product.setCreateTime(DateUtil.date());
                    product.setModifyTime(DateUtil.date());
                    product.setBranchCode(itemParam.getLabel());
                    product.setTenantId(itemParam.getTenantId());
                    if (ObjectUtil.isEmpty(product.getAutosyns()) || product.getAutosyns().equals("null") || product.getAutosyns().equals("y")) {
                        //根据物料号查询到物料实体
                        if (null != productMap.get(product.getMaterialNo())) {
                            //存入该物料号以前的id
                            product.setId(productMap.get(product.getMaterialNo()).getId());
                        } else {
                            //没有重新生成uuid
                            product.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                        }
                        materialSyncService.save(product);
                    }
                }
                log.debug("工厂代码：" + itemParam.getCode() + "同步结束");
            }
        }
        return CommonResult.success(true);
    }
}
