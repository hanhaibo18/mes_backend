package com.richfit.mes.produce.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.DevicePerson;
import com.richfit.mes.common.model.base.SequenceSite;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.ForgControlRecordDto;
import com.richfit.mes.produce.entity.OutsourceCompleteDto;
import com.richfit.mes.produce.entity.QueryWorkingTimeVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import com.richfit.mes.produce.utils.DateUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 胡甲
 * @Description 正火去氢工序控制记录Controller
 */
@Slf4j
@Api(value = "正火去氢工序控制记录", tags = {"正火去氢工序控制记录"})
@RestController
@RequestMapping("/api/produce/normalizeDehydroRecord")
public class NormalizeDehydroRecordController extends BaseController {

    @Autowired
    private NormalizeDehydroRecordService normalizeDehydroRecordService;
    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;
    @Autowired
    private SystemServiceClient systemServiceClient;

    @ApiOperation(value = "正火去氢工序控制查询", notes = "正火去氢工序控制查询")
    @PostMapping("/pageNormalizeDehydroRecord")
    public CommonResult<IPage<NormalizeDehydroRecord>> pageNormalizeDehydroRecord(@RequestBody NormalizeDehydroRecord normalizeDehydroRecord) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        QueryWrapper<NormalizeDehydroRecord> queryWrapper = new QueryWrapper<>();

