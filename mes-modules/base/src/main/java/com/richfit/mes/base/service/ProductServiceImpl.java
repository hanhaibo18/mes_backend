package com.richfit.mes.base.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.ProductMapper;
import com.richfit.mes.base.enmus.MaterialTypeEnum;
import com.richfit.mes.base.enmus.MessageEnum;
import com.richfit.mes.base.enmus.TrackTypeEnum;
import com.richfit.mes.base.provider.SystemServiceClient;
import com.richfit.mes.base.provider.WmsServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.sys.DataDictionaryParam;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.wms.InventoryQuery;
import com.richfit.mes.common.model.wms.InventoryReturn;
import com.richfit.mes.common.model.wms.MaterialBasis;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.function.Function;
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

    @Autowired
    private WmsServiceClient wmsServiceClient;

    @Autowired
    private SystemServiceClient systemServiceClient;

    @Override
    public IPage<Product> selectProduct(Page<Product> page, QueryWrapper<Product> query) {
        return productMapper.selectProduct(page, query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importMaterialByExcle(MultipartFile file, String tenantId, String branchCode) {
        String[] fieldNames = new String[]{"productName", "materialType", "materialNo", "drawingNo", "materialDate",
                "objectType", "weight", "texture", "unit", "materialDesc", "autosyns", "specification"};
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
            if (!result) {
                throw new GlobalException("excel批量导入失败", ResultCode.FAILED);
            }
        } catch (Exception e) {
            log.error("excel导入失败", e);
            return "failed";
        }
        return "success";
    }

    /**
     * 物料导入 Excel
     *
     * @param file
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult importMaterialExcel(MultipartFile file) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        String tenantId = currentUser.getTenantId();
        String[] MaterialNames = {"materialNo", "drawingNo", "productName", "materialDate", "materialType", "objectType", "materialDesc", "texture", "weight", "unit", "isKeyPart", "isNeedPicking", "trackType", "isEdgeStore", "isCheck"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            List<Product> productList = ExcelUtils.importExcel(excelFile, Product.class, MaterialNames, 1, 0, 0, tempName.toString());
            if (org.springframework.util.CollectionUtils.isEmpty(productList)) {
                return CommonResult.failed("未检测到有物料导入！");
            }
            // 判断SAP 物料编码是否存在
            boolean exist = false;
            List<String> productAddByIdList = new ArrayList<>();
            List<Product> productUpdateList = new ArrayList();
            List<Product> productAddList = new ArrayList();
            for (Product product : productList) {
                if (StringUtils.isNullOrEmpty(product.getMaterialNo())) {
                    return CommonResult.failed("物料编码不能为空！");
                }
                if (StringUtils.isNullOrEmpty(product.getProductName())) {
                    return CommonResult.failed("产品名称不能为空！");
                }
                if (StringUtils.isNullOrEmpty(product.getDrawingNo())) {
                    return CommonResult.failed("图号不能为空！");
                }
                if (StringUtils.isNullOrEmpty(product.getMaterialType())) {
                    return CommonResult.failed("类型不能为空！");
                }
                if (StringUtils.isNullOrEmpty(product.getMaterialDesc())) {
                    return CommonResult.failed("物料描述不能为空！");
                }
                if (StringUtils.isNullOrEmpty(product.getTrackType())) {
                    return CommonResult.failed("跟踪类型不能为空！");
                }
                product.setTenantId(tenantId);
                product.setMaterialNo(product.getMaterialNo().trim());
                product.setDrawingNo(product.getDrawingNo().trim());
                product.setProductName(product.getProductName());
                product.setMaterialDate(product.getMaterialDate());
                product.setMaterialType(MaterialTypeEnum.getCode(product.getMaterialType()));
                product.setObjectType(product.getObjectType());
                product.setMaterialDesc(product.getMaterialDesc());
                product.setTexture(product.getTexture());
                product.setWeight(product.getWeight());
                product.setUnit(product.getUnit());
                product.setTrackType(TrackTypeEnum.getCode(product.getTrackType()));
                if (StringUtils.isNullOrEmpty(product.getIsKeyPart())) {
                    product.setIsKeyPart("0");
                } else {
                    product.setIsKeyPart(Integer.toString(MessageEnum.getCode(product.getIsKeyPart())));
                }
                if (StringUtils.isNullOrEmpty(product.getIsNeedPicking())) {
                    product.setIsNeedPicking("0");
                } else {
                    product.setIsNeedPicking(Integer.toString(MessageEnum.getCode(product.getIsNeedPicking())));
                }
                if (StringUtils.isNullOrEmpty(product.getIsEdgeStore())) {
                    product.setIsEdgeStore("0");
                } else {
                    product.setIsEdgeStore(Integer.toString(MessageEnum.getCode(product.getIsEdgeStore())));
                }
                if (StringUtils.isNullOrEmpty(product.getIsCheck())) {
                    product.setIsCheck("0");
                } else {
                    product.setIsCheck(Integer.toString(MessageEnum.getCode(product.getIsCheck())));
                }
                // 物料编码若存在则更新 否则就新增
                if (checkExist(product) == true) {
                    productUpdateList.add(product);
                } else {
                    exist = true;
                    productAddList.add(product);
                    productAddByIdList.add(product.getMaterialNo());
                }
            }
            if (CollectionUtils.isNotEmpty(productAddList)) {
                productService.saveBatch(productAddList);
            }
            if (CollectionUtils.isNotEmpty(productUpdateList)) {
                productService.updateBatchById(productUpdateList);
            }
            if (exist) {
                return CommonResult.success("该SAP物料编码不存在已新增，SAP物料编码为：" + productAddByIdList, "导入成功");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return CommonResult.success(null, "导入成功");

    }

    private boolean checkExist(Product product) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", product.getTenantId());
        queryWrapper.eq("material_no", product.getMaterialNo());
        List<Product> list = productService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            String id = list.stream().map(e -> e.getId()).collect(Collectors.toList()).get(0);
            product.setId(id);
            return true;
        }
        return false;
    }

    /**
     * 勾选物料同步到wms
     *
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveWmsSync(List<String> ids) {
        List<Product> productList = productMapper.selectBatchIds(ids);
        if (CollectionUtils.isEmpty(productList)) {
            return CommonResult.failed("未勾选中物料数据");
        }

        List<String> erpCodeList = new ArrayList<>();
        // 关联租户 获取erpCode
        for (Product product : productList) {
            CommonResult<Tenant> tenant = systemServiceClient.tenantById(product.getTenantId());
            erpCodeList.add(tenant.getData().getTenantErpCode());
        }
        Map<String, Product> productMap = productList.stream().collect(Collectors.toMap(e -> e.getId(), product -> product));

        if (CollectionUtils.isNotEmpty(erpCodeList)) {
            int init = 0;
            List<MaterialBasis> materialBasisList = new ArrayList<>();
            MaterialBasis materialBasis = new MaterialBasis();
            for (Product product : productList) {
                materialBasis.setWorkCode(erpCodeList.get(init));
                materialBasis.setMaterialNum(productMap.get(product.getId()).getMaterialNo());
                materialBasis.setMaterialDesc(productMap.get(product.getId()).getMaterialDesc());
                materialBasis.setUnit(productMap.get(product.getId()).getUnit());
                materialBasis.setCrucialFlag(productMap.get(product.getId()).getIsKeyPart());
                materialBasis.setTrackingMode(productMap.get(product.getId()).getTrackType());
                materialBasis.setPartsMaterial(productMap.get(product.getId()).getTexture());
                materialBasis.setSpec(productMap.get(product.getId()).getSpecification());
                if (productMap.get(product.getId()).getWeight() == null) {
                    materialBasis.setSingleWeight(null);
                } else {
                    materialBasis.setSingleWeight(productMap.get(product.getId()).getWeight().toString());
                }
                materialBasis.setDeliveryFlag(productMap.get(product.getId()).getIsEdgeStore());
                materialBasis.setProduceType("");
                materialBasis.setMaterialType(MaterialTypeEnum.getName(productMap.get(product.getId()).getMaterialType()));
                materialBasis.setWorkshop(productMap.get(product.getId()).getBranchCode());
                materialBasis.setField1("");
                materialBasis.setField2("");
                materialBasis.setField3("");
                materialBasis.setField4("");
                materialBasis.setField5("");
                materialBasisList.add(materialBasis);
                init++;
            }
            // 同步到wms中
            wmsServiceClient.materialBasis(materialBasisList);

            return CommonResult.success(true, "操作成功");
        }
        return CommonResult.failed("操作失败");
    }


    /**
     * 查询库存
     *
     * @param inventoryQuery
     * @return
     */
    @Override
    public CommonResult<List<InventoryReturn>> selectInventory(InventoryQuery inventoryQuery) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        String tenantId = currentUser.getTenantId();
        if (StringUtils.isNullOrEmpty(inventoryQuery.getMaterialNum())) {
            return CommonResult.failed("物料编码不能为空");
        }
        String[] materialNums = inventoryQuery.getMaterialNum().split(",");
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.in("material_no", materialNums);
        List<Product> productList = productService.list(queryWrapper);
        Map<String, Product> productMap = productList.stream().collect(Collectors.toMap(Product::getMaterialNo, Function.identity()));
        //获取当前登录用户erpCode
        String tenantErpCode = systemServiceClient.tenantById(tenantId).getData().getTenantErpCode();
        if (CollectionUtils.isNotEmpty(productList) && !StringUtils.isNullOrEmpty(tenantErpCode)) {
            int init = 1;
            StringBuilder stringBuilder = new StringBuilder();
            for (Product product : productList) {
                if (init == 1) {
                    stringBuilder.append(product.getMaterialNo());
                } else {
                    stringBuilder.append(",").append(product.getMaterialNo());
                }
                init++;
            }
            inventoryQuery.setMaterialNum(stringBuilder.toString());
            if (StringUtils.isNullOrEmpty(inventoryQuery.getWorkCode())) {
                inventoryQuery.setWorkCode(tenantErpCode);
            }
            CommonResult<List<InventoryReturn>> listCommonResult = wmsServiceClient.inventoryQuery(inventoryQuery);
            if (CollectionUtils.isEmpty(listCommonResult.getData())) {
                return CommonResult.failed("未查询到相关信息");
            }
            for (InventoryReturn datum : listCommonResult.getData()) {
                datum.setDrawingNo(productMap.get(datum.getMaterialNum()).getDrawingNo());
            }
            return listCommonResult;
        }
        return CommonResult.failed("未查询到相关信息");
    }

    @Override
    public Page<InventoryReturn> selectMaterial(String branchCode, int limit, int page, String materialNo, String materialName, Integer invType, String texture) {
        List<DataDictionaryParam> dataDictionaryParams = systemServiceClient.getDataDictionaryParamByBranchCode(branchCode).getData();
        //按条件过滤
        if (materialNo != null) {
            dataDictionaryParams = dataDictionaryParams.stream().filter(x -> Objects.equals(x.getMaterialNo(), materialNo)).collect(Collectors.toList());
        }
        if (materialName != null) {
            dataDictionaryParams = dataDictionaryParams.stream().filter(x -> Objects.equals(x.getMaterialName(), materialName)).collect(Collectors.toList());
        }
        if (texture != null) {
            dataDictionaryParams = dataDictionaryParams.stream().filter(x -> Objects.equals(x.getTexture(), texture)).collect(Collectors.toList());
        }
        Page<InventoryReturn> resultPage = new Page<>();
        if (CollectionUtils.isEmpty(dataDictionaryParams)) {
            return resultPage;
        }
        Map<String, DataDictionaryParam> dictionaryParamMap = dataDictionaryParams.stream().collect(Collectors.toMap(DataDictionaryParam::getMaterialNo, Function.identity()));
        String materialNos = "";
        for (DataDictionaryParam dataDictionaryParam : dataDictionaryParams) {
            materialNos = materialNos + dataDictionaryParam.getMaterialNo() + ",";
        }
        materialNos = materialNos.substring(0, materialNos.lastIndexOf(","));
        InventoryQuery inventoryQuery = new InventoryQuery();
        inventoryQuery.setMaterialNum(materialNos);
        List<InventoryReturn> inventoryQueryList = this.selectInventory(inventoryQuery).getData();
        if (inventoryQueryList == null) {
            return resultPage;
        }
        if (invType != null) {
            inventoryQueryList = inventoryQueryList.stream().filter(x -> Objects.equals(x.getInvType(), invType)).collect(Collectors.toList());
        }
        for (InventoryReturn inventoryReturn : inventoryQueryList) {
            inventoryReturn.setMaterialName(dictionaryParamMap.get(inventoryReturn.getMaterialNum()).getMaterialName());
            inventoryReturn.setMaterialName(dictionaryParamMap.get(inventoryReturn.getMaterialNum()).getTexture());
        }
        resultPage.setTotal(inventoryQueryList.size());
        resultPage.setSize(limit);
        resultPage.setCurrent(page);
        inventoryQueryList = inventoryQueryList.stream().skip((page - 1) * limit).limit(limit).collect(Collectors.toList());
        resultPage.setRecords(inventoryQueryList);
        return resultPage;
    }


}
