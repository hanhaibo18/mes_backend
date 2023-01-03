package com.richfit.mes.produce.controller.heat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单工序Controller
 */
@Slf4j
@Api(value = "跟单工序管理", tags = {"跟单工序管理"})
@RestController
@RequestMapping("/api/produce/heat/track_item")
public class HeatTrackItemController extends BaseController {

    @Autowired
    public TrackItemService trackItemService;
    @Autowired
    public BaseServiceClient baseServiceClient;
    @Autowired
    public TrackHeadService trackHeadService;

    public static String TRACK_HEAD_ID_NULL_MESSAGE = "跟单ID不能为空！";
    public static String TRACK_ITEM_ID_NULL_MESSAGE = "跟单工序ID不能为空！";
    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败，请重试！";


    @ApiOperation(value = "预装炉工序查询", notes = "预装炉工序查询")
    @GetMapping("/item/precharge")
    public CommonResult<List<TrackItem>> selectTrackHead(String id, String trackId, String optVer, String productNo) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper();
        if (!StringUtils.isNullOrEmpty(trackId)) {
            queryWrapper.eq("track_head_id", trackId);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("product_no", productNo);
        }
        if (!StringUtils.isNullOrEmpty(optVer)) {
            queryWrapper.like("opt_ver", optVer);
        }
        queryWrapper.orderByAsc("sequence_order_by");
        return CommonResult.success(trackItemService.list(queryWrapper), SUCCESS_MESSAGE);
    }
}
