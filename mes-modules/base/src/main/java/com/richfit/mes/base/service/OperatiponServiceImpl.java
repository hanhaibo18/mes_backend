package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.OperatiponMapper;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Operatipon;
import com.richfit.mes.common.model.base.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OperatiponServiceImpl extends ServiceImpl<OperatiponMapper, Operatipon> implements OperatiponService {

    @Autowired
    private OperatiponMapper operatiponMapper;


    @Autowired
    private SequenceService sequenceService;

    @Override
    public IPage<Operatipon> selectPage(Page page, QueryWrapper<Operatipon> qw) {
        return operatiponMapper.selectPage(page, qw);
    }

    /**
     * 功能描述: 通过工序字典id集合批量删除工序字典信息。
     * 在删除前会通过工序字典id查询是否已经匹配过工艺，如果已经匹配工艺则提示错误不能进行删除。
     *
     * @param ids 工序字典id集合
     * @Author: zhiqiang.lu
     * @Date: 2022/9/22 9:18
     */
    @Override
    public void delete(String[] ids) throws GlobalException {
        for (String id : ids) {
            QueryWrapper<Sequence> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("opt_id", id);
            if (sequenceService.list(queryWrapper).size() > 0) {
                Operatipon operatipon = this.getById(id);
                throw new GlobalException(operatipon.getOptName() + ":当前工序字典已经匹配过工艺，不能被删除", ResultCode.FAILED);
            }
        }
        this.removeByIds(java.util.Arrays.asList(ids));
    }

}
