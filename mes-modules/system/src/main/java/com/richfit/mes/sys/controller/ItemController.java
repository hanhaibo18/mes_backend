package com.richfit.mes.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.ItemClass;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.annotation.Inner;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.service.ItemClassService;
import com.richfit.mes.sys.service.ItemParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典 前端控制器
 * </p>
 *
 * @author 王瑞
 * @since 2020-08-05
 */
@Slf4j
@Api("字典管理")
@RestController
@RequestMapping("/api/sys/item")
public class ItemController extends BaseController {

    @Autowired
    private ItemParamService itemParamService;

    @Autowired
    private ItemClassService itemClassService;

    public static String ITEM_ID_NULL_MESSAGE = "ID不能为空!";
    public static String ITEM_CLASS_NAME_NULL_MESSAGE = "分类名称不能为空!";
    public static String ITEM_PARAM_CODE_NULL_MESSAGE = "参数编码不能为空!";
    public static String ITEM_PARAM_LABEL_NULL_MESSAGE = "参数名称不能为空!";
    public static String HAVE_PARAMS_MESSAGE = "选择的分类下有相关参数！";
    public static String ITEM_SUCCESS_MESSAGE = "操作成功！";

    @ApiOperation(value = "新增字典分类", notes = "新增字典分类")
    @PostMapping("/item/class")
    public CommonResult<Boolean> saveItemClass(@RequestBody ItemClass entity) throws GlobalException {
        entity.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        entity.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setCreateTime(new Date());
        if (StringUtils.isNullOrEmpty(entity.getName())) {
            return CommonResult.failed(ITEM_CLASS_NAME_NULL_MESSAGE);
        }
        return CommonResult.success(itemClassService.save(entity));
    }

