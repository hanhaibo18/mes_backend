package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.produce.provider.BaseServiceClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:51
 */
@Component
public class BaseServiceClientFallbackImpl implements BaseServiceClient {
    @Override
    public CommonResult<List<Sequence>> getByRouterNo(String routerNo, String branchCode, String tenantId, String optId) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Branch>> selectBranchChildByCode(String branchCode) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Branch> selectBranchByCodeAndTenantId(String branchCode, String tenantId) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Product>> selectProduct(String materialNo, String drawingNo, String materialType) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Device> getDeviceById(String id) {
        return CommonResult.success(null);
    }

    @Override
    public List<Device> getDeviceByIdList(List<String> idList) {
        return null;
    }

    @Override
    public CommonResult<List<CalendarClass>> selectCalendarClass(String name) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<CalendarDay>> selectCalendarDay(String startDate, String endDate) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Router> getByRouterNo(String routerNo, String branchCode) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Router>> getByRouterNos(String routerNos, String branchCode) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Operatipon>> find(String id, String optCode, String optName, String routerId, String branchCode, String tenantId) {
        return null;
    }


    @Override
    public CommonResult<List<SequenceSite>> getSequenceDevice(String sequenceId, String siteId, String siteCode, String branchCode, String isDefault) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<DevicePerson>> getDevicePerson(String deviceId, String userId, String branchCode, String isDefault) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Product>> queryProductName() {
        return CommonResult.success(null);
    }

    @Override
    public List<ProjectBom> getProjectBomPartByIdList(String id) {
        return null;
    }

    @Override
    public CommonResult<List<RouterCheck>> find(String drawingNo, String optId, String type) {
        return CommonResult.success(null);
    }

    @Override
    public List<RouterCheck> queryRouterList(String optId, String type, String branchCode, String tenantId) {
        return null;
    }


    @Override
    public CommonResult<List<OperationTypeSpec>> list(String optType, String branchCode, String tenantId) {
        return CommonResult.success(null);
    }

    @Override
    public ProjectBom queryBom(String workPlanNo, String branchCode) {
        return null;
    }

    @Override
    public CommonResult<OperationAssign> assignGet(String optName, String branchCode) {
        return null;
    }


    @Override
    public CommonResult<Sequence> querySequenceById(String optName, String branchCode) {
        return null;
    }

    @Override
    public String queryCraft(String optName, String branchCode) {
        return null;
    }

    @Override
    public CommonResult<List<PdmDraw>> queryDrawList(String itemId) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<PdmMesOption> queryOptionDraw(String id) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<RouterCheck> routerCheckSelectById(String id) {
        return null;
    }

    @Override
    public Branch queryTenantIdByBranchCode(String branchCode) {
        return null;
    }

    @Override
    public List<OperationTypeSpec> queryOperationTypeSpecByType(String type, String branchCode, String tenantId) {
        return null;
    }

    @Override
    public List<Product> listByMaterialNo(String materialNo) {
        return null;
    }

    @Override
    public List<Product> selectOrderProduct(String materialNo, String drawingNo) {
        return null;
    }

    @Override
    public List<Operatipon> queryOptByOptNames(List<String> optNams, String branchCode) {
        return null;
    }

    @PostMapping("/api/base/router/get_by_drawNo")
    @Override
    public CommonResult<List<Router>> getByDrawNo(List<String> drawNos, String branchCode) {
        return null;
    }

    @Override
    public List<Sequence> querySequenceByRouterIds(List<String> routerIds) {
        return null;
    }


}
