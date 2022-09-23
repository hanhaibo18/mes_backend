package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Operatipon;

/**
 * @author 马峰
 * @Description 工艺服务
 */
public interface OperatiponService extends IService<Operatipon> {

    public IPage<Operatipon> selectPage(Page page, QueryWrapper<Operatipon> qw);

    /**
     * 功能描述: 通过工序字典id集合批量删除工序字典信息。
     * 在删除前会通过工序字典id查询是否已经匹配过工艺，如果已经匹配工艺则提示错误不能进行删除。
     *
     * @param ids 工序字典id集合
     * @Author: zhiqiang.lu
     * @Date: 2022/9/22 9:18
     */
    void delete(String[] ids) throws GlobalException;

    /**
     * 修改工序字典信息
     * @param operatipon
     * @return
     */
    CommonResult updateOperatipon(Operatipon operatipon);
}
