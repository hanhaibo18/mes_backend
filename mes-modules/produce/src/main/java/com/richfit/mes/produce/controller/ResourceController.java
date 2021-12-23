package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.CalendarClass;
import com.richfit.mes.common.model.base.CalendarDay;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.ResourceDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 王瑞
 * @Description 资源负荷Controller
 */
@Slf4j
@Api("资源负荷")
@RestController
@RequestMapping("/api/produce/resource")
public class ResourceController {

    @Autowired
    private TrackAssignService trackAssignService;

    @Autowired
    private TrackCompleteService trackCompleteService;

    @Autowired
    private TrackItemService trackItemService;

    @Autowired
    private BaseServiceClient baseServiceClient;

    @Autowired
    private SystemServiceClient systemServiceClient;

    @ApiOperation(value = "查询资源负荷", notes = "根据员工/设备和时间范围查询")
    @GetMapping("/resource")
    public CommonResult<List<Object>> selectResource(String type, String dateType, String startDate, String endDate){
        QueryWrapper<TrackItem> assignQuery = new QueryWrapper<>(); //派工数据查询
        QueryWrapper<TrackItem> itemQuery = new QueryWrapper<>(); //报工数据查询

        if(!StringUtils.isNullOrEmpty(startDate)){
            assignQuery.ge("start_time", startDate);
            itemQuery.ge("start_doing_time", startDate);
        }

        if(!StringUtils.isNullOrEmpty(endDate)){
            assignQuery.le("end_time", endDate);
            itemQuery.le("operation_complete_time", endDate);
        }
        assignQuery.isNotNull("start_time");
        assignQuery.isNull("operation_complete_time");
        itemQuery.isNotNull("start_doing_time");
        itemQuery.isNotNull("operation_complete_time");
        assignQuery.orderByAsc("start_time");
        itemQuery.orderByAsc("start_doing_time");

        //assignQuery.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        //itemQuery.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        List<TrackItem> assignList = trackItemService.selectTrackItemAssign(assignQuery);
        List<TrackItem> itemList = trackItemService.selectTrackItem(itemQuery);
        List<Object> result = new ArrayList<>();
        List<ResourceDto> resourceDtos = new ArrayList<>();

        CommonResult<List<CalendarClass>> classList = baseServiceClient.selectCalendarClass(null);
        CommonResult<List<CalendarDay>> dayList = baseServiceClient.selectCalendarDay(startDate, endDate);

        double allTime = 8;
        if(classList != null){
            allTime = 0;
            for(CalendarClass c: classList.getData()){
                double diff = (c.getEndTime().getTime() - c.getStartTime().getTime()) / (1000*60*60);
                allTime += diff;
            }
        }
        List<CalendarDay> days = new ArrayList<>();
        if(dayList != null){
            days = dayList.getData();
        }
        result.add(days);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

        for (TrackItem assign : assignList) {
            ResourceDto dto = new ResourceDto();
            dto.setTime(assign.getSinglePieceHours());
            dto.setDate(sdf.format(assign.getStartTime()));
            dto.setType(0);
            dto.setAllTime(allTime);
            if(type.equals("0")) { //按设备查询
                CommonResult<Device> commonResult = baseServiceClient.getDeviceById(assign.getAssignDeviceId());
                if(commonResult != null && commonResult.getData() != null) {
                    Device device = commonResult.getData();
                    dto.setId(device.getId());
                    dto.setName(device.getName());
                    resourceDtos.add(dto);
                }
            } else {
                if(assign.getUserId() != null && !assign.getUserId().equals("")){
                    CommonResult<TenantUserVo> commonResult = systemServiceClient.getUserById(assign.getUserId());
                    if(commonResult != null && commonResult.getData() != null) {
                        TenantUserVo user = commonResult.getData();
                        dto.setId(user.getId());
                        dto.setName(user.getEmplName());
                        resourceDtos.add(dto);
                    }
                }

            }
        }

        for(TrackItem item : itemList){
            ResourceDto dto = new ResourceDto();
            dto.setTime(item.getActualHours());
            dto.setStartDate(sdf.format(item.getStartDoingTime()));
            dto.setEndDate(sdf.format(item.getOperationCompleteTime()));
            dto.setType(1);
            dto.setAllTime(allTime);
            if(type.equals("0")) { //按设备查询
                CommonResult<Device> commonResult = baseServiceClient.getDeviceById(item.getAssignDeviceId());
                if(commonResult != null && commonResult.getData() != null) {
                    Device device = commonResult.getData();
                    dto.setId(device.getId());
                    dto.setName(device.getName());
                    resourceDtos.add(dto);
                }
            } else {
                if(item.getUserId() != null && !item.getUserId().equals("")) {
                    CommonResult<TenantUserVo> commonResult = systemServiceClient.getUserById(item.getUserId());
                    if (commonResult != null && commonResult.getData() != null) {
                        TenantUserVo user = commonResult.getData();
                        dto.setId(user.getId());
                        dto.setName(user.getEmplName());
                        resourceDtos.add(dto);
                    }
                }
            }
        }

        if(!StringUtils.isNullOrEmpty(dateType) && dateType.equals("yearAndMonth")){
            try{
                result.addAll(sumMonthData(resourceDtos));
            } catch (Exception e){
                return CommonResult.failed("操作失败:" + e.getMessage());
            }
        } else {
            result.addAll(resourceDtos);
        }

        return CommonResult.success(result, "操作成功");
    }

