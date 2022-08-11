package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.base.entity.QueryIsHistory;
import com.richfit.mes.base.entity.QueryProcessRecordsVo;
import com.richfit.mes.common.model.base.Router;

import java.util.List;

/**
 * @author 马峰
 * @Description 工艺服务
 */
public interface RouterService extends IService<Router> {

    public IPage<Router> selectPage(Page page, QueryWrapper<Router> qw);

    public IPage<Router> selectPageAndChild(Page page, QueryWrapper<Router> qw);

    List<Router> getList(QueryWrapper<Router> qw);

    /**
     * 功能描述: 查询当前工艺是否为历史工艺
     *
     * @param routerId
     * @Author: xinYu.hou
     * @Date: 2022/6/24 3:59
     * @return: Boolean
     **/
    QueryIsHistory queryIsHistory(String routerId);

    /**
     * 功能描述:
     *
     * @param routerId
     * @Author: xinYu.hou
     * @Date: 2022/6/24 4:25
     * @return: QueryProcessRecordsVo
     **/
    QueryProcessRecordsVo queryProcessRecords(String routerId);
}
