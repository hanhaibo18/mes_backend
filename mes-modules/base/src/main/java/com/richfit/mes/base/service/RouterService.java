package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.base.entity.QueryIsHistory;
import com.richfit.mes.base.entity.QueryProcessRecordsVo;
import com.richfit.mes.common.core.exception.GlobalException;
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
     * 功能描述: 查询历史工艺
     *
     * @param routerNo
     * @param branchCode
     * @param id
     * @param type
     * @Author: xinYu.hou
     * @Date: 2023/3/28 14:53
     * @return: List<Router>
     **/
    List<Router> getHistoryList(String routerNo, String type, String branchCode, String id);

    /**
     * 功能描述: 查询当前工艺是否为历史工艺
     *
     * @param routerId
     * @Author: xinYu.hou
     * @Date: 2022/6/24 3:59
     * @return: Boolean
     **/
    QueryIsHistory queryIsHistory(String routerId, String branchCode);

    /**
     * 功能描述:
     *
     * @param routerId
     * @Author: xinYu.hou
     * @Date: 2022/6/24 4:25
     * @return: QueryProcessRecordsVo
     **/
    QueryProcessRecordsVo queryProcessRecords(String routerId);


    /**
     * 功能描述: 通过工艺id集合批量删除工艺信息，并删除工艺的关联的工序信息。
     * 在删除前会通过工艺id查询是否已经生产跟单，如果已经生成则不能删除工艺。
     *
     * @param ids 工艺id集合
     * @Author: zhiqiang.lu
     * @Date: 2022/9/22 9:18
     */
    void delete(String[] ids) throws GlobalException;
}
