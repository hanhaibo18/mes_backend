package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.RouterCheckService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.RouterCheck;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 马峰
 * @Description 技术要求Controller
 */
@Slf4j
@Api("工序技术要求")
@RestController
@RequestMapping("/api/base/routerCheck")
public class RouterCheckController extends BaseController {

    @Autowired
    private RouterCheckService routerCheckService;

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "技术要求分页查询", notes = "技术要求分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sId", value = "工序ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<RouterCheck>> page(int page, int limit, String sequenceId, String name, String drawingNo, String id, String type) {
        try {
            QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
            if (!StringUtils.isNullOrEmpty(sequenceId)) {
                queryWrapper.eq("sequence_id", sequenceId);
            } else {
                queryWrapper.eq("sequence_id", "-1");
            }
            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.eq("type", type);
            } else {
                queryWrapper.notIn("type", "质量资料,技术要求,注意事项".split(","));
            }
            if (!StringUtils.isNullOrEmpty(name)) {
                queryWrapper.like("name", name);
            }
            if (!StringUtils.isNullOrEmpty(drawingNo)) {
                queryWrapper.like("drawing_no", drawingNo);
            }
            if (!StringUtils.isNullOrEmpty(id)) {
                queryWrapper.eq("id", id);
            }
            queryWrapper.orderByAsc("check_order");
            IPage<RouterCheck> routerChecks = routerCheckService.page(new Page<RouterCheck>(page, limit), queryWrapper);
            return CommonResult.success(routerChecks);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }


    /**
     * ***
     * 分页查询
     */
    @ApiOperation(value = "技术要求分页查询", notes = "技术要求分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sId", value = "工序ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/find")
    public CommonResult<List<RouterCheck>> find(String drawingNo, String optId, String type) {
        try {
            QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
            queryWrapper.apply("sequence_id in (select opt_id from base_sequence where id = '" + optId + "') and router_id in (select id from base_router where router_no = '" + drawingNo + "' and status ='1')");

            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.eq("type", type);
            } else {
                queryWrapper.notIn("type", "质量资料,技术要求,注意事项".split(","));
            }
            queryWrapper.orderByAsc("check_order");
            List<RouterCheck> routerChecks = routerCheckService.list(queryWrapper);
            return CommonResult.success(routerChecks);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增技术要求", notes = "新增技术要求")
    @ApiImplicitParam(name = "routerCheck", value = "技术要求", required = true, dataType = "RouterCheck", paramType = "query")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<RouterCheck> addRouterCheck(@RequestBody RouterCheck routerCheck) {

        if (StringUtils.isNullOrEmpty(routerCheck.getName())) {
            return CommonResult.failed("名称不能为空！");
        } else {
            boolean bool = routerCheckService.save(routerCheck);
            if (bool) {
                return CommonResult.success(routerCheck, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改技术要求", notes = "修改技术要求")
    @ApiImplicitParam(name = "routerCheck", value = "技术要求", required = true, dataType = "RouterCheck", paramType = "path")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<RouterCheck> updateRouterCheck(@RequestBody RouterCheck routerCheck) {
        if (StringUtils.isNullOrEmpty(routerCheck.getId())) {
            return CommonResult.failed("ID不能为空！");
        } else {
            boolean bool = routerCheckService.updateById(routerCheck);
            if (bool) {
                return CommonResult.success(routerCheck, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }


    @ApiOperation(value = "删除技术要求", notes = "根据id删除技术要求")
    @ApiImplicitParam(name = "ids", value = "编码", required = true, dataType = "String[]", paramType = "query")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<RouterCheck> delete(@RequestBody String[] ids) {

        for (int i = 0; i < ids.length; i++) {

            routerCheckService.removeById(ids[i]);
        }
        return CommonResult.success(null, "删除成功！");

    }


}
