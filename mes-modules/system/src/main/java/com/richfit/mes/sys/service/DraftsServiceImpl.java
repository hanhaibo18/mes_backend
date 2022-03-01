package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Drafts;
import com.richfit.mes.sys.dao.DraftsMapper;
import com.richfit.mes.sys.entity.dto.QueryDto;

import javax.annotation.Resource;

/**
 * @ClassName: DraftsServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月17日 16:21:00
 */
public class DraftsServiceImpl extends ServiceImpl<DraftsMapper, Drafts> implements DraftsService{

    @Resource
    private DraftsService draftsService;

    @Override
    public CommonResult<IPage<Drafts>> queryDraftsList(QueryDto<Drafts> queryDto) {
        return CommonResult.success(draftsService.page(new Page<>(queryDto.getPage(), queryDto.getSize())));
    }

    @Override
    public CommonResult<Boolean> deleteDraftsById(String id) {
        return CommonResult.success(draftsService.removeById(id));
    }

    @Override
    public CommonResult<Drafts> queryDraftsById(QueryDto<String> queryDto) {
        QueryWrapper<Drafts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",queryDto.getData());
        return CommonResult.success(draftsService.getOne(queryWrapper));
    }

    @Override
    public CommonResult<Boolean> updateDrafts(Drafts drafts) {
        QueryWrapper<Drafts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",drafts.getId());
        return CommonResult.success(draftsService.update(drafts,queryWrapper));
    }
}