        if(!StringUtils.isNullOrEmpty(normalizeDehydroRecord.getSerialNo())){
            queryWrapper.eq("serial_no", normalizeDehydroRecord.getSerialNo());
        }
        if(!StringUtils.isNullOrEmpty(normalizeDehydroRecord.getEquipmentNo())){
            queryWrapper.eq("equipment_no", normalizeDehydroRecord.getEquipmentNo());
        }
        if (!StringUtils.isNullOrEmpty(normalizeDehydroRecord.getStartTime())) {
            queryWrapper.ge("create_time", normalizeDehydroRecord.getStartTime() + " 00:00:00");
        }
        if (!StringUtils.isNullOrEmpty(normalizeDehydroRecord.getEndTime())) {
            queryWrapper.le("create_time", normalizeDehydroRecord.getEndTime() + " 23:59:59");
        }
        if (!StringUtils.isNullOrEmpty(normalizeDehydroRecord.getBranchCode())) {
            queryWrapper.eq("branch_code", normalizeDehydroRecord.getBranchCode());
        }
        if (BeanUtil.isNotEmpty(normalizeDehydroRecord.getType())) {
            queryWrapper.eq("type", normalizeDehydroRecord.getType());
        }
        boolean isBzz = normalizeDehydroRecordService.isBzz(currentUser);
        //班组长查询
        if (isBzz) {
            //班组长查询同班员工号
            List<TenantUser> tenantUserList = systemServiceClient.queryClass(currentUser.getUsername());
            if(CollectionUtils.isNotEmpty(tenantUserList)){
                List<String> userIdList = tenantUserList.stream().map(TenantUser::getUserAccount).collect(Collectors.toList());
                //userIdList.add(currentUser.getUsername());
                queryWrapper.in("create_by",userIdList);
            }
        }
        else {
            //普通操作工查询
            queryWrapper.eq("create_by",currentUser.getUsername());
        }
        return CommonResult.success(normalizeDehydroRecordService.page(new Page<>(normalizeDehydroRecord.getPage(),normalizeDehydroRecord.getLimit()), queryWrapper));
    }




    @ApiOperation(value = "添加正火去氢工序控制记录", notes = "添加正火去氢工序控制记录")
    @PostMapping("/saveNormalizeDehydroRecord")
    public CommonResult<Boolean> saveNormalizeDehydroRecord(@RequestBody NormalizeDehydroRecord record) {
        return CommonResult.success(normalizeDehydroRecordService.saveNormalizeDehydroRecord(record));
    }

    @ApiOperation(value = "根据预装炉id查询正火去氢工序控制记录", notes = "根据预装炉id查询正火去氢工序控制记录")
    @ApiImplicitParam(name = "furnaceId", value = "预装炉ID", required = true, dataType = "String", paramType = "query")
    @GetMapping("/getRecordByFurnaceId")
    public CommonResult<List<NormalizeDehydroRecord>> normalizeDehydroRecord(String furnaceId) {
        QueryWrapper<NormalizeDehydroRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("furnace_id", furnaceId);
        return CommonResult.success(normalizeDehydroRecordService.list(queryWrapper));
    }

    @ApiOperation(value = "根据id查询正火去氢工序控制记录", notes = "根据id查询正火去氢工序控制记录")
    @ApiImplicitParam(name = "ID", value = "ID", required = true, dataType = "String", paramType = "query")
    @GetMapping("/getRecordById")
    public CommonResult<NormalizeDehydroRecord> getRecordById(String id) {
        return CommonResult.success(normalizeDehydroRecordService.getById(id));
    }

    @ApiOperation(value = "正火去氢工序控制记录修改", notes = "正火去氢工序控制记录修改")
    @PostMapping("/updateRecordById")
    public CommonResult<Boolean> updateRecordById(@RequestBody NormalizeDehydroRecord normalizeDehydroRecord) {
        return CommonResult.success(normalizeDehydroRecordService.updateNormalizeDehydroRecord(normalizeDehydroRecord));
    }

    @ApiOperation(value = "正火去氢工序控制删除", notes = "正火去氢工序控制删除")
    @PostMapping("/deleteNormalizeDehydroRecord")
    public CommonResult<Boolean> deleteNormalizeDehydroRecord(@RequestBody List<String> idList) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //只有班组长有删除权限
        if(normalizeDehydroRecordService.isBzz(currentUser)){
            return CommonResult.success(normalizeDehydroRecordService.removeByIds(idList));
        }else {
            throw new GlobalException("删除失败!无班组长权限", ResultCode.FAILED);
        }

    }

    @ApiOperation(value = "正火去氢工序控审核", notes = "正火去氢工序控审核")
    @PostMapping("/auditNormalizeDehydroRecord")
    public CommonResult<Boolean> auditNormalizeDehydroRecord(@ApiParam(value = "idList") @RequestBody List<String> idList,
                                                             @ApiParam(value = "审核状态  0 未审核  1 通过,2 未通过",required = true)@RequestParam Integer status) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //只有班组长有审核权限
        if(!normalizeDehydroRecordService.isBzz(currentUser)){
            throw new GlobalException("审核失败!无班组长权限",ResultCode.FAILED);
        }
        UpdateWrapper<NormalizeDehydroRecord> updateWrapper=new UpdateWrapper<>();
        updateWrapper.in("id",idList);
        updateWrapper.set("audit_status",status);
        updateWrapper.set("audit_by",currentUser.getUsername());
        //登陆人信息
        TenantUserVo tenantUser = systemServiceClient.queryByUserId(SecurityUtils.getCurrentUser().getUserId()).getData();
        updateWrapper.set("audit_name",tenantUser.getEmplName());
        updateWrapper.set("audit_time",new Date());

        //查出审核的装炉记录
        QueryWrapper<NormalizeDehydroRecord> queryWrapper=new QueryWrapper();
        queryWrapper.in("id",idList);
        List<NormalizeDehydroRecord> list = normalizeDehydroRecordService.list(queryWrapper);
        if(status==1){
            if(CollectionUtils.isNotEmpty(list)){
                for (NormalizeDehydroRecord normalizeDehydroRecord : list) {
                    //根据装炉记录同步装炉中是记录状态  审核通过
                    normalizeDehydroRecordService.synchronizationRecordStatus(normalizeDehydroRecord.getFurnaceId(),"1");
                }
            }
        }else if(status==2){
            if(CollectionUtils.isNotEmpty(list)){
                for (NormalizeDehydroRecord normalizeDehydroRecord : list) {
                    //根据装炉记录同步装炉中是记录状态  审核通过
                    normalizeDehydroRecordService.synchronizationRecordStatus(normalizeDehydroRecord.getFurnaceId(),"2");
                }
            }
        }
        return CommonResult.success(normalizeDehydroRecordService.update(updateWrapper));
    }

}
