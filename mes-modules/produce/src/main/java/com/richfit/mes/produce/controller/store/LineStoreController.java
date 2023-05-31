package com.richfit.mes.produce.controller.store;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.code.StoreInputTypeEnum;
import com.richfit.mes.common.model.code.StoreItemStatusEnum;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.store.LineStoreSum;
import com.richfit.mes.common.model.produce.store.LineStoreSumZp;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.aop.OperationLog;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.richfit.mes.produce.aop.LogConstant.CERTIFICATE;
import static com.richfit.mes.produce.aop.LogConstant.LINE_STORE;

/**
 * @author 王瑞
 * @Description 库存Controller
 */
@Slf4j
@Api(value = "库存管理", tags = {"库存管理接口"})
@RestController
@RequestMapping("/api/produce/line_store")
public class LineStoreController extends BaseController {

    private final static String WORKBLANK_NULL_MESSAGE = "编号不能为空!";
    private final static String CODE_EXITS = "编号已存在！";
    private final static String MATERIAL_CODE_NULL_MSG = "物料编号不能为空！";
    private final static String DRAWING_NO_NULL_MSG = "图号不能为空！";
    private final static String MATERIAL_NO_NOT_EXIST = "物料号不存在！";

    private final static String STATUS_NOT_RIGHT_FOR_EDIT = "料单当前状态不支持该操作";

    private final static String MATERIAL_TYPE_NOT_MATCH = "料单入库类型同选择的物料号类型不匹配";

    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败！";

    @Autowired
    private LineStoreService lineStoreService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private BaseServiceClient baseServiceClient;

    @Autowired
    private SystemServiceClient systemServiceClient;

    @Autowired
    private ActionService actionService;

    @Autowired
    private StoreAttachRelService storeAttachRelService;

    @Autowired
    private ErpServiceClient erpServiceClient;