    //按照月来汇总数据
    public List<ResourceDto> sumMonthData(List<ResourceDto> data) throws ParseException {
        List<ResourceDto> result = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        for (ResourceDto dto : data) {
            if(dto.getType() == 0){ //派工时计划时间
                Date date = sdf.parse(dto.getDate());
                Calendar ca = Calendar.getInstance();
                ca.setTime(date);
                int year = ca.get(Calendar.YEAR);//年份数值
                int month = ca.get(Calendar.MONTH) + 1;//第几个月
                String newDate = year + "-" + month + "-1";
                boolean isHave = false;
                int index = 0;
                for(int i=0; i<result.size(); i++){
                    if(result.get(i).getType() == 0 && result.get(i).getName().equals(dto.getName()) && result.get(i).getDate().equals(newDate)){
                        index = i;
                        isHave = true;
                        break;
                    }
                }
                if(isHave){
                    result.get(index).setTime(result.get(index).getTime() + dto.getTime());
                } else {
                    dto.setDate(newDate);
                    result.add(dto);
                }
            } else {
                Date date1 = sdf.parse(dto.getStartDate());
                Date date2 = sdf.parse(dto.getEndDate());
                Calendar ca1 = Calendar.getInstance();
                ca1.setTime(date1);
                int year1 = ca1.get(Calendar.YEAR);//年份数值
                int month1 = ca1.get(Calendar.MONTH) + 1;//第几个月
                Calendar ca2 = Calendar.getInstance();
                ca2.setTime(date1);
                int year2 = ca2.get(Calendar.YEAR);//年份数值
                int month2 = ca2.get(Calendar.MONTH) + 1;//第几个月
                if(year1 != year2 || month1 != month2){
                    String dateDiff = year2 + "-" + month2 + "-01";
                    Long diff1 = sdf.parse(dateDiff).getTime() - date1.getTime();
                    Long diff2 = date2.getTime() - sdf.parse(dateDiff).getTime();
                    long all = diff1 + diff2;
                    double time1 = dto.getTime() * (diff1/all);
                    double time2 = dto.getTime() * (diff2/all);
                    dto.setStartDate(year1 + "-" + month1 + "-1");
                    dto.setTime(time1);

                    ResourceDto dto1 = new ResourceDto();
                    dto1.setStartDate(year2 + "-" + month2 + "-1");
                    dto1.setEndDate(year2 + "-" + month2 + "-2");
                    dto1.setTime(time2);
                    dto1.setType(dto.getType());
                    dto1.setName(dto.getName());
                    dto1.setAllTime(dto.getAllTime());
                    dto1.setId(UUID.randomUUID().toString());
                    boolean isHave = false;
                    int index = 0;
                    boolean isHave2 = false;
                    int index2 = 0;
                    for(int i=0; i<result.size(); i++){
                        if(result.get(i).getType() == 1 && result.get(i).getName().equals(dto.getName()) && result.get(i).getStartDate().equals(dto.getStartDate())){
                            index = i;
                            isHave = true;
                        } else if(result.get(i).getType() == 1 && result.get(i).getName().equals(dto.getName()) && result.get(i).getStartDate().equals(dto1.getStartDate())){
                            index2 = i;
                            isHave2 = true;
                        }
                    }
                    if(isHave){
                        result.get(index).setTime(result.get(index).getTime() + dto.getTime());
                    } else {
                        result.add(dto);
                    }

                    if(isHave2){
                        result.get(index2).setTime(result.get(index2).getTime() + dto1.getTime());
                    } else {
                        result.add(dto1);
                    }
                } else {
                    String newDate = year1 + "-" + month1 + "-1";
                    boolean isHave = false;
                    int index = 0;
                    for(int i=0; i<result.size(); i++){
                        if(result.get(i).getType() == 1 && result.get(i).getStartDate().equals(newDate)){
                            index = i;
                            isHave = true;
                            break;
                        }
                    }
                    if(isHave){
                        result.get(index).setTime(result.get(index).getTime() + dto.getTime());
                    } else {
                        dto.setDate(newDate);
                        result.add(dto);
                    }
                }
            }
        }

        return result;
    }

}