    @ApiOperation(value = "修改字典分类", notes = "修改字典分类")
    @PutMapping("/item/class")
    public CommonResult<Boolean> updateItemClass(@RequestBody ItemClass entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getId())) {
            return CommonResult.failed(ITEM_ID_NULL_MESSAGE);
        }
        if (StringUtils.isNullOrEmpty(entity.getName())) {
            return CommonResult.failed(ITEM_CLASS_NAME_NULL_MESSAGE);
        }
        entity.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setModifyTime(new Date());
        return CommonResult.success(itemClassService.updateById(entity));
    }


    @ApiOperation(value = "删除字典分类", notes = "删除字典分类")
    @DeleteMapping("/item/class/{id}")
    public CommonResult<Boolean> deleteItemClass(@PathVariable String id) throws GlobalException {
        if (StringUtils.isNullOrEmpty(id)) {
            return CommonResult.failed(ITEM_ID_NULL_MESSAGE);
        }
        List<ItemParam> params = itemParamService.list(
                new QueryWrapper<ItemParam>().eq("class_id", id)
        );
        if (params.size() > 0) {
            return CommonResult.failed(HAVE_PARAMS_MESSAGE);
        }
        return CommonResult.success(itemClassService.removeById(id));
    }

    @ApiOperation(value = "新增字典参数", notes = "新增字典参数")
    @PostMapping("/item/param")
    public CommonResult<Boolean> saveItemParam(@RequestBody ItemParam entity) throws GlobalException {
        entity.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        entity.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setCreateTime(new Date());
        if (StringUtils.isNullOrEmpty(entity.getCode())) {
            return CommonResult.failed(ITEM_PARAM_CODE_NULL_MESSAGE);
        }
        if (StringUtils.isNullOrEmpty(entity.getLabel())) {
            return CommonResult.failed(ITEM_PARAM_LABEL_NULL_MESSAGE);
        }
        return CommonResult.success(itemParamService.save(entity));
    }

    @ApiOperation(value = "修改字典参数", notes = "修改字典参数")
    @PutMapping("/item/param")
    public CommonResult<Boolean> updateItemParam(@RequestBody ItemParam entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getId())) {
            return CommonResult.failed(ITEM_ID_NULL_MESSAGE);
        }
        if (StringUtils.isNullOrEmpty(entity.getCode())) {
            return CommonResult.failed(ITEM_PARAM_CODE_NULL_MESSAGE);
        }
        if (StringUtils.isNullOrEmpty(entity.getLabel())) {
            return CommonResult.failed(ITEM_PARAM_LABEL_NULL_MESSAGE);
        }
        entity.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setModifyTime(new Date());
        return CommonResult.success(itemParamService.updateById(entity));
    }

    @ApiOperation(value = "删除字典参数", notes = "根据参数ID删除字典参数")
    @DeleteMapping("/item/param")
    public CommonResult<Boolean> deleteItemClass(@RequestBody List<String> ids) throws GlobalException {
        if (ids == null || ids.size() == 0) {
            return CommonResult.failed(ITEM_ID_NULL_MESSAGE);
        }
        return CommonResult.success(itemParamService.removeByIds(ids));
    }

    @ApiOperation(value = "分页查询字典参数", notes = "根据参数编码、参数名称、分类分页查询字典参数")
    @GetMapping("/item/param")
    public CommonResult<IPage<ItemParam>> selectItemParam(String code, String label, String classId, int page, int limit) {
        QueryWrapper<ItemParam> queryWrapper = new QueryWrapper<ItemParam>();

        if (!StringUtils.isNullOrEmpty(classId)) {
            queryWrapper.eq("class_id", classId);
            if (!StringUtils.isNullOrEmpty(code) && !StringUtils.isNullOrEmpty(classId)) {
                queryWrapper.like("code", code);
            }
            if (!StringUtils.isNullOrEmpty(label)) {
                queryWrapper.like("label", label);
            }
        } else {
            if (!StringUtils.isNullOrEmpty(code)) {
                queryWrapper.inSql("class_id", "select id from sys_item_class where code ='" + code + "'");
            }
            if (!StringUtils.isNullOrEmpty(label)) {
                queryWrapper.inSql("class_id", "select id from sys_item_class where label ='" + label + "'");
            }
        }
        if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        // queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByAsc("order_num");
        IPage<ItemParam> itemParamIPage = itemParamService.page(new Page<ItemParam>(page, limit), queryWrapper);
        for (ItemParam itemParam : itemParamIPage.getRecords()) {
            itemParam.setValue(itemParam.getCode());
        }
        return CommonResult.success(itemParamIPage, ITEM_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询字典参数", notes = "根据参数类别和参数名称查询字典参数")
    @GetMapping("/item/param/list")
    public CommonResult<List<ItemParam>> selectItemParamByCode(String code, String label) throws Exception {
        QueryWrapper<ItemClass> queryWrapper = new QueryWrapper<ItemClass>();
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
            if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            }
        }
        List<ItemClass> iClasses = itemClassService.list(queryWrapper);
        //获取字典工厂代码id
        List<String> classIds = iClasses.stream().map(ItemClass::getId).collect(Collectors.toList());
        if (classIds.size() > 0) {
            QueryWrapper<ItemParam> wrapper = new QueryWrapper<>();
            wrapper.in("class_id", classIds);
            if (!StringUtils.isNullOrEmpty(label)) {
                wrapper.like("label", label);
            }
            if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
                wrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            }
//            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            wrapper.orderByAsc("order_num");
            return CommonResult.success(itemParamService.list(wrapper), ITEM_SUCCESS_MESSAGE);
        } else {
            throw new Exception("没有找到key=" + code + "的字典！");
        }
    }

    @ApiOperation(value = "查询字典参数数量", notes = "根据参数分类查询字典参数总数")
    @GetMapping("/item/param/count")
    public CommonResult<Integer> selectItemParamCount(String classId) {
        QueryWrapper<ItemParam> queryWrapper = new QueryWrapper<ItemParam>();
        if (!StringUtils.isNullOrEmpty(classId)) {
            queryWrapper.eq("class_id", classId);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(itemParamService.count(queryWrapper), ITEM_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询字典分类", notes = "查询字典分类")
    @GetMapping("/item/class")
    public CommonResult<List<ItemClass>> selectItemClass(String name, String code) {
        QueryWrapper<ItemClass> queryWrapper = new QueryWrapper<ItemClass>();
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
        }
        if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        // queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(itemClassService.list(queryWrapper), ITEM_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "根据Code查询指定字典项", notes = "查询字典项")
    @GetMapping("/param/find_by_code")
    public CommonResult<ItemParam> findItemParamByCode(String code) {

        QueryWrapper<ItemParam> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        wrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        return CommonResult.success(itemParamService.getOne(wrapper));
    }


    @ApiOperation(value = "查询字典参数", notes = "根据参数类别和参数名称查询字典参数")
    @GetMapping("/item/param/list/inner")
    @Inner
    public CommonResult<List<ItemParam>> selectItemParamByCodeInner(String code, String label, String tenantId) throws Exception {
        QueryWrapper<ItemClass> queryWrapper = new QueryWrapper<ItemClass>();
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
            queryWrapper.eq("tenant_id", tenantId);
        }
        List<ItemClass> iClasses = itemClassService.list(queryWrapper);
        if (iClasses.size() > 0) {
            QueryWrapper<ItemParam> wrapper = new QueryWrapper<>();
            wrapper.eq("class_id", iClasses.get(0).getId());
            if (!StringUtils.isNullOrEmpty(label)) {
                wrapper.like("label", label);
            }
            queryWrapper.eq("tenant_id", tenantId);
            wrapper.orderByAsc("order_num");
            return CommonResult.success(itemParamService.list(wrapper), ITEM_SUCCESS_MESSAGE);
        } else {
            throw new Exception("没有找到key=" + code + "的字典！");
        }
    }

    @ApiOperation(value = "根据Code查询指定字典项", notes = "查询字典项,仅限定时任务微服务直接调用使用,前端调用无效")
    @GetMapping("/param/find_by_code/inner")
    @Inner
    public CommonResult<ItemParam> findItemParamByCodeInner(String code, String tenantId) {

        QueryWrapper<ItemParam> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        //补充,定时任务没有租户ID
        if (StrUtil.isNotBlank(tenantId)) {
            wrapper.eq("tenant_id", tenantId);
        }
        return CommonResult.success(itemParamService.getOne(wrapper));
    }

}
