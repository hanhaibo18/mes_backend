package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Drafts;
import com.richfit.mes.sys.dao.DraftsMapper;
import com.richfit.mes.sys.entity.dto.QueryDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName: DraftsServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月17日 16:21:00
 */
@Service
public class DraftsServiceImpl extends ServiceImpl<DraftsMapper, Drafts> implements DraftsService{

    @Resource
    private DraftsService draftsService;

    @Override
    public CommonResult<IPage<Drafts>> queryDraftsList(QueryDto<String> queryDto) {
        QueryWrapper <Drafts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by",queryDto.getParam());
        return CommonResult.success(draftsService.page(new Page<>(queryDto.getPage(), queryDto.getSize()),queryWrapper));
    }

    @Override
    public Boolean deleteDraftsById(String id) {
        return draftsService.removeById(id);
    }

    @Override
    public CommonResult<Drafts> queryDraftsById(String queryDto) {
        QueryWrapper<Drafts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",queryDto);
        return CommonResult.success(draftsService.getOne(queryWrapper));
    }

    @Override
    public CommonResult<Boolean> updateDrafts(Drafts drafts) {
        QueryWrapper<Drafts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",drafts.getId());
        return CommonResult.success(draftsService.update(drafts,queryWrapper));
    }

    @Override
    public CommonResult<Boolean> saveDrafts(Drafts drafts) {
        return CommonResult.success(this.save(drafts));
    }


}
