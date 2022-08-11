package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.RouterMapper;
import com.richfit.mes.base.entity.QueryIsHistory;
import com.richfit.mes.base.entity.QueryProcessRecordsVo;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class RouterServiceImpl extends ServiceImpl<RouterMapper, Router> implements RouterService {

    @Autowired
    private RouterMapper routerMapper;
    @Autowired
    private SequenceService sequenceService;

    @Override
    public IPage<Router> selectPage(Page page, QueryWrapper<Router> qw) {
        return routerMapper.selectPage(page, qw);
    }

    /*
        查询列表后，直接查询每条工艺的历史工艺，一起返回
     */
    @Override
    public IPage<Router> selectPageAndChild(Page page, QueryWrapper<Router> qw) {
        IPage<Router> routers = this.page(page, qw);


        for (Router u : routers.getRecords()) {
            QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
            queryWrapper.eq("router_no", u.getRouterNo());
            queryWrapper.eq("branch_code", u.getBranchCode());
            queryWrapper.eq("tenant_id", u.getTenantId());
            //历史版本
            queryWrapper.eq("is_active", "2");
            //过滤当前工艺自己，不出现在历史版本中
            queryWrapper.ne("id", u.getId());
            u.setChildren(this.list(queryWrapper));
        }

        return routers;
    }

    @Override
    public List<Router> getList(QueryWrapper<Router> qw) {
        return routerMapper.selectRouter(qw);
    }

    @Override
    public QueryIsHistory queryIsHistory(String routerId) {
        Router router = this.getById(routerId);
        QueryIsHistory queryIsHistory = new QueryIsHistory();
        if ("2".equals(router.getIsActive())) {
            queryIsHistory.setOldVersions(router.getVersion());
            queryIsHistory.setIsHistory(true);
            QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
            queryWrapper.eq("router_no", router.getRouterNo());
            queryWrapper.eq("is_active", "1");
            Router one = this.getOne(queryWrapper);
            queryIsHistory.setNewVersions(one.getVersion());
            return queryIsHistory;
        }
        queryIsHistory.setIsHistory(false);
        return queryIsHistory;
    }

    @Override
    public QueryProcessRecordsVo queryProcessRecords(String routerId) {
        //获取老工序数据
        List<Sequence> oldSequenceList = new ArrayList<>();
        QueryWrapper<Sequence> sequenceOldQueryWrapper = new QueryWrapper<>();
        sequenceOldQueryWrapper.eq("router_id", routerId);
        oldSequenceList = sequenceService.list(sequenceOldQueryWrapper);
        //获取新工序数据
        List<Sequence> newSequenceList = new ArrayList<>();
        QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
        Router router = this.getById(routerId);
        queryWrapper.eq("router_no", router.getRouterNo());
        queryWrapper.eq("is_active", "1");
        Router one = this.getOne(queryWrapper);
        QueryWrapper<Sequence> sequenceNewQueryWrapper = new QueryWrapper<>();
        sequenceNewQueryWrapper.eq("router_id", one.getId());
        newSequenceList = sequenceService.list(sequenceNewQueryWrapper);
        return new QueryProcessRecordsVo(oldSequenceList, newSequenceList);
    }

}
