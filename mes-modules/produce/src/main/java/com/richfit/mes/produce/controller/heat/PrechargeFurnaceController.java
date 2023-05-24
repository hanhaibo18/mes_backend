package com.richfit.mes.produce.controller.heat;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author zhiqiang.lu
 * @Description 预装炉Controller
 */
@Slf4j
@Api(value = "预装炉", tags = {"预装炉"})
@RestController
@RequestMapping("/api/produce/heat/precharge/furnace")
public class PrechargeFurnaceController extends BaseController {


    @Autowired
    private TrackItemService trackItemService;



    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;

    @ApiOperation(value = "装炉")
    @PostMapping("/furnace_charging")
    public CommonResult furnaceCharging(@ApiParam(value = "保存信息", required = true) @RequestBody List<Assign> assignList,
                                        @ApiParam(value = "预装温度", required = true) @RequestParam String tempWork) {
        prechargeFurnaceService.furnaceCharging(assignList, tempWork);
        return CommonResult.success("装炉成功");
    }

    @ApiOperation(value = "装炉(热工)")
    @PostMapping("/furnace_charging_hot")
    public CommonResult furnaceChargingHot(@ApiParam(value = "保存信息", required = true) @RequestBody List<Assign> assignList,
                                        @ApiParam(value = "材质", required = false) @RequestParam String texture) {
        prechargeFurnaceService.furnaceChargingHot(assignList,texture);
        return CommonResult.success("装炉成功");
    }

    @ApiOperation(value = "预装炉删除")
    @PostMapping("/delete")
    public CommonResult delete(@ApiParam(value = "预装炉ID", required = true) @RequestParam(value = "id") Long id) {
        PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(id);
        if (!PrechargeFurnace.STATE_WKG.equals(prechargeFurnace.getStatus())) {
            throw new GlobalException("只能删除未开工的预装炉", ResultCode.FAILED);
        }
        List<Assign> assignList = prechargeFurnaceService.queryTrackItem(id);
        if (assignList.isEmpty()) {
            prechargeFurnaceService.removeById(id);
            return CommonResult.success("删除成功");
        } else {
            return CommonResult.failed("当前炉内还有生产数据，不能删除！");
        }
    }

