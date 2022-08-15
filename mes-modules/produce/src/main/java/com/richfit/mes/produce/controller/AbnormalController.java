package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Abnormal;
import com.richfit.mes.common.model.produce.AbnormalConfig;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.AbnormalConfigMapper;
import com.richfit.mes.produce.dao.AbnormalMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 * 异常管理
 * </p>
 *
 * @author 马峰
 * @since 2020-11-12
 */
@Slf4j
@Api("异常管理")
@RestController
@RequestMapping("/api/produce/abnormal")
public class AbnormalController extends BaseController {

    @Autowired
    private AbnormalMapper abnormalMapper;
    @Autowired
    private AbnormalConfigMapper abnormalConfigMapper;

    public static String ID_NULL_MESSAGE = "ID不能为空!";
    public static String CLASS_NAME_NULL_MESSAGE = "名称不能为空!";
    public static String SUCCESS_MESSAGE = "操作成功！";

    @ApiOperation(value = "分页查询异常报告", notes = "根据分页查询异常报告")
    @GetMapping("/page")
    public CommonResult<IPage<Abnormal>> pageAbnormal(String siteId, String orgId, String type, String name, String startTime, String endTime, String status, String isAndon, int page, int limit, String branchCode, String tenantId) {
        QueryWrapper<Abnormal> queryWrapper = new QueryWrapper<Abnormal>();
        if (!StringUtils.isNullOrEmpty(siteId)) {
            queryWrapper.eq("site_id", siteId);
        }
        if (!StringUtils.isNullOrEmpty(orgId)) {
            queryWrapper.eq("org_id", orgId);
        }
        if (!StringUtils.isNullOrEmpty(type)) {
            queryWrapper.eq("type", Integer.parseInt(type));
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("status", Integer.parseInt(status));
        }
        if (!StringUtils.isNullOrEmpty(isAndon)) {
            queryWrapper.eq("is_andon", Integer.parseInt(isAndon));
        }
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + startTime + " 00:00:00')");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(modify_time) <= UNIX_TIMESTAMP('" + endTime + " 23:59:59')");
        }

        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        queryWrapper.orderByDesc(new String[]{"status", "modify_time"});
        return CommonResult.success(abnormalMapper.selectPage(new Page<Abnormal>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "新增异常报告", notes = "新增异常报告")
    @PostMapping("/save")
    public CommonResult<Boolean> saveAbnormal(@RequestBody Abnormal entity) throws GlobalException {
        entity.setSubmitBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setSubmitTime(new Date());
        entity.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        entity.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setCreateTime(new Date());
        abnormalMapper.insert(entity);
        return CommonResult.success(true);
    }

    @ApiOperation(value = "修改异常报告", notes = "修改异常报告")
    @PostMapping("/update")
    public CommonResult<Boolean> updateAbnormal(@RequestBody Abnormal entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getId())) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }
        Abnormal oldEntity = abnormalMapper.selectById(entity);
        // 如果状态为0，则写入关闭人，关闭时间
        if (oldEntity.getStatus() == 1 && entity.getStatus() == 0) {
            entity.setCloseBy(SecurityUtils.getCurrentUser().getUsername());
            entity.setCloseTime(new Date());

        }
        entity.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setModifyTime(new Date());
        abnormalMapper.updateById(entity);
        return CommonResult.success(true);
    }

    @ApiOperation(value = "删除异常报告", notes = "删除异常报告")
    @PostMapping("/delete/{id}")
    public CommonResult<Boolean> deleteAbnormal(@PathVariable String id) throws GlobalException {
        if (StringUtils.isNullOrEmpty(id)) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }
        Abnormal entity = new Abnormal();
        entity.setId(id);
        abnormalMapper.deleteById(entity);
        return CommonResult.success(true);
    }


    @ApiOperation(value = "分页查询异常报告", notes = "根据分页查询异常报告")
    @GetMapping("/config/page")
    public CommonResult<IPage<AbnormalConfig>> pageAbnormalConfig(String siteId, String orgId, int page, int limit, String branchCode, String tenantId) {
        QueryWrapper<AbnormalConfig> queryWrapper = new QueryWrapper<AbnormalConfig>();
        if (!StringUtils.isNullOrEmpty(siteId)) {
            queryWrapper.eq("site_id", siteId);
        }
        if (!StringUtils.isNullOrEmpty(orgId)) {
            queryWrapper.eq("org_id", orgId);
        }

        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByDesc(new String[]{"status", "modify_time"});
        return CommonResult.success(abnormalConfigMapper.selectPage(new Page<AbnormalConfig>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "新增异常报告", notes = "新增异常报告")
    @PostMapping("/config/save")
    public CommonResult<Boolean> saveAbnormalConfig(@RequestBody AbnormalConfig entity) throws GlobalException {
        entity.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        entity.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setCreateTime(new Date());
        abnormalConfigMapper.insert(entity);
        return CommonResult.success(true);
    }

    @ApiOperation(value = "修改异常报告", notes = "修改异常报告")
    @PostMapping("/config/update")
    public CommonResult<Boolean> updateAbnormalConfig(@RequestBody AbnormalConfig entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getId())) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }

        entity.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setModifyTime(new Date());
        abnormalConfigMapper.updateById(entity);
        return CommonResult.success(true);
    }

    @ApiOperation(value = "删除异常报告", notes = "删除异常报告")
    @PostMapping("/config/delete/{id}")
    public CommonResult<Boolean> deleteAbnormalConfig(@PathVariable String id) throws GlobalException {
        if (StringUtils.isNullOrEmpty(id)) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }
        AbnormalConfig entity = new AbnormalConfig();
        entity.setId(id);
        abnormalConfigMapper.deleteById(entity);
        return CommonResult.success(true);
    }

}
