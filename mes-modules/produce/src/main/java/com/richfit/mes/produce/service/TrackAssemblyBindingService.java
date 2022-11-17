package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackAssemblyBinding;

import java.util.List;

/**
 * @ClassName: TrackAssemblyBindingService.java
 * @Author: Hou XinYu
 * @Description: 装配绑定记录
 * @CreateTime: 2022年07月18日 10:36:00
 */
public interface TrackAssemblyBindingService extends IService<TrackAssemblyBinding> {

    /**
     * 功能描述: 新增绑定记录
     *
     * @param assembly
     * @Author: xinYu.hou
     * @Date: 2022/7/18 10:47
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> saveAssemblyBinding(TrackAssemblyBinding assembly);

    /**
     * 功能描述: 绑定
     *
     * @param id
     * @param isBinding
     * @param itemId
     * @Author: xinYu.hou
     * @Date: 2022/7/26 10:52
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> updateBinding(String id, int isBinding, String itemId);

    /**
     * 功能描述: 删除记录
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/7/18 10:56
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> deleteAssemblyBinding(String id);

    /**
     * 功能描述: 查询
     *
     * @param assemblyId
     * @Author: xinYu.hou
     * @Date: 2022/7/18 11:03
     * @return: List<TrackAssemblyBinding>
     **/
    List<TrackAssemblyBinding> queryAssemblyBindingList(String assemblyId);

    /**
     * 功能描述: 查询已绑定信息
     *
     * @param assemblyIdList
     * @Author: xinYu.hou
     * @Date: 2022/11/17 16:46
     * @return: List<TrackAssemblyBinding>
     **/
    List<TrackAssemblyBinding> queryBindingList(String assemblyIdList);
}
