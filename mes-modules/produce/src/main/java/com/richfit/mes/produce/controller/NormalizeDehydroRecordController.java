package com.richfit.mes.produce.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.DevicePerson;
import com.richfit.mes.common.model.base.SequenceSite;
import com.richfit.mes.common.model.produce.*;
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
import com.richfit.mes.produce.utils.DateUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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


    @ApiOperation(value = "正火去氢工序控制记录", notes = "正火去氢工序控制记录")
    @PostMapping("/saveNormalizeDehydroRecord")
    public CommonResult<Boolean> saveNormalizeDehydroRecord(@RequestBody NormalizeDehydroRecord record) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //记录编号
        String timeStemp = String.valueOf(System.currentTimeMillis());
        String yyyyMMddhhmmss = DateUtils.dateToString(new Date(), "yyyyMMddhhmmss");
        //记录编号
        record.setSerialNo(yyyyMMddhhmmss+timeStemp.substring(timeStemp.length()-4));
        //'审核状态 0 未通过  1 通过'
        record.setAuditStatus(0);
        return CommonResult.success(normalizeDehydroRecordService.save(record));
    }

    @ApiOperation(value = "正火去氢工序控制查询", notes = "正火去氢工序控制查询")
    @ApiImplicitParam(name = "itemId", value = "工序ID", required = true, dataType = "String", paramType = "query")
    @GetMapping("/pageNormalizeDehydroRecord")
    public CommonResult<IPage<NormalizeDehydroRecord>> pageNormalizeDehydroRecord(String itemId, String orderCol, String order, int page, int limit) {
        QueryWrapper<NormalizeDehydroRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        OrderUtil.query(queryWrapper, orderCol, order);
        return CommonResult.success(normalizeDehydroRecordService.page(new Page<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "正火去氢工序控制修改", notes = "正火去氢工序控制修改")
    @ApiImplicitParams({@ApiImplicitParam(name = "normalizeDehydroRecordList", value = "正火去氢工序控制记录实体", required = true, dataType = "List", paramType = "query")
            , @ApiImplicitParam(name = "itemId", value = "工序ItemId", required = true, dataType = "String", paramType = "query")})
    @PutMapping("/updateNormalizeDehydroRecord/{itemId}")
    public CommonResult<Boolean> updateNormalizeDehydroRecord(@RequestBody List<NormalizeDehydroRecord> normalizeDehydroRecordList, @PathVariable String itemId) {
        return CommonResult.success(normalizeDehydroRecordService.updateBatch(normalizeDehydroRecordList, itemId));
    }

    @ApiOperation(value = "正火去氢工序控制删除", notes = "正火去氢工序控制删除")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "normalizeDehydroRecordList", value = "正火去氢工序控制记录实体", required = true, dataType = "List", paramType = "query")}
    )
    @DeleteMapping("/deleteNormalizeDehydroRecord")
    public CommonResult<Boolean> deleteNormalizeDehydroRecord(@RequestBody List<NormalizeDehydroRecord> normalizeDehydroRecordList) {
        return CommonResult.success(normalizeDehydroRecordService.removeByIds(normalizeDehydroRecordList));
    }

}
