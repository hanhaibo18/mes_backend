package com.richfit.mes.produce.controller.heat;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.entity.heat.HeatCompleteDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.service.heat.HeatTrackCompleteService;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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

    @ApiOperation(value = "报工")
    @PostMapping("/saveComplete")
    public CommonResult<Boolean> saveComplete(@ApiParam(value = "查询条件", required = true) @RequestBody HeatCompleteDto heatCompleteDto) throws Exception {
        return CommonResult.success(heatTrackCompleteService.saveComplete(heatCompleteDto));
    }

    @ApiOperation(value = "开工")
    @GetMapping("/startWork")
    public CommonResult<Boolean> startWork(@ApiParam(value = "预装炉id", required = true) @RequestParam String prechargeFurnaceId){
        return CommonResult.success(heatTrackCompleteService.startWork(prechargeFurnaceId));
    }

    @ApiOperation(value = "根据预装炉id获取报工信息")
    @GetMapping("/getCompleteInfoByFuId")
    public CommonResult<Map<String,Object>> getCompleteInfoByFuId(@ApiParam(value = "预装炉id", required = true) String prechargeFurnaceId){
        return CommonResult.success(heatTrackCompleteService.getCompleteInfoByFuId(prechargeFurnaceId));
    }

    @ApiOperation(value = "（已报工）分页查询已报工信息")
    @PostMapping("/alreadyCompletePage")
    public Page<PrechargeFurnace> alreadyCompletePage(@ApiParam(value = "查询条件", required = true) @RequestBody ForDispatchingDto dispatchingDto){
        TenantUserVo data = systemServiceClient.getUserById(SecurityUtils.getCurrentUser().getUserId()).getData();
        if(ObjectUtil.isEmpty(data)){
            throw new GlobalException("用户不存在", ResultCode.FAILED);
        }
        //当前用户报过的步骤信息
        QueryWrapper<TrackComplete> completeQueryWrapper = new QueryWrapper<>();
        completeQueryWrapper.eq("complete_by", data.getUserAccount());
        List<TrackComplete> completeList = heatTrackCompleteService.list(completeQueryWrapper);
        //预装炉id集合
        List<String> fuIds = new ArrayList<>(completeList.stream().map(TrackComplete::getPrechargeFurnaceId).collect(Collectors.toSet()));
        if(fuIds.size()>0){
            QueryWrapper<PrechargeFurnace> prechargeFurnaceQueryWrapper = new QueryWrapper<>();
            if (!StringUtils.isNullOrEmpty(dispatchingDto.getTempWork())) {
                int tempWorkZ = Integer.parseInt(StringUtils.isNullOrEmpty(dispatchingDto.getTempWork())?"0":dispatchingDto.getTempWork()) + Integer.parseInt(dispatchingDto.getTempWork1());
                int tempWorkQ = Integer.parseInt(StringUtils.isNullOrEmpty(dispatchingDto.getTempWork())?"0":dispatchingDto.getTempWork()) - Integer.parseInt(dispatchingDto.getTempWork1());
                //小于等于
                prechargeFurnaceQueryWrapper.le("temp_work", tempWorkZ);
                //大于等于
                prechargeFurnaceQueryWrapper.ge("temp_work", tempWorkQ);
            }
            prechargeFurnaceQueryWrapper.in("id",fuIds)
                    .orderByAsc("modify_time");
            return  prechargeFurnaceService.page(new Page<PrechargeFurnace>(dispatchingDto.getPage(),dispatchingDto.getLimit()),prechargeFurnaceQueryWrapper);

        }
        return new Page<PrechargeFurnace>();
    }

    @ApiOperation(value = "（已报工）已报工根据预装炉id查询当前用户报工的步骤及用户")
    @GetMapping("/queryStepListByFuId")
    public Map queryStepListByFuId(@ApiParam(value = "预装炉id", required = true) @RequestParam String id){
        TenantUserVo data = systemServiceClient.getUserById(SecurityUtils.getCurrentUser().getUserId()).getData();
        if(ObjectUtil.isEmpty(data)){
            throw new GlobalException("用户不存在", ResultCode.FAILED);
        }
        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("precharge_furnace_id",id)
                .eq("complete_by", data.getUserAccount())
                .orderByAsc("create_time");
        List<TrackComplete> completes = heatTrackCompleteService.list(queryWrapper);
        List<TrackComplete> stepList = new ArrayList<>();
        List<TrackComplete> userList = new ArrayList<>();
        //根据步骤分组id分组 得到步骤集合
        Map<String, List<TrackComplete>> stepGroup = completes.stream().collect(Collectors.groupingBy(TrackComplete::getStepGroupId));
        stepGroup.forEach((key,value)->{
            stepList.add(value.get(0));
            for (TrackComplete trackComplete : value) {
                userList.add(trackComplete);
            }
        });
        Map<String, List<TrackComplete>> returnMap = new HashMap<>();
        stepList.sort((t1,t2)->t1.getCreateTime().compareTo(t2.getCreateTime()));
        userList.sort((t1,t2)->t1.getCreateTime().compareTo(t2.getCreateTime()));
        returnMap.put("stepList",stepList);
        returnMap.put("userList",userList);
        return returnMap;
    }

    @ApiOperation(value = "（已报工）根据步骤id查询跟单工序信息")
    @GetMapping("/queryItemListByStepGroupId")
    public List<TrackItem> queryItemListByStepGroupId(@ApiParam(value = "步骤分组id（stepGroupId）", required = true) @RequestParam String stepGroupId){
        QueryWrapper<TrackComplete> wrapper = new QueryWrapper<>();
        wrapper.eq("step_group_id",stepGroupId);
        List<TrackComplete> stepCompletes = heatTrackCompleteService.list(wrapper);
        List<String> itemIds = stepCompletes.stream().map(TrackComplete::getTiId).collect(Collectors.toList());
        if(itemIds.size()>0){
            QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
            trackItemQueryWrapper.in("id",itemIds);
            return trackItemService.list(trackItemQueryWrapper);

        }
        return new ArrayList<TrackItem>();
    }





}
