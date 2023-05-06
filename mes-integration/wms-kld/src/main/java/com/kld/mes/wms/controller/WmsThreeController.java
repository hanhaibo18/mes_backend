package com.kld.mes.wms.controller;

import com.kld.mes.wms.service.ProductToWmsThreeService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.wms.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: zhiqiang.lu
 * @Date: 2023/3/10 10:36
 */
@Slf4j
@Api("wms three 仓储接口")
@RestController
@RequestMapping("/api/integration/wms/three")
public class WmsThreeController {

    @Resource
    private ProductToWmsThreeService productToWmsThreeService;

    @ApiOperation(value = "MES物料基础数据同步接口", notes = "将MES系统物料基础数据实时同步到WMS系统")
    @PostMapping("/material_basis")
    public CommonResult<ApplicationResult> materialBasis(@RequestBody List<MaterialBasis> materialBasisList) {
        return new CommonResult(productToWmsThreeService.materialBasisInterface(materialBasisList));
    }

    @ApiOperation(value = "MES报检单驳回WMS", notes = "MES系统支持对未入库的WMS报检单驳回，驳回成功上传WMS，上传成功，WMS系统报检单状态更新已退回，可查看驳回原因、驳回操作人和驳回日期，该报检单允许修改后重新提交")
    @PostMapping("/reject_inspection_doc")
    public CommonResult<ApplicationResult> rejectInspectionDoc(@RequestBody RejectInspectionDoc rejectInspectionDoc) {
        return new CommonResult(productToWmsThreeService.rejectInspectionDocInterface(rejectInspectionDoc));
    }

    @ApiOperation(value = "MES报检单质检结果上传WMS", notes = "MES系统将WMS报检单质检结果上传WMS，上传成功，WMS系统报检单状态更新已检验")
    @PostMapping("/inspection_doc_upload")
    public CommonResult<ApplicationResult> inspectionDocUpload(@RequestBody InspectionDocUpload inspectionDocUpload) {
        return new CommonResult(productToWmsThreeService.inspectionDocUploadInterface(inspectionDocUpload));
    }

    @ApiOperation(value = "MES申请单上传WMS（已上线）", notes = "将MES系统满足条件申请单上传WMS。提供上传WMS按钮，用户点击按钮可手动将申请单上传WMS系统")
    @PostMapping("/apply_list_upload")
    public CommonResult<ApplicationResult> applyListUpload(@RequestBody List<ApplyListUpload> applyListUpload) {
        return new CommonResult(productToWmsThreeService.applyListUploadInterface(applyListUpload));
    }


    @ApiOperation(value = "MES领料单上传WMS", notes = "将MES系统领料单上传WMS，若单据类型为自动出库时，WMS系统要检查各行项目物料的在该工厂下的库存数量能否满足领料数量，需全部行项目物料库存都满足时，WMS系统自动生成出库单，物资库存减少，调用WMS领料单出库信息上传MES接口")
    @PostMapping("/material_requisition_upload")
    public CommonResult<ApplicationResult> materialRequisitionUpload(@RequestBody MaterialRequisitionUpload materialRequisitionUpload) {
        return new CommonResult(productToWmsThreeService.materialRequisitionUploadInterface(materialRequisitionUpload));
    }


    @ApiOperation(value = "MES领料单撤回上传WMS", notes = "MES系统将未出库的领料单撤回上传WMS，上传成功，WMS系统撤回的领料单或部分行项目删除")
    @PostMapping("/material_requisition_recall")
    public CommonResult<ApplicationResult> materialRequisitionRecall(@RequestBody MaterialRequisitionRecall materialRequisitionRecall) {
        return new CommonResult(productToWmsThreeService.materialRequisitionRecallInterface(materialRequisitionRecall));
    }


    @ApiOperation(value = "MES计划清单锁定/解锁物资库存上传WMS", notes = "MES系统将锁定、释放的工厂锁定库存上传WMS")
    @PostMapping("/system_upload")
    public CommonResult<ApplicationResult> systemUpload(@RequestBody SystemUpload systemUpload) {
        return new CommonResult(productToWmsThreeService.systemUploadInterface(systemUpload));
    }

    @ApiOperation(value = "MES实时查询WMS库存", notes = "MES实时查询WMS库存")
    @PostMapping("/inventory_query")
    public CommonResult<List<InventoryReturn>> inventoryQuery(@RequestBody InventoryQuery inventoryQuery) {
        return CommonResult.success(productToWmsThreeService.inventoryQueryInterface(inventoryQuery));
    }


}
