package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.DeviceMapper;
import com.richfit.mes.base.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.DevicePerson;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
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
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private SystemServiceClient systemServiceClient;

    @Autowired
    private DevicePersonService devicePersonService;

    @Override
    public IPage<Device> selectPage(Page page, QueryWrapper<Device> qw) {
        return deviceMapper.selectPage(page, qw);
    }



    //设备组
    private static final String GROUP = "1";
    //设备
    private static final String DEVICE = "0";


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
    @Transactional(rollbackFor = Exception.class)
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

            //校验数据
            String message = checkExportInfo(list, branchCode);
            if(!StringUtils.isNullOrEmpty(message)){
                return CommonResult.failed("设备导入失败原因如下：</br>"+message);
            }
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
                }else{
                    //生成32位uuid
                    String id = UUID.randomUUID().toString().replaceAll("-", "");
                    exportDevice.setId(id);
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
            //保存人员
            if(!StringUtils.isNullOrEmpty(exportDevice.getUserAccount())){
                //查询原有的人员（有的跟新，没有的新增）
                QueryWrapper<DevicePerson> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("device_id",exportDevice.getId());
                List<DevicePerson> oldDevicePerson = devicePersonService.list(queryWrapper);
                Map<String, DevicePerson> oldDevicePersonMap = oldDevicePerson.stream().collect(Collectors.toMap(DevicePerson::getUserId,Function.identity(),(x1,x2)->x2));

                    for (String s : exportDevice.getUserAccount().split(":")) {
                    DevicePerson devicePerson = new DevicePerson();
                    if(oldDevicePersonMap.containsKey(s)){
                        devicePerson = oldDevicePersonMap.get(s);
                    }
                    devicePerson.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                    devicePerson.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    devicePerson.setCreateTime(new Date());
                    devicePerson.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    devicePerson.setModifyTime(new Date());
                    devicePerson.setBranchCode(branchCode);
                    devicePerson.setDeviceId(exportDevice.getId());
                    devicePerson.setUserId(s);
                    //派工默认
                    if(!StringUtils.isNullOrEmpty(exportDevice.getTask())){
                        List<String> defautUser = Arrays.asList(exportDevice.getTask().split(":"));
                        devicePerson.setIsDefault(defautUser.contains(s)?1:0);
                    }else{
                        devicePerson.setIsDefault(0);
                    }
                    devicePersonService.saveOrUpdate(devicePerson);
                }
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

    //设备导入校验
    private String checkExportInfo(List<Device> list,String branchCode){
        StringBuilder message = new StringBuilder();

        //类型为空的数据
        List<Device> typeNullList = list.stream().filter(item -> StringUtils.isNullOrEmpty(item.getType())).collect(Collectors.toList());

        //设备组数据
        List<Device> groups = list.stream().filter(item -> GROUP.equals(item.getType())).collect(Collectors.toList());

        //设备数据
        List<Device> devices = list.stream().filter(item -> DEVICE.equals(item.getType())).collect(Collectors.toList());

        //类型不为0，1的数据
        List<Device> others = list.stream().filter(item -> !DEVICE.equals(item.getType()) && !GROUP.equals(item.getType()) && !StringUtils.isNullOrEmpty(item.getType())).collect(Collectors.toList());

       if(typeNullList.size()>0){
           message.append("类型为必填</br>");
       }
        if(others.size()>0){
            message.append("类型必须为0或1</br>");
        }

        //设备组校验
        for (Device device : groups) {
            String oneMessage =  "";
            if (StringUtils.isNullOrEmpty(device.getCode())) {
                oneMessage += "设备编码";
            }
            if (StringUtils.isNullOrEmpty(device.getName())) {
                if(!StringUtils.isNullOrEmpty(oneMessage)){
                    oneMessage+="、";
                }
                oneMessage += "设备名称";
            }
            if (StringUtils.isNullOrEmpty(device.getRunStatus())) {
                if(!StringUtils.isNullOrEmpty(oneMessage)){
                    oneMessage+="、";
                }
                oneMessage += "运行状态";
            }
            if(!StringUtils.isNullOrEmpty(oneMessage)){
                message.append("设备组："+device.getName()+","+oneMessage+"不能为空</br>");
            }
        }

        //设备校验
        for (Device device : devices) {
            String oneMessage =  "";
            if (StringUtils.isNullOrEmpty(device.getRunStatus())) {
                oneMessage += "设备组";
            }
            if (StringUtils.isNullOrEmpty(device.getCode())) {
                if(!StringUtils.isNullOrEmpty(oneMessage)){
                    oneMessage+="、";
                }
                oneMessage += "设备编码";
            }
            if (StringUtils.isNullOrEmpty(device.getName())) {
                if(!StringUtils.isNullOrEmpty(oneMessage)){
                    oneMessage+="、";
                }
                oneMessage += "设备名称";
            }
            if (StringUtils.isNullOrEmpty(device.getRunStatus())) {
                if(!StringUtils.isNullOrEmpty(oneMessage)){
                    oneMessage+="、";
                }
                oneMessage += "运行状态";
            }
            if(!StringUtils.isNullOrEmpty(oneMessage)){
                message.append("设备："+device.getName()+","+oneMessage+"不能为空</br>");
            }
        }

        //人员信息校验
        List<TenantUserVo> tenantUserVos = systemServiceClient.queryUserByBranchCode(branchCode).getData();
        Map<String, TenantUserVo> tenantUserVosMap = tenantUserVos.stream().collect(Collectors.toMap(TenantUserVo::getUserAccount, Function.identity()));

        for (Device device : list) {
            String oneMessage =  "";
            if(!StringUtils.isNullOrEmpty(device.getUserAccount())){
                List<String> userAccounts = Arrays.asList(device.getUserAccount().split(":"));
                for (String userAccount : userAccounts) {
                    if(!tenantUserVosMap.containsKey(userAccount)){
                        if(!StringUtils.isNullOrEmpty(oneMessage)){
                            oneMessage+="、";
                        }
                        oneMessage+=userAccount;
                    }
                }
            }
            if(!StringUtils.isNullOrEmpty(oneMessage)){
                message.append("设备名称："+device.getName()+","+oneMessage+"人员信息不正确</br>");
            }
        }

        return message.toString();

    }

    @Override
    public List<Device> queryDeviceByIdList(List<String> idList){
        return deviceMapper.selectBatchIds(idList);
    }



}
