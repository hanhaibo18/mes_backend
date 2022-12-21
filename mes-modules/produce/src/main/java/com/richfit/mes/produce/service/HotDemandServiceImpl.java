package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.produce.HotDemand;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.HotDemandMapper;
import com.richfit.mes.produce.entity.DemandExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class HotDemandServiceImpl extends ServiceImpl<HotDemandMapper, HotDemand> implements HotDemandService {


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
            hotDemand.setBranchCode(branchCode);
            hotDemand.setCreateTime(new Date());
            hotDemand.setSubmitState(0);
            demandList.add(hotDemand);
        }

        this.saveBatch(demandList);
        return CommonResult.success("");
    }



}
