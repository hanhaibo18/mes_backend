package com.richfit.mes.produce.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Operatipon;
import com.richfit.mes.common.model.produce.Hour;
import com.richfit.mes.common.model.produce.HourStandard;
import com.richfit.mes.common.model.produce.RgDevice;
import com.richfit.mes.produce.dao.HourMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
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


    @Autowired
    private BaseServiceClient baseServiceClient;
    @Autowired
    private RgDeviceService rgDeviceService;
    @Autowired
    private HourStandardService hourStandardService;
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
        String[] fieldNames = {"isExport","deviceName","optName","weightDown","weightUp","layerDepthCarbonFloor","layerDepthCarbonCeiling","layerDepthNitrogenCeiling","layerDepthNitrogenFloor","isHighTemp","hour"};

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
                        "设备类型".equals(checkInfo.get(0).getTypeName()) &&
                        "工序".equals(checkInfo.get(0).getOptName()) &&
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
            //工序和设备需要根据name 获取id和设备类型  转换map 便于取值
            List<String> optNames = exportList.stream().map(Hour::getOptName).collect(Collectors.toList());
            List<String> deviceNames = exportList.stream().map(Hour::getTypeName).collect(Collectors.toList());
            Map<String, Operatipon> optMap = baseServiceClient.queryOptByOptNames(optNames, branchCode).stream().collect(Collectors.toMap(Operatipon::getOptName, Function.identity()));
            QueryWrapper<RgDevice> rgDeviceQueryWrapper = new QueryWrapper<>();
            rgDeviceQueryWrapper.in("type_name",deviceNames);
            Map<String, RgDevice> deviceMap = rgDeviceService.list(rgDeviceQueryWrapper).stream().collect(Collectors.toMap(RgDevice::getTypeName, Function.identity()));
            //工序id和设备类型赋值
            for (Hour hour : exportList) {
                if (!ObjectUtil.isEmpty(optMap.get(hour.getOptName()))) {
                    hour.setOptId(optMap.get(hour.getOptName()).getId());
                }
                if (!ObjectUtil.isEmpty(optMap.get(hour.getTypeName()))) {
                    hour.setTypeCode(deviceMap.get(hour.getTypeName()).getTypeCode());
                }
            }
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

    @Override
    public void exportExcel(String verId, String branchCode, HttpServletResponse rsp) {
        try {
            //工时版本
            HourStandard hourStandard = hourStandardService.getById(verId);
            //工时标准
            QueryWrapper<Hour> queryWrapper = new QueryWrapper<Hour>();
            if (!StringUtils.isNullOrEmpty(verId)) {
                queryWrapper.eq("ver_id", verId);
            }
            queryWrapper.orderByDesc("modify_time");
            List<Hour> list = this.list(queryWrapper);

            for (Hour hour : list) {
                hour.setVer(hourStandard.getVer());
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "热工工时标准列表_" + format.format(new Date()) + ".xlsx";

            String[] columnHeaders = {"ID", "版本号", "设备类型", "设备名", "工序id", "工序名", "重量下限", "重量上限", "碳化上限", "碳化下限", "氮化上限","氮化下限","是否高温","工时","变更时间","变更人"};

            String[] fieldNames = {"id", "ver", "typeCode", "deviceName", "optId", "optName", "weightUp", "weightDown", "layerDepthCarbonCeiling","layerDepthCarbonFloor","layerDepthNitrogenCeiling","layerDepthNitrogenFloor","isHighTemp","hour","modifyTime","modifyBy"};

            //export
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
