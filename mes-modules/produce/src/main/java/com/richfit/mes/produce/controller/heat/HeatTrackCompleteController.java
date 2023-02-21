package com.richfit.mes.produce.controller.heat;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.heat.CompleteUserInfoDto;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.entity.heat.HeatCompleteDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.service.heat.HeatTrackCompleteService;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author renzewen
 * @Description 跟单报工Controller
 */
@Slf4j
@Api(value = "热工报工", tags = {"热工报工"})
@RestController
@RequestMapping("/api/produce/heat/complete")
public class HeatTrackCompleteController extends BaseController {

    @Autowired
    private HeatTrackCompleteService heatTrackCompleteService;
    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private TrackHeadService headService;


    @ApiOperation(value = "报工")
    @PostMapping("/saveComplete")
    public CommonResult<Boolean> saveComplete(@ApiParam(value = "查询条件", required = true) @RequestBody HeatCompleteDto heatCompleteDto) throws Exception {
        return CommonResult.success(heatTrackCompleteService.saveComplete(heatCompleteDto));
    }

    @ApiOperation(value = "编辑报工")
    @PostMapping("/updateComplete")
    public CommonResult<Boolean> updateComplete(@RequestBody HeatCompleteDto heatCompleteDto) throws Exception {
        List<TrackComplete> trackCompleteList = heatCompleteDto.getTrackCompleteList();
        return CommonResult.success(heatTrackCompleteService.updateComplete(trackCompleteList));
    }


    @ApiOperation(value = "开工")
    @GetMapping("/startWork")
    public CommonResult<Boolean> startWork(@ApiParam(value = "预装炉id", required = true) @RequestParam String prechargeFurnaceId) {
        return CommonResult.success(heatTrackCompleteService.startWork(prechargeFurnaceId));
    }

    @ApiOperation(value = "根据预装炉id获取报工信息")
    @GetMapping("/getCompleteInfoByFuId")
    public CommonResult<Map<String, Object>> getCompleteInfoByFuId(@ApiParam(value = "预装炉id", required = true) String prechargeFurnaceId) {
        return CommonResult.success(heatTrackCompleteService.getCompleteInfoByFuId(prechargeFurnaceId));
    }

    @ApiOperation(value = "（已报工）分页查询已报工信息")
    @PostMapping("/alreadyCompletePage")
    public CommonResult alreadyCompletePage(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto) {
        TenantUserVo data = systemServiceClient.getUserById(SecurityUtils.getCurrentUser().getUserId()).getData();
        if (ObjectUtil.isEmpty(data)) {
            throw new GlobalException("用户不存在", ResultCode.FAILED);
        }
        //当前用户报过的步骤信息
        QueryWrapper<TrackComplete> completeQueryWrapper = new QueryWrapper<>();
        completeQueryWrapper.eq("complete_by", data.getUserAccount());
        List<TrackComplete> completeList = heatTrackCompleteService.list(completeQueryWrapper);
        //预装炉id集合
        List<String> fuIds = new ArrayList<>(completeList.stream().map(TrackComplete::getPrechargeFurnaceId).collect(Collectors.toSet()));
        if (fuIds.size() > 0) {
            QueryWrapper<PrechargeFurnace> prechargeFurnaceQueryWrapper = new QueryWrapper<>();
            if (!StringUtils.isNullOrEmpty(dispatchingDto.getTempWork())) {
                int tempWorkZ = Integer.parseInt(StringUtils.isNullOrEmpty(dispatchingDto.getTempWork()) ? "0" : dispatchingDto.getTempWork()) + Integer.parseInt(dispatchingDto.getTempWork1());
                int tempWorkQ = Integer.parseInt(StringUtils.isNullOrEmpty(dispatchingDto.getTempWork()) ? "0" : dispatchingDto.getTempWork()) - Integer.parseInt(dispatchingDto.getTempWork1());
                //小于等于
                prechargeFurnaceQueryWrapper.le("temp_work", tempWorkZ);
                //大于等于
                prechargeFurnaceQueryWrapper.ge("temp_work", tempWorkQ);
            }
            prechargeFurnaceQueryWrapper.in("id", fuIds);
            if (StringUtils.isNullOrEmpty(dispatchingDto.getOrderCol())) {
                prechargeFurnaceQueryWrapper.orderByAsc("modify_time");
            } else {
                OrderUtil.query(prechargeFurnaceQueryWrapper, dispatchingDto.getOrderCol(), dispatchingDto.getOrder());
            }

            return CommonResult.success(prechargeFurnaceService.page(new Page<PrechargeFurnace>(dispatchingDto.getPage(), dispatchingDto.getLimit()), prechargeFurnaceQueryWrapper));

        }
        return CommonResult.success(new Page<PrechargeFurnace>());
    }


