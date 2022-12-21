package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.RouterMapper;
import com.richfit.mes.base.entity.QueryIsHistory;
import com.richfit.mes.base.entity.QueryProcessRecordsVo;
import com.richfit.mes.base.provider.ProduceServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.TrackHead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RouterServiceImpl extends ServiceImpl<RouterMapper, Router> implements RouterService {

    @Autowired
    private RouterMapper routerMapper;

    @Autowired
    private SequenceService sequenceService;

    @Resource
    private ProduceServiceClient produceServiceClient;

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
            queryWrapper.eq("status", "2");
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
        if (null != router && !"1".equals(router.getIsActive())) {
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

    /**
     * 功能描述: 通过工艺id集合批量删除工艺信息，并删除工艺的关联的工序信息。
     * 在删除前会通过工艺id查询是否已经生产跟单，如果已经生成则不能删除工艺。
     *
     * @param ids 工艺id集合
     * @Author: zhiqiang.lu
     * @Date: 2022/9/22 9:18
     */
    @Override
    public void delete(String[] ids) throws GlobalException {
        for (String id : ids) {
            CommonResult<List<TrackHead>> commonResult = produceServiceClient.selectByRouterId(id);
            if (commonResult.getData().size() > 0) {
                Router router = this.getById(id);
                throw new GlobalException(router.getRouterName() + ":当前工艺已生成跟单，不能被删除", ResultCode.FAILED);
            }
        }
        for (int i = 0; i < ids.length; i++) {
            // 删除工艺关联的工序
            QueryWrapper<Sequence> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("router_id", ids[i]);
            sequenceService.remove(queryWrapper);
//如果不是历史版本，需要将历史版本先删除
//            Router r = this.getByRouterId(ids[i]).getData();
//            if (null != r && !"2".equals(r.getStatus())) {
//                List<Router> routers = this.find(null, r.getRouterNo(), null, null, r.getBranchCode(), "2", r.getTenantId()).getData();
//                for (int j = 0; j < routers.size(); j++) {
//                    routerService.removeById(routers.get(j).getId());
//                }
//            }
            this.removeById(ids[i]);
        }
    }
}
