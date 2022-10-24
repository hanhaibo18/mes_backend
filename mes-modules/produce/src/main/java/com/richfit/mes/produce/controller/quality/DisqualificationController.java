package com.richfit.mes.produce.controller.quality;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.common.model.produce.DisqualificationAttachment;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;
import com.richfit.mes.produce.entity.quality.QueryCheckDto;
import com.richfit.mes.produce.entity.quality.QueryInspectorDto;
import com.richfit.mes.produce.entity.quality.SignedRecordsVo;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.service.quality.DisqualificationAttachmentService;
import com.richfit.mes.produce.service.quality.DisqualificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: DisqualificationController.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年09月30日 14:40:00
 */
@Slf4j
@Api(value = "不合格品处理流程", tags = {"不合格品处理流程"})
@RestController
@RequestMapping("/api/produce/quality/disqualification")
public class DisqualificationController extends BaseController {

    @Resource
    private DisqualificationService disqualificationService;

    @Resource
    private DisqualificationAttachmentService attachmentService;

    @Resource
    private TrackItemService trackItemService;

    @ApiOperation(value = "待处理申请单", notes = "根据查询条件查询待处理申请单")
    @PostMapping("/queryInspector")
    private CommonResult<IPage<Disqualification>> queryInspector(@RequestBody QueryInspectorDto queryInspectorDto) {
        return CommonResult.success(disqualificationService.queryInspector(queryInspectorDto));
    }

    @ApiOperation(value = "创建/修改申请单", notes = "创建或修改不合格申请单接口")
    @PostMapping("/saveDisqualification")
    public CommonResult<Boolean> saveDisqualification(@RequestBody Disqualification disqualification) {
        return CommonResult.success(disqualificationService.saveOrUpdateDisqualification(disqualification));
    }

    @ApiOperation(value = "开单", notes = "发布申请单")
    @ApiImplicitParam(name = "id", value = "申请单Id", required = true, paramType = "query", dataType = "String")
    @GetMapping("/issueApplication")
    public CommonResult<Boolean> issueApplication(String id) {
        return CommonResult.success(disqualificationService.updateIsIssue(id, "1"));
    }

    @ApiOperation(value = "关单", notes = "关闭申请单")
    @ApiImplicitParam(name = "id", value = "申请单Id", required = true, paramType = "query", dataType = "String")
    @GetMapping("/closeApplication")
    public CommonResult<Boolean> closeApplication(String id) {
        return CommonResult.success(disqualificationService.updateIsIssue(id, "2"));
    }

    @ApiOperation(value = "查询申请单信息", notes = "根据工序Id查询申请单所用参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchCode", value = "车间", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/queryItem")
    public CommonResult<DisqualificationItemVo> queryItem(String tiId, String branchCode) {
        return CommonResult.success(trackItemService.queryItem(tiId, branchCode));
    }

    @ApiOperation(value = "查询质量检测部", notes = "第一次提交申请单查询质量检测部人员")
    @GetMapping("/queryUser")
    public CommonResult<List<TenantUserVo>> queryUser() {
        return CommonResult.success(disqualificationService.queryUser());
    }

    @ApiOperation(value = "查询未处理/已处理申请单", notes = "质检人员查询不合格品处理单查询接口")
    @PostMapping("/queryCheck")
    public CommonResult<IPage<Disqualification>> queryCheck(@RequestBody QueryCheckDto queryCheckDto) {
        return CommonResult.success(disqualificationService.queryCheck(queryCheckDto));
    }

    @ApiOperation(value = "查询意见列表", notes = "查询意见列表")
    @GetMapping(value = "/querySignedRecordsList")
    public CommonResult<List<SignedRecordsVo>> querySignedRecordsList(String disqualificationId) {
        return CommonResult.success(disqualificationService.querySignedRecordsList(disqualificationId));
    }

    @ApiOperation(value = "保存文件中间表数据", notes = "保存文件中间表数据")
    @PostMapping("/saveAttachment")
    public CommonResult<Boolean> saveAttachment(List<DisqualificationAttachment> attachments) {
        return CommonResult.success(attachmentService.saveAttachment(attachments));
    }
}