    @ApiOperation(value = "（已报工）已报工根据预装炉id查询当前用户报工的步骤及用户")
    @GetMapping("/queryStepListByFuId")
    public CommonResult<Map> queryStepListByFuId(@ApiParam(value = "预装炉id", required = true) @RequestParam String id) {
        TenantUserVo data = systemServiceClient.getUserById(SecurityUtils.getCurrentUser().getUserId()).getData();
        if (ObjectUtil.isEmpty(data)) {
            throw new GlobalException("用户不存在", ResultCode.FAILED);
        }
        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("precharge_furnace_id", id)
                .eq("complete_by", data.getUserAccount())
                .orderByAsc("create_time");
        List<TrackComplete> completes = heatTrackCompleteService.queryList(queryWrapper);
        List<TrackComplete> stepList = new ArrayList<>();
        List<TrackComplete> userList = new ArrayList<>();
        //根据步骤分组id分组 得到步骤集合
        Map<String, List<TrackComplete>> stepGroup = completes.stream().collect(Collectors.groupingBy(TrackComplete::getStepGroupId));
        stepGroup.forEach((key, value) -> {
            stepList.add(value.get(0));
            for (TrackComplete trackComplete : value) {
                userList.add(trackComplete);
            }
        });
        Map<String, List<TrackComplete>> returnMap = new HashMap<>();
        //每一步骤人员列表
        for (TrackComplete complete : stepList) {
            List<CompleteUserInfoDto> stepUserInfos = new ArrayList<>();
            List<TrackComplete> completes1 = stepGroup.get(complete.getStepGroupId());
            //改步骤下的报工信息 按照跟单工序分组
            Map<String, List<TrackComplete>> itemStepCompleteInfos = completes1.stream().collect(Collectors.groupingBy(TrackComplete::getTiId));
            //查询步骤的报工人员（此步骤下的跟单工序 报工信息一直  所以取第一个的就行）
            if(itemStepCompleteInfos.size()>0){
                for (Map.Entry<String, List<TrackComplete>> itemStepCompleteInfo : itemStepCompleteInfos.entrySet()) {
                    for (TrackComplete trackComplete : itemStepCompleteInfo.getValue()) {
                        CompleteUserInfoDto completeUserInfoDto = new CompleteUserInfoDto();
                        BeanUtils.copyProperties(trackComplete, completeUserInfoDto);
                        stepUserInfos.add(completeUserInfoDto);
                    }
                    complete.setUserInfos(stepUserInfos);
                    break;
                }
            }
        }

        stepList.sort((t1, t2) -> t1.getCreateTime().compareTo(t2.getCreateTime()));
        userList.sort((t1, t2) -> t1.getCreateTime().compareTo(t2.getCreateTime()));

        returnMap.put("stepList", stepList);
        returnMap.put("userList", userList);
        return CommonResult.success(returnMap);
    }

    @ApiOperation(value = "（已报工）根据步骤id查询跟单工序信息")
    @GetMapping("/queryItemListByStepGroupId")
    public CommonResult<List<TrackItem>> queryItemListByStepGroupId(@ApiParam(value = "步骤分组id（stepGroupId）", required = true) @RequestParam String stepGroupId) {
        TrackComplete trackComplete = heatTrackCompleteService.getById(stepGroupId);
        QueryWrapper<TrackComplete> wrapper = new QueryWrapper<>();
        wrapper.eq("step_group_id", trackComplete.getStepGroupId());
        List<TrackComplete> stepCompletes = heatTrackCompleteService.list(wrapper);
        List<String> itemIds = stepCompletes.stream().map(TrackComplete::getTiId).collect(Collectors.toList());
        if (itemIds.size() > 0) {
            QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
            trackItemQueryWrapper.in("id", itemIds);
            List<TrackItem> list = trackItemService.list(trackItemQueryWrapper);
            for (TrackItem trackItem : list) {
                TrackHead head = headService.getById(trackItem.getTrackHeadId());
                trackItem.setTrackNo(head.getTrackNo());
                trackItem.setProductName(head.getProductName());
                trackItem.setWeight(head.getWeight());
            }
            return CommonResult.success(list);

        }
        return CommonResult.success(new ArrayList<TrackItem>());
    }

