package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.DeviceMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public IPage<Device> selectPage(Page page, QueryWrapper<Device> qw) {
        return deviceMapper.selectPage(page, qw);
    }


    @Override
    public Boolean delete(String id, QueryWrapper<Device> queryWrapper) {
        return deviceMapper.delete(id, queryWrapper);
    }

    /**
     * 导入设备excel
     * @param file
     * @param branchCode
     * @param tenantId
     * @return
     */
    @Override
    public CommonResult importExcel(MultipartFile file, String branchCode, String tenantId) {
        CommonResult result = null;
        //封装证件信息实体类
        java.lang.reflect.Field[] fields = Device.class.getDeclaredFields();
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
            List<Device> list = ExcelUtils.importExcel(excelFile, Device.class, fieldNames, 1, 0, 0, tempName.toString());
            //获取设备组的map集合
            QueryWrapper<Device> deviceQueryWrapper = new QueryWrapper<>();
            List<Device> deviceGroup = this.list(deviceQueryWrapper.eq("type", 1).eq("branch_code",branchCode));
            Map<String, String> deviceGroupMap = deviceGroup.stream().collect(Collectors.toMap(Device::getCode, v->v.getId(),(t1, t2)->t1));

            FileUtils.delete(excelFile);

            //先保存设备组
            List<Device> exportDeviceGroups = list.stream().filter(t -> "1".equals(t.getType())).collect(Collectors.toList());
            boolean groupBool = true;
            if(exportDeviceGroups.size()>0){
                groupBool = saveExportDeviceGroup(branchCode, tenantId, exportDeviceGroups, deviceGroupMap);
            }


            //后保存设备
            boolean deviceBool= true;
            List<Device> exportDevices = list.stream().filter(t -> "0".equals(t.getType())).collect(Collectors.toList());
            if(exportDevices.size()>0){
                deviceBool = saveExportDevice(branchCode, tenantId, deviceGroupMap, exportDevices);
            }

            if (deviceBool & groupBool) {
                return CommonResult.success(null);
            } else {
                return CommonResult.failed();
            }
        } catch (Exception e) {
            return CommonResult.failed();
        }
    }

    private boolean saveExportDevice(String branchCode, String tenantId, Map<String, String> deviceGroupMap, List<Device> exportDevices) {
        for (Device exportDevice : exportDevices) {
            //关联的设备组id
            String parentId = deviceGroupMap.get(exportDevice.getParentId());
            exportDevice.setTenantId(tenantId);
            exportDevice.setBranchCode(branchCode);

            if(!StringUtils.isNullOrEmpty(parentId)){
                //绑定设备组
                exportDevice.setParentId(parentId);
            }
            if(!StringUtils.isNullOrEmpty(exportDevice.getCode())){
                //判断此code否存在  存在更新 不存在新增
                QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("code",exportDevice.getCode()).eq("branch_code",branchCode);
                if(this.list(queryWrapper).size()>0){
                    exportDevice.setId(this.list(queryWrapper).get(0).getId());
                }
            }

            if ("是".equals(exportDevice.getRunStatus())) {
                exportDevice.setRunStatus("1");
            } else if ("否".equals(exportDevice.getRunStatus())) {
                exportDevice.setRunStatus("0");
            }
            if ("是".equals(exportDevice.getStatus())) {
                exportDevice.setStatus("1");
            } else if ("否".equals(exportDevice.getStatus())) {
                exportDevice.setStatus("0");
            }

        }
        //保存设备
        return this.saveOrUpdateBatch(exportDevices);
    }


    private boolean saveExportDeviceGroup(String branchCode, String tenantId, List<Device> list, Map<String, String> deviceGroupMap) {

        for (Device exportGroup : list) {
            //数据库该code存在的设备组
            String existGroupId = deviceGroupMap.get(exportGroup.getCode());

            if (!Strings.isNullOrEmpty(exportGroup.getCode())) {
                if (null != SecurityUtils.getCurrentUser()) {
                    exportGroup.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                }
                exportGroup.setTenantId(tenantId);
                exportGroup.setBranchCode(branchCode);

                //查找设备是否存在
                if (!StringUtils.isNullOrEmpty(existGroupId)) {
                    exportGroup.setId(existGroupId);
                } else {
                    //生成32位uuid
                    String id = UUID.randomUUID().toString().replaceAll("-", "");
                    exportGroup.setId(id);
                    if (!StringUtils.isNullOrEmpty(exportGroup.getCode())) {
                        //把设备组id存到deviceGroupMap  以备导入设备时关联到设备组id
                        deviceGroupMap.put(exportGroup.getCode(), id);
                    }
                }
                //设备组parent_id为空
                exportGroup.setParentId(null);

                if ("是".equals(exportGroup.getRunStatus())) {
                    exportGroup.setRunStatus("1");
                } else if ("否".equals(exportGroup.getRunStatus())) {
                    exportGroup.setRunStatus("0");
                }
                if ("是".equals(exportGroup.getStatus())) {
                    exportGroup.setStatus("1");
                } else if ("否".equals(exportGroup.getStatus())) {
                    exportGroup.setStatus("0");
                }
            }
        }
        //保存设备组
        return this.saveOrUpdateBatch(list);

    }

    @Override
    public List<Device> queryDeviceByIdList(List<String> idList){
       return deviceMapper.selectBatchIds(idList);
    }

}
