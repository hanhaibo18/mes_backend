package com.richfit.mes.sys.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Announcement;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.oss.service.FastDfsService;
import com.richfit.mes.sys.entity.dto.QueryDto;
import com.richfit.mes.sys.service.AnnouncementService;
import com.richfit.mes.sys.service.TenantService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AnnouncementController.java
 * @Author: Hou XinYu
 * @Description: 通知公告
 * @CreateTime: 2022年01月27日 11:25:00
 */
@Slf4j
@Api("通知公告")
@RequestMapping(value = "/api/sys/announcement")
@RestController
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;
    @Resource
    private TenantService tenantService;
    @Resource
    private FastDfsService fastDfsService;

    @ApiOperation(value = "保存通知信息", notes = "保存通知公告")
    @PostMapping("/save")
    public CommonResult<Boolean> announcementSave(@RequestBody Announcement announcement) {
        return CommonResult.success(announcementService.save(announcement));
    }

    @PostMapping("/query/page")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<IPage<Announcement>> queryPageAnnouncement(@RequestBody QueryDto<Announcement> query) throws IOException {
        Announcement announcement = query.getData();
        boolean empty = StringUtils.isEmpty(announcement);
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        if (!empty && !StringUtils.isEmpty(announcement.getTitle())){
            queryWrapper.like("title","%"+announcement.getTitle() + "%");
        }
        queryWrapper.eq("branch_code",announcement.getBranchCode());
        queryWrapper.orderByDesc("if_top DESC,top_number DESC,create_time");
        Page<Announcement> page = announcementService.page(new Page<>(query.getPage(), query.getSize()), queryWrapper);
        List<Tenant> list = tenantService.list();
        Map<String, String> maps = list.stream().collect(Collectors.toMap(Tenant::getTenantCode, Tenant::getTenantName, (key1, key2) -> key2));
        page.getRecords().forEach(tenant -> {
            tenant.setUserName(maps.get(tenant.getBranchCode()));
            if (tenant.getUserName() == null){
                tenant.setUserName(tenant.getCreateBy());
            }
        });
        return  CommonResult.success(page);
    }

    @ApiOperation(value = "修改信息", notes = "修改信息")
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> updateById(@RequestBody Announcement baseAnnouncement){
        return CommonResult.success(announcementService.updateById(baseAnnouncement));
    }

    @ApiOperation(value = "根据id删除信息", notes = "根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteById(@PathVariable String id) throws GlobalException{
        return CommonResult.success(announcementService.removeById(id));
    }

    @GetMapping("/query_one")
    public CommonResult<Announcement> queryById(String id){
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        return CommonResult.success(announcementService.getOne(queryWrapper));
    }

    @GetMapping("/size")
    private Integer querySize(){
        return announcementService.count();
    }
}
