package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.Sequence;

/**
 * @author 马峰
 * @Description 工艺服务
 */
public interface SequenceService extends IService<Sequence> {

    public IPage<Sequence> selectPage(Page page, QueryWrapper<Sequence> qw);

    /**
     * 功能描述: 查询工艺
     *
     * @param optName
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/8/18 16:26
     * @return: String
     **/
    String queryCraft(String optName, String branchCode);
}
