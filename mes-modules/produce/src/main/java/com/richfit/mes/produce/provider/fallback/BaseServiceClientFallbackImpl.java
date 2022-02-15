package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.produce.provider.BaseServiceClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:51
 */
@Component
public class BaseServiceClientFallbackImpl implements BaseServiceClient {
    @Override
    public CommonResult<List<Sequence>> getByRouterNo(String routerNo,String branchCode,String tenantId,String optId) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Branch>> selectBranchChildByCode(String branchCode) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Product>> selectProduct(String materialNo, String drawingNo, String materialType) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Device> getDeviceById(String id)  {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<CalendarClass>> selectCalendarClass(String name)  {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<CalendarDay>> selectCalendarDay(String startDate,String endDate)  {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Router> getRouterByNo(String routerNo, String branchCode) {
        return CommonResult.success(null);
    }
    
    
    @Override
    public CommonResult<List<SequenceSite>> getSequenceDevice(String sequenceId, String siteId, String siteCode, String branchCode, String isDefault)
    {
           return CommonResult.success(null); 
    }
     
    @Override
    public CommonResult<List<DevicePerson>> getDevicePerson(String deviceId, String userId, String branchCode,String isDefault)
    {
         return CommonResult.success(null);
    }

}
