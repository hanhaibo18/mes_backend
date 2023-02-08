package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.produce.HotDemand;
import com.richfit.mes.common.model.produce.HotModelStore;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.HotDemandMapper;
import com.richfit.mes.produce.entity.DemandExcel;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HotDemandServiceImpl extends ServiceImpl<HotDemandMapper, HotDemand> implements HotDemandService {
    @Resource
    private HotDemandService hotDemandService;
    @Resource
    public HotModelStoreService hotModelStoreService;
    /**
     * 导入需求提报数据
     * @param file
     * @param branchCode
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult importDemand(MultipartFile file, String branchCode) {
        CommonResult result = null;
        //封装证件信息实体类
        java.lang.reflect.Field[] fields = DemandExcel.class.getDeclaredFields();
        //封装证件信息实体类
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));

        excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());

        //将导入的excel数据生成证件实体类list
        List<DemandExcel> list = null;
        try {
            file.transferTo(excelFile);
            list = ExcelUtils.importExcel(excelFile, DemandExcel.class, fieldNames, 1, 0, 0, tempName.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        ArrayList<HotDemand> demandList = new ArrayList<>();
        for (DemandExcel hemandExcel : list) {
            //丰富基础数据
            HotDemand hotDemand = new HotDemand();
            try {
                //属性拷贝
                BeanUtils.copyProperties(hotDemand,hemandExcel);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            hotDemand.setTenantId(currentUser.getTenantId());
            hotDemand.setCreateBy(currentUser.getUsername());
            hotDemand.setSubmitOrderTime(new Date());
            hotDemand.setSubmitById(currentUser.getUserId());
            hotDemand.setBranchCode(branchCode);
            hotDemand.setCreateTime(new Date());
            hotDemand.setSubmitState(0);
            demandList.add(hotDemand);
        }

        this.saveBatch(demandList);
        return CommonResult.success("");
    }


    /**
     * 检查模型
      * @param idList
     * @param branchCode
     * @return
     */
    @Override
    public List<String> checkModel(List<String> idList, String branchCode) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();

        //无模型或者模型字段为空的毛坯需求数据
        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("id",idList);
        queryWrapper.eq("tenant_id",currentUser.getTenantId());
        queryWrapper.eq("branch_code",branchCode);
        queryWrapper.apply("is_exist_model is null");
        queryWrapper.apply("(is_exist_model=0 or is_exist_model is null)");
        List<HotDemand> hotDemands = hotDemandService.list(queryWrapper);
        List<String> drawNos = hotDemands.stream().map(x -> x.getDrawNo()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(drawNos))  return new ArrayList<>();
        //根据需求数据中的图号查询模型库
        QueryWrapper<HotModelStore> modelWrapper=new QueryWrapper();
        modelWrapper.eq("tenant_id",currentUser.getTenantId());
        modelWrapper.in("model_drawing_no",drawNos);
        List<HotModelStore> list = hotModelStoreService.list(modelWrapper);
        //模型
        Map<String, HotModelStore> ModelMap = list.stream().collect(Collectors.toMap(x -> x.getModelDrawingNo(), x -> x));

        List<String> ids=new ArrayList<>();
        //遍历毛坯需求数据,根据图号在模型map中获取,不为空则有模型
        for (HotDemand hotDemand : hotDemands) {
            HotModelStore hotModelStore = ModelMap.get(hotDemand.getDrawNo());
            if (ObjectUtils.isEmpty(hotModelStore)){
                //收集无模型的毛坯需求id
                ids.add(hotDemand.getId());
            }
        }
        return ids;
    }




}