    @ApiOperation(value = "投料入库信息", notes = "投料入库信息")
    @PostMapping("/feeding")
    public CommonResult feedingLineStore(@ApiParam(value = "料单Id数组") @RequestBody List<LineStore> lineStoreList) {
        //增加check逻辑  状态不是原始入库的，不能删除
        for (LineStore lineStore : lineStoreList) {
            //只有毛胚的物料才可以进行投料
            if ("0".equals(lineStore.getMaterialType())) {
                lineStore = erpServiceClient.storeSendFeeding(lineStore).getData();
                lineStoreService.updateById(lineStore);
            }
        }
        return CommonResult.success(true, SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "获取订单数量和剩余数量", notes = "获取订单数量和剩余数量")
    @PostMapping("/get_order_surplusNum")
    public HashMap<String, Integer> getOrderNumAndInNum(@ApiParam(value = "启始序列号") @RequestParam(required = false) String materialNo,
                                                        @ApiParam(value = "终止序列号") @RequestParam(required = false) String orderNo) {
        return lineStoreService.getOrderNumAndInNum(materialNo, orderNo);
    }


    @ApiOperation(value = "入库", notes = "毛坯或半成品/成品入库")
    @OperationLog(actionType = "0", actionItem = "3", argType = LINE_STORE)
    @PostMapping("/line_store")
    public CommonResult<LineStore> addLineStore(@ApiParam(value = "料单详情") @RequestBody LineStore lineStore,
                                                @ApiParam(value = "启始序列号") @RequestParam(required = false) String startNo,
                                                @ApiParam(value = "终止序列号") @RequestParam(required = false) String endNo,
                                                @ApiParam(value = "前缀字段") @RequestParam(required = false) String suffixNo,
                                                @ApiParam(value = "自动匹配生产订单") @RequestParam Boolean isAutoMatchProd,
                                                @ApiParam(value = "自动匹配采购订单") @RequestParam Boolean isAutoMatchPur,
                                                @ApiParam(value = "所选分公司") @RequestParam String branchCode) throws Exception {

        //用来存放开始编号前缀为0
        StringBuilder strartSuffix = new StringBuilder();
        Integer startNoOld = 0;
        Integer endNoOld = 0;

        //只有单件的时候才会使用启始序列号，只要是单件  则启始序列号必有
        if (lineStore != null && org.apache.commons.lang3.StringUtils.isNotEmpty(lineStore.getTrackType()) && "0".equals(lineStore.getTrackType())) {
            //前端不需要校验endNo有没有填写 如果没有填写endNo 则吧startNo给到endNo
            if (endNo == null || org.apache.commons.lang3.StringUtils.isEmpty(endNo)) {
                endNo = startNo;
            }
            //如果用Integer类型接收startNo和endNo输入数字 前面如果有0，
            // 则会将0给省略掉，所以换成用String，将0截取出来然后在拼接到开始编号前面
            startNoOld = Integer.valueOf(startNo);
            endNoOld = Integer.valueOf(endNo);

//            String[] split = startNo.split("");
//            List<String> strings = Arrays.asList(split);
            //判断第一位是不是0，如果不是直接跳出
//            for (String string : strings) {
//                if ("0".equals(string)) {
//                    strartSuffix.append(string);
//                } else {
//                    break;
//                }
//            }
        }


        if (StringUtils.isNullOrEmpty(lineStore.getWorkblankNo())) {
            return CommonResult.failed(WORKBLANK_NULL_MESSAGE);
        } else if (StringUtils.isNullOrEmpty(lineStore.getMaterialNo())) {
            return CommonResult.failed(MATERIAL_CODE_NULL_MSG);
        } else if (StringUtils.isNullOrEmpty(lineStore.getDrawingNo())) {
            return CommonResult.failed(DRAWING_NO_NULL_MSG);
            //校验物料号是否存在
        } else if (!isMaterialNoExist(lineStore.getMaterialNo())) {
            return CommonResult.failed(MATERIAL_NO_NOT_EXIST);
            //校验编号是否已存在，如存在，返回报错信息
        } else if (lineStoreService.checkCodeExist(lineStore, startNoOld, endNoOld, suffixNo, strartSuffix.toString())) {
            String message = lineStore.getMaterialType().equals(0) ? "毛坯" : "零（部）件";
            return CommonResult.failed(message + CODE_EXITS);
        } else if (!isMaterialTypeMatch(lineStore.getMaterialNo(), lineStore.getMaterialType())) {
            return CommonResult.failed(MATERIAL_TYPE_NOT_MATCH);
        } else {
            boolean bool = lineStoreService.addStore(lineStore, startNoOld, endNoOld, suffixNo, isAutoMatchProd, isAutoMatchPur, branchCode, strartSuffix.toString());
            orderService.orderDataUsed(lineStore.getBranchCode(), lineStore.getProductionOrder());
            if (bool) {
                return CommonResult.success(lineStore, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }

    }


    @ApiOperation(value = "入库(新)", notes = "毛坯或半成品/成品入库")
    @OperationLog(actionType = "0", actionItem = "3", argType = LINE_STORE)
    @PostMapping("/line_store_new")
    public CommonResult<LineStore> addLineStoreNew(@ApiParam(value = "料单详情") @RequestBody LineStore lineStore,
                                                   @ApiParam(value = "启始序列号") @RequestParam(required = false) String startNo,
                                                   @ApiParam(value = "终止序列号") @RequestParam(required = false) String endNo,
                                                   @ApiParam(value = "前缀字段") @RequestParam(required = false) String suffixNo,
                                                   @ApiParam(value = "0手动, 1自动匹配生产订单 2 逆向生成订单") @RequestParam String isAutoMatchProd,
                                                   @ApiParam(value = "自动匹配采购订单") @RequestParam Boolean isAutoMatchPur,
                                                   @ApiParam(value = "所选分公司") @RequestParam String branchCode) throws Exception {

        //用来存放开始编号前缀为0
        StringBuilder strartSuffix = new StringBuilder();
        Integer startNoOld = 0;
        Integer endNoOld = 0;

        //只有单件的时候才会使用启始序列号，只要是单件  则启始序列号必有
        if (lineStore != null && org.apache.commons.lang3.StringUtils.isNotEmpty(lineStore.getTrackType()) && "0".equals(lineStore.getTrackType())) {
            //前端不需要校验endNo有没有填写 如果没有填写endNo 则吧startNo给到endNo
            if (endNo == null || org.apache.commons.lang3.StringUtils.isEmpty(endNo)) {
                endNo = startNo;
            }
            //如果用Integer类型接收startNo和endNo输入数字 前面如果有0，
            // 则会将0给省略掉，所以换成用String，将0截取出来然后在拼接到开始编号前面
            if (startNo != null) {
                startNoOld = Integer.valueOf(startNo);
            }
            if (startNo != null) {
                endNoOld = Integer.valueOf(endNo);
            }
//            String[] split = startNo.split("");
//            List<String> strings = Arrays.asList(split);
            //判断第一位是不是0，如果不是直接跳出
//            for (String string : strings) {
//                if ("0".equals(string)) {
//                    strartSuffix.append(string);
//                } else {
//                    break;
//                }
//            }
        }


        if (StringUtils.isNullOrEmpty(lineStore.getWorkblankNo())) {
            return CommonResult.failed(WORKBLANK_NULL_MESSAGE);
        } else if (StringUtils.isNullOrEmpty(lineStore.getMaterialNo())) {
            return CommonResult.failed(MATERIAL_CODE_NULL_MSG);
        } else if (StringUtils.isNullOrEmpty(lineStore.getDrawingNo())) {
            return CommonResult.failed(DRAWING_NO_NULL_MSG);
            //校验物料号是否存在
        } else if (!isMaterialNoExist(lineStore.getMaterialNo())) {
            return CommonResult.failed(MATERIAL_NO_NOT_EXIST);
            //校验编号是否已存在，如存在，返回报错信息
        } else if (lineStoreService.checkCodeExist(lineStore, startNoOld, endNoOld, suffixNo, strartSuffix.toString())) {
            String message = lineStore.getMaterialType().equals(0) ? "毛坯" : "零（部）件";
            return CommonResult.failed(message + CODE_EXITS);
        } else if (!isMaterialTypeMatch(lineStore.getMaterialNo(), lineStore.getMaterialType())) {
            return CommonResult.failed(MATERIAL_TYPE_NOT_MATCH);
        } else {
            boolean bool = lineStoreService.addStoreNew(lineStore, startNoOld, endNoOld, suffixNo, isAutoMatchProd, isAutoMatchPur, branchCode, strartSuffix.toString());
            orderService.orderDataUsed(lineStore.getBranchCode(), lineStore.getProductionOrder());
            if (bool) {
                return CommonResult.success(lineStore, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }

    }


    @ApiOperation(value = "来料接收入库", notes = "根据合格证，实现半成品/成品入库")
    @OperationLog(actionType = "0", actionItem = "3", argType = CERTIFICATE)
    @PostMapping("/add_by_cert")
    public CommonResult<Boolean> addLineStoreByCert(@ApiParam(value = "合格证信息") @RequestBody Certificate cert) throws Exception {

        //TODO 逻辑验证
        Boolean b = lineStoreService.addStoreByCertTransfer(cert);

        return CommonResult.success(b);
    }

    @ApiOperation(value = "修改入库信息", notes = "修改入库信息")
    @OperationLog(actionType = "1", actionItem = "3", argType = LINE_STORE)
    @PutMapping("/line_store")
    public CommonResult<LineStore> updateLineStore(@ApiParam(value = "料单详情") @RequestBody LineStore lineStore,
                                                   @ApiParam(value = "所选分公司") @RequestParam String branchCode) {
        if (StringUtils.isNullOrEmpty(lineStore.getWorkblankNo())) {
            return CommonResult.failed(WORKBLANK_NULL_MESSAGE);
        } else if (StringUtils.isNullOrEmpty(lineStore.getDrawingNo())) {
            return CommonResult.failed(DRAWING_NO_NULL_MSG);
        } else if (!isStatusFinish(lineStore)) {
            return CommonResult.failed(STATUS_NOT_RIGHT_FOR_EDIT);
        } else if (!isMaterialNoExist(lineStore.getMaterialNo())) {
            return CommonResult.failed(MATERIAL_NO_NOT_EXIST);
        } else if (!isMaterialTypeMatch(lineStore.getMaterialNo(), lineStore.getMaterialType())) {
            return CommonResult.failed(MATERIAL_TYPE_NOT_MATCH);
        } else {
            boolean bool = false;
            bool = lineStoreService.updateById(lineStore);
            if (CollectionUtils.isNotEmpty(lineStore.getFileList())) {
                storeAttachRelService.updateStoreFile(lineStore.getId(), branchCode, lineStore.getFileList());
            }
            orderService.orderDataUsed(lineStore.getBranchCode(), lineStore.getProductionOrder());
            if (bool) {
                return CommonResult.success(lineStore, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "删除入库信息", notes = "删除入库信息")
    @DeleteMapping("/line_store")
    public CommonResult deleteLineStore(@ApiParam(value = "料单Id数组") @RequestBody List<LineStore> lineStoreList, HttpServletRequest request) {
        //增加check逻辑  状态不是原始入库的，不能删除
        for (LineStore lineStore : lineStoreList) {
            if (lineStore.getInputType().equals(StoreInputTypeEnum.CERT_ACCEPT.getCode())) {
                return CommonResult.failed("来料接收料单不能删除,编号:" + lineStore.getWorkblankNo());
            }
            if (!isStatusFinish(lineStore)) {
                return CommonResult.failed(STATUS_NOT_RIGHT_FOR_EDIT + ",编号:" + lineStore.getWorkblankNo());
            }
        }
        for (LineStore lineStore : lineStoreList) {
            lineStoreService.removeById(lineStore);
            actionService.saveAction(ActionUtil.buildAction
                    (lineStore.getBranchCode(), "2", "3", "删除入库，物料号:" + lineStore.getMaterialNo(), OperationLogAspect.getIpAddress(request)));
            orderService.orderDataUsed(lineStore.getBranchCode(), lineStore.getProductionOrder());
        }
        return CommonResult.success(true, SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "分页查询入库信息", notes = "根据图号、合格证号、物料编号分页查询入库信息")
    @GetMapping("/line_store")
    public CommonResult<IPage<LineStore>> selectLineStore(@ApiParam(value = "料单Id") @RequestParam(required = false) String id,
                                                          @ApiParam(value = "物料号") @RequestParam(required = false) String materialNo,
                                                          @ApiParam(value = "物料类型") @RequestParam(required = false) String materialType,
                                                          @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                          @ApiParam(value = "合格证号") @RequestParam(required = false) String certificateNo,
                                                          @ApiParam(value = "工作号") @RequestParam(required = false) String workNo,
                                                          @ApiParam(value = "订单号") @RequestParam(required = false) String productionOrder,
                                                          @ApiParam(value = "入库时间(起)") @RequestParam(required = false) String startTime,
                                                          @ApiParam(value = "入库时间(止)") @RequestParam(required = false) String endTime,
                                                          @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo,
                                                          @ApiParam(value = "料单状态") @RequestParam(required = false) String status,
                                                          @ApiParam(value = "数量") @RequestParam(required = false) Integer number,
                                                          @ApiParam(value = "跟踪方式") @RequestParam(required = false) String trackType,
                                                          @ApiParam(value = "排序方式") @RequestParam(required = false) String order,
                                                          @ApiParam(value = "排序字段") @RequestParam(required = false) String orderCol,
                                                          @ApiParam(value = "页码") @RequestParam int page,
                                                          @ApiParam(value = "每页记录数") @RequestParam int limit,
                                                          @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            queryWrapper.eq("material_type", materialType);
        }
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("status", status);
        } else {
            queryWrapper.notIn("status", 3);
        }
        if (number != null) {
            queryWrapper.eq("number", number);
        }
        if (!StringUtils.isNullOrEmpty(trackType)) {
            queryWrapper.eq("track_type", trackType);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(workNo)) {
            queryWrapper.like("work_no", workNo);
        }
        if (!StringUtils.isNullOrEmpty(productionOrder)) {
            queryWrapper.like("production_order", productionOrder);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.ge("in_Time", startTime + " 00:00:00");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.le("in_Time", endTime + " 23:59:59");
        }
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.like("certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(workblankNo)) {
            queryWrapper.like("workblank_no", workblankNo);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if ("desc".equals(order)) {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if ("asc".equals(order)) {
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("modify_time");
        }
        return CommonResult.success(fillBranchName(lineStoreService.page(new Page<LineStore>(page, limit), queryWrapper)), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "分页查询入库信息", notes = "根据图号、合格证号、物料编号分页查询入库信息")
    @GetMapping("/line_store_use")
    public CommonResult<IPage<LineStore>> selectLineStoreByUse(@ApiParam(value = "料单Id") @RequestParam(required = false) String id,
                                                               @ApiParam(value = "物料号") @RequestParam(required = false) String materialNo,
                                                               @ApiParam(value = "物料类型") @RequestParam(required = false) String materialType,
                                                               @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                               @ApiParam(value = "合格证号") @RequestParam(required = false) String certificateNo,
                                                               @ApiParam(value = "工作号") @RequestParam(required = false) String workNo,
                                                               @ApiParam(value = "入库时间(起)") @RequestParam(required = false) String startTime,
                                                               @ApiParam(value = "入库时间(止)") @RequestParam(required = false) String endTime,
                                                               @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo,
                                                               @ApiParam(value = "料单状态") @RequestParam(required = false) String status,
                                                               @ApiParam(value = "数量") @RequestParam(required = false) Integer number,
                                                               @ApiParam(value = "跟踪方式") @RequestParam(required = false) String trackType,
                                                               @ApiParam(value = "排序方式") @RequestParam(required = false) String order,
                                                               @ApiParam(value = "排序字段") @RequestParam(required = false) String orderCol,
                                                               @ApiParam(value = "页码") @RequestParam int page,
                                                               @ApiParam(value = "每页记录数") @RequestParam int limit,
                                                               @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            queryWrapper.eq("material_type", materialType);
        }
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (number != null) {
            queryWrapper.eq("number", number);
        }
        if (!StringUtils.isNullOrEmpty(trackType)) {
            queryWrapper.eq("track_type", trackType);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(workNo)) {
            queryWrapper.like("work_no", workNo);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.ge("in_Time", startTime);
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.le("in_Time", endTime);
        }
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.like("certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(workblankNo)) {
            queryWrapper.like("workblank_no", workblankNo);
        }
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.apply("number > use_num");
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if ("desc".equals(order)) {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if ("asc".equals(order)) {
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("id");
            queryWrapper.orderByDesc("modify_time");
        }
        return CommonResult.success(fillBranchName(lineStoreService.page(new Page<LineStore>(page, limit), queryWrapper)), SUCCESS_MESSAGE);
    }

    private IPage<LineStore> fillBranchName(IPage<LineStore> certificateIPage) {

        List<ItemParam> itemParamList = systemServiceClient.selectItemClass("stockFrom", "").getData();
        for (LineStore lineStore : certificateIPage.getRecords()) {
            for (ItemParam b : itemParamList) {
                if (b.getCode().equals(lineStore.getMaterialSource())) {
                    lineStore.setMaterialSourceName(b.getLabel());
                    break;
                }
            }
        }
        return certificateIPage;
    }

    @ApiOperation(value = "通过id查询库存", notes = "通过id查询库存")
    @GetMapping("/line_store/{id}")
    public CommonResult<LineStore> selectLineStore(@ApiParam(value = "料单Id", required = true) @PathVariable String id) {
        return CommonResult.success(lineStoreService.LineStoreById(id));
    }

    @ApiOperation(value = "查询入库总览", notes = "根据物料号查询入库总览")
    @GetMapping("/line_store/group")
    public CommonResult<IPage<LineStoreSum>> selectLineStoreGroup(@ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                                  @ApiParam(value = "物料号") @RequestParam(required = false) String materialNo,
                                                                  @ApiParam(value = "页码") @RequestParam int page,
                                                                  @ApiParam(value = "每页数") @RequestParam int limit,
                                                                  @ApiParam(value = "当前分公司") @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
//        if (!StringUtils.isNullOrEmpty(materialType)) {
//            queryWrapper.eq("material_type", materialType);
//        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", materialNo);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);

        queryWrapper.orderByAsc("drawing_no");
        return CommonResult.success(lineStoreService.selectGroup(new Page<LineStore>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "装配库存总览", notes = "根据物料号查询装配库存总览")
    @GetMapping("/sum/zp")
    public CommonResult<IPage<LineStoreSumZp>> selectLineStoreSumZp(@ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                                    @ApiParam(value = "物料号") @RequestParam(required = false) String materialNo,
                                                                    @ApiParam(value = "页码") @RequestParam int page,
                                                                    @ApiParam(value = "每页数") @RequestParam int limit,
                                                                    @ApiParam(value = "当前分公司") @RequestParam String branchCode) throws Exception {

        Map parMap = new HashMap();
        parMap.put("branchCode", branchCode);
        parMap.put("tenantId", SecurityUtils.getCurrentUser().getTenantId());
        parMap.put("drawingNo", drawingNo);
        parMap.put("materialNo", materialNo);

        IPage<LineStoreSumZp> ipage = lineStoreService.queryLineStoreSumZp(new Page<LineStoreSumZp>(page, limit), parMap);

        return CommonResult.success(ipage);
    }

    @ApiOperation(value = "装配当前库数量", notes = "根据物料号查询装配库存数量")
    @GetMapping("/sum/zp_num")
    public CommonResult<Integer> selectLineStoreSumZpNumber(@ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                            @ApiParam(value = "物料号") @RequestParam(required = false) String materialNo) throws Exception {

        Map parMap = new HashMap();
        parMap.put("drawingNo", drawingNo);
        parMap.put("materialNo", materialNo);
        Integer number = lineStoreService.queryLineStoreSumZpNumber(parMap);

        return CommonResult.success(number);
    }

    @ApiOperation(value = "查询入库信息", notes = "根据图号、合格证号、物料编号查询入库信息")
    @GetMapping("/line_store/list")
    public CommonResult<List<LineStore>> selectLineStoreList(@ApiParam(value = "料单Id") @RequestParam(required = false) String id,
                                                             @ApiParam(value = "料单类型") @RequestParam(required = false) String materialType,
                                                             @ApiParam(value = "物料码") @RequestParam(required = false) String materialNo,
                                                             @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                             @ApiParam(value = "合格证号") @RequestParam(required = false) String certificateNo,
                                                             @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo,
                                                             @ApiParam(value = "跟踪方式") @RequestParam(required = false) String trackType,
                                                             @ApiParam(value = "数量") @RequestParam(required = false) Integer number,
                                                             @ApiParam(value = "已用数量") @RequestParam(required = false) Integer userNum,
                                                             @ApiParam(value = "料单状态") @RequestParam(required = false) String status,
                                                             @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        if (!StringUtils.isNullOrEmpty(materialType)) {
            queryWrapper.eq("material_type", materialType);
        }
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.like("certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(workblankNo)) {
            queryWrapper.like("workblank_no", workblankNo);
        }
        if (!StringUtils.isNullOrEmpty(trackType)) {
            queryWrapper.eq("track_type", trackType);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (number != null) {
            queryWrapper.eq("number", number);
        }
        if (userNum != null && userNum > 0) {
            queryWrapper.ge("number - use_num", userNum);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.orderByDesc("create_time");
        return CommonResult.success(lineStoreService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询入库信息", notes = "根据图号、合格证号、物料编号查询入库信息")
    @GetMapping("/line_store/list/workblankNo")
    public CommonResult<List<LineStore>> selectLineStoreListWorkblankNo(@ApiParam(value = "料单类型") @RequestParam(required = false) String materialType,
                                                                        @ApiParam(value = "物料码") @RequestParam(required = false) String materialNo,
                                                                        @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                                        @ApiParam(value = "合格证号") @RequestParam(required = false) String certificateNo,
                                                                        @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo,
                                                                        @ApiParam(value = "跟踪方式") @RequestParam(required = false) String trackType,
                                                                        @ApiParam(value = "数量") @RequestParam(required = false) Integer number,
                                                                        @ApiParam(value = "可使用数量") @RequestParam(required = false) Integer usableNumber,
                                                                        @ApiParam(value = "料单状态") @RequestParam(required = false) String status,
                                                                        @ApiParam(value = "分公司", required = true) @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        if (!StringUtils.isNullOrEmpty(materialType)) {
            queryWrapper.eq("material_type", materialType);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.like("certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(workblankNo)) {
            queryWrapper.eq("workblank_no", workblankNo);
        }
        if (!StringUtils.isNullOrEmpty(trackType)) {
            queryWrapper.eq("track_type", trackType);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (number != null && number > 0) {
            queryWrapper.eq("number", number);
        }
        if (usableNumber != null && usableNumber > 0) {
            queryWrapper.ge("number - use_num", usableNumber);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.orderByDesc("create_time");
        return CommonResult.success(lineStoreService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "物料完工", notes = "物料完工")
    @GetMapping("/line_store/finish")
    public CommonResult<Boolean> finishProduct(@ApiParam(value = "跟单号") @RequestParam String trackNo,
                                               @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        queryWrapper.eq("track_no", trackNo);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        TrackHead trackHead = trackHeadService.getOne(queryWrapper);
        boolean bool = lineStoreService.changeStatus(trackHead);
        if (bool) {
            return CommonResult.success(bool, SUCCESS_MESSAGE);
        }
        return CommonResult.failed(FAILED_MESSAGE);
    }

    @ApiOperation(value = "修改状态", notes = "修改产品状态")
    @GetMapping("/line_store/change_status")
    public CommonResult<Boolean> changeStatus(@ApiParam(value = "毛坯号") @RequestParam String workblankNo,
                                              @ApiParam(value = "状态") @RequestParam String status,
                                              @ApiParam(value = "分公司") @RequestParam String branchCode) {
        UpdateWrapper<LineStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", status);
        updateWrapper.eq("workblank_no", workblankNo);
        updateWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        updateWrapper.eq("branch_code", branchCode);
        boolean bool = lineStoreService.update(updateWrapper);
        if (bool) {
            return CommonResult.success(bool, SUCCESS_MESSAGE);
        }
        return CommonResult.failed(FAILED_MESSAGE);
    }

    @ApiOperation(value = "查询产品使用的毛坯信息", notes = "根据图号查询产品使用的毛坯信息")
    @GetMapping("/line_store/workblank")
    public CommonResult<LineStore> selectWorkblankByTrackNo(@ApiParam(value = "跟单号") @RequestParam String trackNo,
                                                            @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        queryWrapper.eq("track_no", trackNo);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        TrackHead trackHead = trackHeadService.getOne(queryWrapper);
        LineStore lineStore = new LineStore();
        if (trackHead != null && !StringUtils.isNullOrEmpty(trackHead.getUserProductNo())) {
            QueryWrapper<LineStore> wrapper = new QueryWrapper<LineStore>();
            wrapper.eq("workblank_no", trackHead.getUserProductNo());
            wrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            lineStore = lineStoreService.getOne(wrapper);
        }
        return CommonResult.success(lineStore, SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "分页查询产品装配信息", notes = "根据跟踪类型，产品编号分页查询产品装配信息")
    @GetMapping("/line_store/product")
    public CommonResult<IPage<LineStore>> selectLineStoreByProduct(@ApiParam(value = "跟踪类型") @RequestParam(required = false) String trackType,
                                                                   @ApiParam(value = "物料类型") @RequestParam(required = false) String materialType,
                                                                   @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo,
                                                                   @ApiParam(value = "状态") @RequestParam(required = false) String status,
                                                                   @ApiParam(value = "页码") @RequestParam int page,
                                                                   @ApiParam(value = "条数") @RequestParam int limit,
                                                                   @ApiParam(value = "分公司") @RequestParam String branchCode) {
        QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
        if (!StringUtils.isNullOrEmpty(trackType)) {
            queryWrapper.eq("ls.track_type", trackType);
        }
        if (!StringUtils.isNullOrEmpty(workblankNo)) {
            queryWrapper.like("ls.workblank_no", workblankNo);
        }
        if (!StringUtils.isNullOrEmpty(materialType)) {
            queryWrapper.eq("ls.material_type", materialType);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("ls.status", status);
        }
        queryWrapper.eq("ls.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("ls.branch_code", branchCode);
        return CommonResult.success(lineStoreService.selectLineStoreByProduce(new Page<LineStore>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }


    @ApiOperation(value = "导入料单", notes = "根据Excel文档导入物料")
    @PostMapping("/import_excel")
    public CommonResult importExcel(@ApiIgnore HttpServletRequest request,
                                    @ApiParam(value = "文件") @RequestParam("file") MultipartFile file) {
        CommonResult result = null;
        //封装证件信息实体类
        String[] fieldNames = {"workblankNo", "materialNo", "materialName", "productName", "materialDesc", "materialType", "drawingNo", "texture", "weight", "trackType", "number", "certificateNo", "workNo", "materialSource", "batchNo", "testBarType", "testBarNumber", "productionOrder", "purchaseOrder", "contractNo", "replaceMaterial", "beforehandAssigned", "prevTrackNum"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<LineStore> list = ExcelUtils.importExcel(excelFile, LineStore.class, fieldNames, 1, 0, 0, tempName.toString());
            FileUtils.delete(excelFile);

            list = list.stream().filter(item -> item.getMaterialNo() != null).collect(Collectors.toList());
            QueryWrapper<LineStore> queryWrapper = new QueryWrapper<>();
            List<LineStore> lineStores = new ArrayList<>();
            list.forEach(item -> {
                if ("毛坯".equals(item.getMaterialType())) {
                    item.setMaterialType("0");
                } else if ("半成品/成品".equals(item.getMaterialType())) {
                    item.setMaterialType("1");
                }
                if ("单件".equals(item.getTrackType())) {
                    item.setTrackType("0");
                    item.setNumber(1);
                } else if ("批次".equals(item.getTrackType())) {
                    item.setTrackType("1");
                    if (item.getNumber() <= 0) {
                        item.setNumber(1);
                    }
                }

                item.setUseNum(0);
                item.setStatus("1");
                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                item.setBranchCode(SecurityUtils.getCurrentUser().getBelongOrgId());
                item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                item.setCreateTime(new Date());
                item.setInTime(new Date());

                queryWrapper.eq("workblank_no", item.getWorkblankNo());
                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

                List<LineStore> l = lineStoreService.list(queryWrapper);
                if (l == null || l.size() == 0) {
                    CommonResult<List<Product>> commonResult = baseServiceClient.selectProduct(item.getTenantId(), item.getMaterialNo(), item.getDrawingNo(), item.getMaterialType());
                    List<Product> pList = commonResult.getData();
                    if (pList != null && pList.size() > 0) {
                        lineStores.add(item);
                    }
                }
            });

            boolean bool = lineStoreService.saveBatch(lineStores);
            if (bool) {

                return CommonResult.success(null, "导入成功!");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        } catch (Exception e) {
            return CommonResult.failed("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "导出库存信息", notes = "通过Excel文档导出库存信息")
    @GetMapping("/export_excel")
    public void exportExcel(@ApiParam(value = "物料号") @RequestParam(required = false) String materialNo, @ApiParam(value = "物料类型") @RequestParam(required = false) String materialType,
                            @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo, @ApiParam(value = "合格证编号") @RequestParam(required = false) String certificateNo,
                            @ApiParam(value = "毛坯号") @RequestParam(required = false) String workblankNo, @ApiParam(value = "状态") @RequestParam(required = false) String status,
                            @ApiParam(value = "分公司") @RequestParam String branchCode, @ApiIgnore HttpServletResponse rsp) {
        try {
            QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
            if (!StringUtils.isNullOrEmpty(materialNo)) {
                queryWrapper.eq("material_no", materialNo);
            }
            if (!StringUtils.isNullOrEmpty(materialType)) {
                queryWrapper.eq("material_type", materialType);
            }
            if (!StringUtils.isNullOrEmpty(status)) {
                queryWrapper.eq("status", status);
            }
            if (!StringUtils.isNullOrEmpty(drawingNo)) {
                queryWrapper.like("drawing_no", drawingNo);
            }
            if (!StringUtils.isNullOrEmpty(certificateNo)) {
                queryWrapper.like("certificate_no", certificateNo);
            }
            if (!StringUtils.isNullOrEmpty(workblankNo)) {
                queryWrapper.like("workblank_no", workblankNo);
            }


            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.eq("branch_code", branchCode);
            queryWrapper.orderByDesc("create_time");
            List<LineStore> list = lineStoreService.list(queryWrapper);
            for (LineStore lineStore : list) {
                if ("0".equals(lineStore.getMaterialType()) && lineStore.getMaterialType() != null) {
                    lineStore.setMaterialType("毛坯");
                } else if ("1".equals(lineStore.getMaterialType()) && lineStore.getMaterialType() != null) {
                    lineStore.setMaterialType("半成品/成品");
                }
                if ("0".equals(lineStore.getTrackType()) && lineStore.getTrackType() != null) {
                    lineStore.setTrackType("单件");
                } else if ("1".equals(lineStore.getTrackType()) && lineStore.getTrackType() != null) {
                    lineStore.setTrackType("批次");
                }
                if ("0".equals(lineStore.getTestBarType()) && lineStore.getTestBarType() != null) {
                    lineStore.setTestBarType("普通试棒");
                } else if ("1".equals(lineStore.getTestBarType()) && lineStore.getTestBarType() != null) {
                    lineStore.setTestBarType("基尔试棒");
                }
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "物料信息_" + format.format(new Date()) + ".xlsx";


            String[] columnHeaders = {"编号", "物料号", "物料名称", "产品名称", "物料描述", "物料类型", "图号", "材质", "重量", "跟踪类型", "数量", "合格证编号", "工作号", "物料来源", "炉号", "试棒类型", "试棒数量", "生产订单编号", "采购订单编号", "合同编号", "代用材料", "预先派工", "上工序跟单号"};

            String[] fieldNames = {"workblankNo", "materialNo", "materialName", "productName", "materialDesc", "materialType", "drawingNo", "texture", "weight", "trackType", "number", "certificateNo", "workNo", "materialSource", "batchNo", "testBarType", "testBarNumber", "productionOrder", "purchaseOrder", "contractNo", "replaceMaterial", "beforehandAssigned", "prevTrackNum"};

            //export
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @ApiOperation(value = "flowid查询产品信息", notes = "通过flowid查询产品信息")
    @GetMapping("/list_flowid")
    public CommonResult<List<LineStore>> listByFlowId(@ApiParam(value = "flowId", required = true) @RequestParam String flowId,
                                                      @ApiParam(value = "追溯类型，0物料，1产品") @RequestParam(required = false) String type) throws Exception {
        List<LineStore> fileIdList = lineStoreService.listByFlowId(flowId, type);
        return CommonResult.success(fileIdList);

    }

    @ApiOperation(value = "下载质量资料id", notes = "通过料单Id，查询质量资料Id")
    @GetMapping("/query_quality_file_ids/{id}")
    public CommonResult<List<String>> downloadQualityFile(@ApiIgnore HttpServletResponse response, @ApiParam(value = "料单Id", required = true) @PathVariable String id) throws Exception {

        List<String> fileIdList = lineStoreService.queryStoreFileIdList(id);

        return CommonResult.success(fileIdList);

    }

    private boolean isStatusFinish(LineStore lineStore) {
        return lineStore.getStatus().equals(StoreItemStatusEnum.FINISH.getCode());
    }

    /**
     * 校验物料号是否在物料表中存在
     */
    private boolean isMaterialNoExist(String materialNo) {
        CommonResult<List<Product>> result = baseServiceClient.selectProduct(null, materialNo, null, null);
        return result.getData().size() > 0;
    }

    /**
     * 校验物料号对应的物料类型是否和 传入的料单物料类型对应
     * 物料表 物料类型  0铸件 1锻件 2精铸件 3成品 4下料 5模型 6半成品
     * 料单表 物料类型 0毛坯             1半成品/成品
     */
    private boolean isMaterialTypeMatch(String materialNo, String materialType) {

        CommonResult<List<Product>> result = baseServiceClient.selectProduct(null, materialNo, null, null);
        boolean isMatch = false;
        if (!result.getData().isEmpty()) {
            Product product = result.getData().get(0);
            String productType = product.getMaterialType();
            if ("0".equals(materialType)) {
                isMatch = !"3".equals(productType);
            } else if ("1".equals(materialType)) {
                isMatch = "3".equals(productType);
            }

        }
        return isMatch;
    }

    public static void main(String[] args) {
        Integer startNoOld = Integer.valueOf("06");
        Integer endNoOld = Integer.valueOf("11");

        System.out.println(startNoOld);
    }

}
