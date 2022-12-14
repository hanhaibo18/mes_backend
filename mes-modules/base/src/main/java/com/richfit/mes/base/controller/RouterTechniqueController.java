package com.richfit.mes.base.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.service.RouterTechniqueService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.RouterTechnique;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author renzewen
 * @date 2022-05-13 17:16
 */
@Slf4j
@Api(value = "工艺技术要求", tags = {"工艺技术要求"})
@RestController
@RequestMapping("/api/base/technique")
public class RouterTechniqueController {

    @Autowired
    private RouterTechniqueService routerTechniqueService;

    @GetMapping("/query/list")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    public CommonResult<List<RouterTechnique>> queryList(@RequestParam("routerId") String routerId) throws Exception {
        QueryWrapper<RouterTechnique> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("router_id",routerId)
                .orderByDesc("modify_time");
        return CommonResult.success(routerTechniqueService.list(queryWrapper));

    }

    @GetMapping("/queryPage/list")
    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    public CommonResult<IPage<RouterTechnique>> queryPageList(@RequestParam("routerId") String routerId,@RequestParam("page") int page,@RequestParam("limit") int limit) throws Exception {
        QueryWrapper<RouterTechnique> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("router_id",routerId)
                .orderByDesc("modify_time");
        return CommonResult.success(routerTechniqueService.page(new Page<RouterTechnique>(page,limit),queryWrapper));
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存", notes = "保存")
    public CommonResult<Boolean> save(@RequestBody RouterTechnique routerTechnique) throws Exception {
        return CommonResult.success(routerTechniqueService.saveOrUpdate(routerTechnique));
    }

    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParam(paramType = "path", name = "id", value = "技术及注意事项id", required = true, dataType = "String")
    @DeleteMapping(value = "/{id}")
    public CommonResult<Boolean> delete(@PathVariable String id) {
        return CommonResult.success(routerTechniqueService.removeById(id));
    }

}
