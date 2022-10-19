package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Device;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 马峰
 * @Description 工艺服务
 */
public interface DeviceService extends IService<Device> {

    public IPage<Device> selectPage(Page page, QueryWrapper<Device> qw);


    Boolean delete(String id, QueryWrapper<Device> queryWrapper);

    CommonResult importExcel(MultipartFile file, String branchCode, String tenantId);

    List<Device> queryDeviceByIdList(List<String> idList);
}
