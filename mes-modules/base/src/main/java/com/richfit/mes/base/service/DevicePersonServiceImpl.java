package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.DevicePersonMapper;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.DevicePerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class DevicePersonServiceImpl extends ServiceImpl<DevicePersonMapper, DevicePerson> implements DevicePersonService {

    @Autowired
    private DevicePersonMapper devicePersonMapper;
    @Resource
    private DeviceService deviceService;

    public IPage<DevicePerson> selectPage(Page page, QueryWrapper<DevicePerson> qw) {
        return devicePersonMapper.selectPage(page, qw);
    }

    @Override
    public List<Device> queryDeviceByUserId(String userId,String branchCode) {
        QueryWrapper<DevicePerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<DevicePerson> devicePeople = this.list(queryWrapper);
        if (devicePeople != null) {
            List<Device> deviceList = new ArrayList<>();
            for (DevicePerson devicePerson : devicePeople) {
                QueryWrapper<Device> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("branch_code",branchCode)
                        .eq("id",devicePerson.getDeviceId());
                List<Device> list = deviceService.list(queryWrapper1);
                if(list.size()>0){
                    deviceList.add(list.get(0));
                }
            }
            return deviceList;
        }
        return Collections.emptyList();
    }

    @Override
    public List<Device> queryDeviceByUserIds(List<String> userIds,String branchCode) {
        if(userIds.size()>0){
            QueryWrapper<DevicePerson> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("user_id", userIds);
            List<DevicePerson> devicePeople = this.list(queryWrapper);
            //取所有人共有的设备
            List<String> deviceIds = new ArrayList<>();
            Map<String, List<DevicePerson>> deviceGroup = devicePeople.stream().collect(Collectors.groupingBy(DevicePerson::getDeviceId));
            deviceGroup.forEach((key,values)->{
                //一个人只能绑定同一个设备一次  因此同一个人同设备信息只能有一个  通过此属性  取人员的设备交集
                if(values.size()==userIds.size()){
                    deviceIds.add(key);
                }
            });
            if (deviceIds.size()>0) {
                List<Device> deviceList = new ArrayList<>();
                for (DevicePerson devicePerson : devicePeople) {
                    QueryWrapper<Device> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("branch_code",branchCode)
                            .in("id",deviceIds);
                    List<Device> list = deviceService.list(queryWrapper1);
                    if(list.size()>0){
                        deviceList.add(list.get(0));
                    }
                }
                return deviceList;
            }
        }
        return Collections.emptyList();
    }

}
