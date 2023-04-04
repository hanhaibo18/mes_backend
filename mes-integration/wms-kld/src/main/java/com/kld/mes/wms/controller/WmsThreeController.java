package com.kld.mes.wms.controller;

import com.kld.mes.wms.service.ProductToWmsThreeService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.wms.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
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

    @ApiOperation(value = "WMS报检单上传MES", notes = "将WMS系统生成状态待报检的报检单上传MES，上传成功，WMS系统报检单状态更新已报检，上传不成功仍为待报检")
    @PostMapping("/reverse_inspection_doc_upload")
    public CommonResult<ApplicationResult> reverseInspectionDocUpload(@RequestBody ReverseInspectionDocUpload reverseInspectionDocUpload) {
        return new CommonResult(productToWmsThreeService.reverseInspectionDocUploadInterface(reverseInspectionDocUpload));
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
    public CommonResult<ApplicationResult> applyListUpload(@RequestBody ApplyListUpload applyListUpload) {
        return new CommonResult(productToWmsThreeService.applyListUploadInterface(applyListUpload));
    }

    @ApiOperation(value = "WMS入库信息上传MES", notes = "WMS系统参考MES申请单、外协产品、外购产品入库操作成功，将入库信息上传MES")
    @PostMapping("/reverse_input_database_upload")
    public CommonResult<ApplicationResult> reverseInputDatabaseUpload(@RequestBody ReverseInputDatabaseUpload reverseInputDatabaseUpload) {
        return new CommonResult(productToWmsThreeService.reverseInputDatabaseUploadInterface(reverseInputDatabaseUpload));
    }

    @ApiOperation(value = "WMS入库信息冲销上传MES", notes = "WMS系统将业务类型为MES申请单、外协产品、外购产品的入库记录冲销成功，将冲销信息上传MES后删除")
    @PostMapping("/reverse_input_database_cover_upload")
    public CommonResult<ApplicationResult> reverseInputDatabaseCoverUpload(@RequestBody ReverseInputDatabaseCoverUpload reverseInputDatabaseCoverUpload) {
        return new CommonResult(productToWmsThreeService.reverseInputDatabaseCoverUploadInterface(reverseInputDatabaseCoverUpload));
    }

    @ApiOperation(value = "MES领料单上传WMS", notes = "将MES系统领料单上传WMS，若单据类型为自动出库时，WMS系统要检查各行项目物料的在该工厂下的库存数量能否满足领料数量，需全部行项目物料库存都满足时，WMS系统自动生成出库单，物资库存减少，调用WMS领料单出库信息上传MES接口")
    @PostMapping("/material_requisition_upload")
    public CommonResult<ApplicationResult> materialRequisitionUpload(@RequestBody MaterialRequisitionUpload materialRequisitionUpload) {
        return new CommonResult(productToWmsThreeService.materialRequisitionUploadInterface(materialRequisitionUpload));
    }

    @ApiOperation(value = "WMS领料单关闭上传MES", notes = "WMS系统将领料单关闭后上传MES，支持整单关闭或部分行项目关闭")
    @PostMapping("/reverse_material_requisition_close_upload")
    public CommonResult<ApplicationResult> reverseMaterialRequisitionCloseUpload(@RequestBody ReverseMaterialRequisitionCloseUpload reverseMaterialRequisitionCloseUpload) {
        return new CommonResult(productToWmsThreeService.reverseMaterialRequisitionCloseUploadInterface(reverseMaterialRequisitionCloseUpload));
    }

    @ApiOperation(value = "MES领料单撤回上传WMS", notes = "MES系统将未出库的领料单撤回上传WMS，上传成功，WMS系统撤回的领料单或部分行项目删除")
    @PostMapping("/material_requisition_recall")
    public CommonResult<ApplicationResult> materialRequisitionRecall(@RequestBody MaterialRequisitionRecall materialRequisitionRecall) {
        return new CommonResult(productToWmsThreeService.materialRequisitionRecallInterface(materialRequisitionRecall));
    }

    @ApiOperation(value = "WMS出库信息上传MES", notes = "WMS系统参考MES领料单出库操作成功，将出库信息上传MES")
    @PostMapping("/reverse_output_database_upload")
    public CommonResult<ApplicationResult> reverseOutputDatabaseUpload(@RequestBody ReverseOutputDatabaseUpload reverseOutputDatabaseUpload) {
        return new CommonResult(productToWmsThreeService.reverseOutputDatabaseUploadInterface(reverseOutputDatabaseUpload));
    }

    @ApiOperation(value = "WMS出库信息冲销上传MES", notes = "WMS系统将MES领料单的出库记录冲销操作成功，将出库冲销信息上传MES")
    @PostMapping("/reverse_output_database_cover_upload")
    public CommonResult<ApplicationResult> reverseOutputDatabaseCoverUpload(@RequestBody ReverseOutputDatabaseCoverUpload reverseOutputDatabaseCoverUpload) {
        return new CommonResult(productToWmsThreeService.reverseOutputDatabaseCoverUploadInterface(reverseOutputDatabaseCoverUpload));
    }

    @ApiOperation(value = "MES计划清单锁定/解锁物资库存上传WMS", notes = "MES系统将锁定、释放的工厂锁定库存上传WMS")
    @PostMapping("/system_upload")
    public CommonResult<ApplicationResult> systemUpload(@RequestBody SystemUpload systemUpload) {
        return new CommonResult(productToWmsThreeService.systemUploadInterface(systemUpload));
    }

    @ApiOperation(value = "MES实时查询WMS库存", notes = "MES实时查询WMS库存")
    @PostMapping("/inventory_query")
    public CommonResult<T> inventoryQuery(@RequestBody InventoryQuery inventoryQuery) {
        return new CommonResult(productToWmsThreeService.inventoryQueryInterface(inventoryQuery));
    }



}
