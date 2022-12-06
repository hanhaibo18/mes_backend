package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.sys.ItemParam;

import java.util.List;

/**
 * <p>
 * 字典参数 服务类
 * </p>
 *
 * @author 王瑞
 * @since 2020-08-05
 */
public interface ItemParamService extends IService<ItemParam> {

    /**
     * 功能描述: 根据code查询字典
     *
     * @param code
     * @Author: xinYu.hou
     * @Date: 2022/10/21 10:10
     * @return: List<ItemParam>
     **/
    List<ItemParam> queryItemByCode(String code) throws Exception;

    /**
     * 根据code和租户id查询字典
     * @param code
     * @param tenantId
     * @return
     * @throws Exception
     */
    List<ItemParam> queryItemByCodeAndTenantId(String code,String tenantId) throws Exception;
}