    @ApiOperation(value = "装炉查询", tags = "不分页装炉列表查询")
    @PostMapping("/query")
    public CommonResult<List<PrechargeFurnace>> query(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) {
        QueryWrapper<PrechargeFurnace> queryWrapper = new QueryWrapper();
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getTempWork())) {
            int tempWorkZ = Integer.parseInt(dispatchingDto.getTempWork()) + Integer.parseInt(dispatchingDto.getTempWork1());
            int tempWorkQ = Integer.parseInt(dispatchingDto.getTempWork()) - Integer.parseInt(dispatchingDto.getTempWork1());
            //小于等于
            queryWrapper.le("temp_work", tempWorkZ);
            //大于等于
            queryWrapper.ge("temp_work", tempWorkQ);
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getStartTime())) {
            queryWrapper.ge("create_time",dispatchingDto.getStartTime());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getEndTime())) {
            queryWrapper.le("create_time",dispatchingDto.getEndTime());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getTexture())) {
            queryWrapper.eq("texture",dispatchingDto.getTexture());
        }
        if (dispatchingDto.getId()!=null) {
            queryWrapper.eq("id",dispatchingDto.getId());
        }
        queryWrapper.eq("site_id", SecurityUtils.getCurrentUser().getBelongOrgId());
        queryWrapper.in("status", new java.lang.String[]{"0", "1"});
        queryWrapper.orderByAsc("modify_time");
        return CommonResult.success(prechargeFurnaceService.list(queryWrapper));
    }

    @ApiOperation(value = "装炉查询分页", tags = "装炉查询分页")
    @PostMapping("/page/query")
    public CommonResult<Page<PrechargeFurnace>> pageQuery(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) {
        QueryWrapper<PrechargeFurnace> queryWrapper = new QueryWrapper();
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getTempWork())) {
            int tempWorkZ = Integer.parseInt(StringUtils.isNullOrEmpty(dispatchingDto.getTempWork()) ? "0" : dispatchingDto.getTempWork()) + Integer.parseInt(StringUtils.isNullOrEmpty(dispatchingDto.getTempWork1())?"0":dispatchingDto.getTempWork1());
            int tempWorkQ = Integer.parseInt(StringUtils.isNullOrEmpty(dispatchingDto.getTempWork()) ? "0" : dispatchingDto.getTempWork()) - Integer.parseInt(StringUtils.isNullOrEmpty(dispatchingDto.getTempWork1())?"0":dispatchingDto.getTempWork1());
            //小于等于
            queryWrapper.le("temp_work", tempWorkZ);
            //大于等于
            queryWrapper.ge("temp_work", tempWorkQ);
        }
        queryWrapper.ge(!StringUtils.isNullOrEmpty(dispatchingDto.getStartTime()), "date_format(modify_time, '%Y-%m-%d')", dispatchingDto.getStartTime())
                .le(!StringUtils.isNullOrEmpty(dispatchingDto.getEndTime()), "date_format(modify_time, '%Y-%m-%d')", dispatchingDto.getEndTime());
        if ("0,1".equals(dispatchingDto.getState())) {
            //查询本部门未开工的 和  自己开工的
            queryWrapper.and(wrapper3 -> wrapper3.and(wrapper4 -> wrapper4.eq("step_status", "0").apply("FIND_IN_SET('" + SecurityUtils.getCurrentUser().getBelongOrgId() + "',site_id)"))
                    .or(wrapper -> wrapper.eq("step_status", "1").and(wrapper2 -> wrapper2.eq("start_work_by", SecurityUtils.getCurrentUser().getUserId()))));
            queryWrapper.in("status", 0, 1);
        }


        if ("2".equals(dispatchingDto.getState())) {
            queryWrapper.eq("start_work_by", SecurityUtils.getCurrentUser().getUserId());
            queryWrapper.in("status", 2);
        }
        if (StringUtils.isNullOrEmpty(dispatchingDto.getOrderCol())) {
            queryWrapper.orderByAsc("modify_time");
        } else {
            OrderUtil.query(queryWrapper, dispatchingDto.getOrderCol(), dispatchingDto.getOrder());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getOptName())) {
            queryWrapper.eq("opt_name",dispatchingDto.getOptName());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getTexture())) {
            queryWrapper.eq("texture",dispatchingDto.getTexture());
        }
        if (dispatchingDto.getId()!=null) {
            queryWrapper.eq("id",dispatchingDto.getId());
        }
        if (dispatchingDto.getAssignStatus()!=null) {
            queryWrapper.eq("assignStatus",dispatchingDto.getAssignStatus());
        }
        return CommonResult.success(prechargeFurnaceService.page(new Page<>(dispatchingDto.getPage(), dispatchingDto.getLimit()), queryWrapper));
    }

    @ApiOperation(value = "装炉跟单工序查询", tags = "不分页装炉跟单工序查询")
    @GetMapping("/query/track/item")
    public CommonResult<List<Assign>> queryTrackItem(@ApiParam(value = "预装炉ID", required = true) @RequestParam Long id) {
        return CommonResult.success(prechargeFurnaceService.queryTrackItem(id));
    }

    @ApiOperation(value = "装炉跟单工序添加", tags = "装炉跟单工序添加")
    @PostMapping("/add/track/item")
    public CommonResult addTrackItem(@ApiParam(value = "跟单工序列表", required = true) @RequestBody List<Assign> assignList) {
        return CommonResult.success(prechargeFurnaceService.addTrackItem(assignList), "更新成功");
    }

    @ApiOperation(value = "装炉跟单工序添加(热工)", tags = "装炉跟单工序添加(热工)")
    @PostMapping("/add/track/item_hot")
    public CommonResult addTrackItemHot(@ApiParam(value = "跟单工序列表", required = true) @RequestBody List<Assign> assignList) {
        return CommonResult.success(prechargeFurnaceService.addTrackItemHot(assignList), "更新成功");
    }

    @ApiOperation(value = "装炉跟单工序删除", tags = "装炉跟单工序删除")
    @PostMapping("/delete/track/item")
    public CommonResult deleteTrackItem(@ApiParam(value = "跟单工序列表", required = true) @RequestBody List<Assign> assignList) {
        return CommonResult.success(prechargeFurnaceService.deleteTrackItem(assignList), "删除成功");
    }

    @ApiOperation(value = "冶炼配炉 根据材质分类合计钢水重量列表")
    @GetMapping("/total_weight_molten")
    public CommonResult<List> totalWeightMolten(String branchCode) {
        return CommonResult.success(prechargeFurnaceService.totalWeightMolten(branchCode));
    }

    @ApiOperation(value = "冶炼配炉 根据材质查询派工列表")
    @GetMapping("/query_assign_by_texture")
    public CommonResult<List<TrackItem>> queryAssignByTexture(String texture, String branchCode) {
        return CommonResult.success(prechargeFurnaceService.queryAssignByTexture(texture,branchCode));
    }

    @ApiOperation(value = "配炉未派工列表查询")
    @GetMapping("/assign_furnace_page_list")
    public CommonResult<Page> assignFurnacePageList(Long id, String texture, String endTime, String startTime, int page, int limit, String branchCode, String workblankType){
        QueryWrapper<PrechargeFurnace> prechargeFurnaceQueryWrapper = new QueryWrapper<>();
        prechargeFurnaceQueryWrapper.eq("branch_code",branchCode)
                .eq(!ObjectUtil.isEmpty(id),"id",id)
                .eq(!StringUtils.isNullOrEmpty(texture),"texture",texture)
                .ge(!StringUtils.isNullOrEmpty(startTime),"date_format(create_time, '%Y-%m-%d')", startTime)
                .le(!StringUtils.isNullOrEmpty(endTime),"date_format(create_time, '%Y-%m-%d')", endTime)
                .eq(!ObjectUtil.isEmpty(workblankType),"workblank_type",workblankType);
        return CommonResult.success(prechargeFurnaceService.page(new Page<>(page, limit), prechargeFurnaceQueryWrapper));
    }

    @ApiOperation(value = "配炉工序列表查询")
    @GetMapping("/furnace_item_list")
    public CommonResult<List> furnaceItemList(Long id){
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_current",1)
                .eq("precharge_furnace_id",id);
        return CommonResult.success(trackItemService.list(queryWrapper));
    }

    @ApiOperation(value = "配炉工序列表查询")
    @GetMapping("/furnace_item_list_YL")
    public CommonResult<List<TrackItem>> furnaceItemListYl(Long id) {
        return CommonResult.success(prechargeFurnaceService.getItemsByPrechargeFurnace(id));
    }
}
