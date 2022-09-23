package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.OperatiponMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Operatipon;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OperatiponServiceImpl extends ServiceImpl<OperatiponMapper, Operatipon> implements OperatiponService {

    private static final String UPDATE_TYPE = "UPDATE"; //修改操作

    private static final String DELETE_TYPE = "DELETE"; //删除操作

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

    /**
     * 修改工序字典信息
     * @param operatipon
     * @return
     */
    @Override
    public CommonResult updateOperatipon(Operatipon operatipon){
        if (StringUtils.isNullOrEmpty(operatipon.getOptCode())) {
            return CommonResult.failed("机构编码不能为空！");
        } else {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            operatipon.setModifyBy(user.getUsername());
            operatipon.setModifyTime(new Date());
            operatipon.setTenantId(user.getTenantId());
            boolean bool = this.updateById(operatipon);
            if (bool) {
                return CommonResult.success(operatipon, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

}
