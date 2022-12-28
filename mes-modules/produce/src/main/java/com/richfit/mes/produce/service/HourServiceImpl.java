package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.produce.Hour;
import com.richfit.mes.produce.dao.HourMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * 热工工时标准
 * </p>
 *
 * @author renzewen
 * @since 2022-12-26
 */
@Service
public class HourServiceImpl extends ServiceImpl<HourMapper, Hour> implements HourService {


    /**
     * 工时标准导入
     * @param file
     * @param branchCode
     * @param verId
     * @return
     */
    @Override
    public CommonResult importExcel(MultipartFile file, String branchCode, String verId) {
        CommonResult result = CommonResult.success(true);
        //封装工时信息实体类
        java.lang.reflect.Field[] fields = Hour.class.getDeclaredFields();

        String[] fieldNames = {"isExport","deviceType","sequence","weightDown","weightUp","cDown","cUp","nDown","nUp","isHighTemp","hour"};

        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //模板校验
            //将导入的excel数据生成实体类list
            List<Hour> checkInfo = ExcelUtils.importExcel(excelFile, Hour.class, fieldNames, 0, 0, 0, tempName.toString());
            if(checkInfo.size()>0){
                if("是否导入".equals(checkInfo.get(0).getIsExport()) &&
                        "设备类型".equals(checkInfo.get(0).getDeviceType()) &&
                        "工序".equals(checkInfo.get(0).getSequence()) &&
                        "重量下限".equals(checkInfo.get(0).getWeightDown())){

                }else{
                    return CommonResult.failed("模板不正确!");
                }
            }else{
                return CommonResult.failed("模板不正确!");
            }
            //将导入的excel数据生成实体类list
            List<Hour> list = ExcelUtils.importExcel(excelFile, Hour.class, fieldNames, 1, 0, 0, tempName.toString());
            //过滤可以导入的数据
            List<Hour> exportList = list.stream().filter(item -> "X".equals(item.getIsExport())).collect(Collectors.toList());
            //绑定版本
            for (Hour hour : exportList) {
                hour.setVerId(verId);
            }
            FileUtils.delete(excelFile);
            //删除旧数据
            QueryWrapper<Hour> hourQueryWrapper = new QueryWrapper<>();
            hourQueryWrapper.eq("ver_id",verId);
            this.remove(hourQueryWrapper);

            //保存工时
            this.saveBatch(exportList);

        } catch (Exception e) {
            return CommonResult.failed();
        }
        return result;
    }
}