    @ApiOperation(value = "（已报工）根据预装炉id导出热处理标签excel")
    @GetMapping("/exportHeatTrackLabel")
    public void exportHeatTrackLabel(HttpServletResponse response, @ApiParam(value = "预装炉id", required = true) @RequestParam String id ) throws IOException {
        trackItemService.exportHeatTrackLabel(response, id);
    }

    @ApiOperation(value = "（已报工）人员批量删除")
    @PostMapping("/deleteCompleteBy")
    public CommonResult<Boolean> deleteCompleteBy(@ApiParam(value = "人员列表的ids", required = true) @RequestBody List<String> ids) {
        if (!ObjectUtil.isEmpty(ids) && ids.size() > 0) {
            QueryWrapper<TrackComplete> wrapper = new QueryWrapper<>();
            wrapper.in("id", ids);
            return CommonResult.success(heatTrackCompleteService.remove(wrapper));
            //todo 删除人员得重新计算工时

        }
        return CommonResult.failed("请选择要删除的人员！");
    }

    @ApiOperation(value = "（已报工）报工信息编辑")
    @PostMapping("/updateCompleteInfo")
    public CommonResult<Boolean> updateCompleteInfo(@ApiParam(value = "报工信息", required = true) @RequestBody TrackComplete trackComplete) {
        //步骤id
        String stepGroupId = trackComplete.getStepGroupId();
        //根据步骤id获取要修改的报工信息
        QueryWrapper<TrackComplete> wrapper = new QueryWrapper<>();
        wrapper.eq("step_group_id", stepGroupId);
        List<TrackComplete> stepCompletes = heatTrackCompleteService.list(wrapper);
        for (TrackComplete complete : stepCompletes) {
            complete.setWaterTempera(trackComplete.getWaterTempera());
            complete.setOilTempera(trackComplete.getOilTempera());
            complete.setNeurogenTempera(trackComplete.getNeurogenTempera());
            complete.setFurnaceCool(trackComplete.getFurnaceCool());
            complete.setWaterCool(trackComplete.getWaterCool());
            complete.setVacancyCool(trackComplete.getVacancyCool());
            complete.setOilCool(trackComplete.getOilCool());
            complete.setNeurogenCool(trackComplete.getNeurogenCool());
        }
        return CommonResult.success(heatTrackCompleteService.updateBatchById(stepCompletes));
    }

    /**
     * @return
     */
    @ApiOperation(value = "热工工时统计查询接口", notes = "热工工时统计查询接口")
    @GetMapping("/pageOptimize")
    public CommonResult<Map<String, Object>> pageOptimize(String trackNo, String startTime, String endTime, String branchCode, String workNo) {
        return CommonResult.success(heatTrackCompleteService.queryTrackCompleteList(trackNo, startTime, endTime, branchCode, workNo));
    }

    /**
     * @return
     */
    @ApiOperation(value = "热工报工获取炉号（设备名称不带DZ）", notes = "热工报工获取炉号")
    @GetMapping("/getFurnaceNo")
    public CommonResult<String> getFurnaceNo(String deviceName,String branchCode,String code){
        return CommonResult.success(heatTrackCompleteService.getFurnaceNo(deviceName, branchCode, code));
    }

    /**
     * @return
     */
    @ApiOperation(value = "热工报工步骤回滚", notes = "热工报工步骤回滚")
    @GetMapping("/rollBack")
    public CommonResult<Boolean> rollBack(Long prechargeFurnaceId){
        return CommonResult.success(heatTrackCompleteService.rollBack(prechargeFurnaceId));
    }


}
