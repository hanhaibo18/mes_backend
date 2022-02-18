package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Drafts;
import com.richfit.mes.sys.entity.dto.QueryDto;

/**
 * @ClassName: DraftsService.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月17日 16:21:00
 */
public interface DraftsService extends IService<Drafts> {
    /**
     * 功能描述: 草稿箱列表查询
     * @Author: xinYu.hou
     * @Date: 2022/2/17 16:36
     * @param queryDto
     * @return: CommonResult<IPage<Drafts>>
     **/
    CommonResult<IPage<Drafts>> queryDraftsList(QueryDto<Drafts> queryDto);
    /**
     * 功能描述: 根据ID物理删除
     * @Author: xinYu.hou
     * @Date: 2022/2/17 16:37
     * @param id
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> deleteDraftsById(String id);
    /**
     * 功能描述: 查询草稿箱详情
     * @Author: xinYu.hou
     * @Date: 2022/2/17 16:37
     * @param queryDto
     * @return: CommonResult<Drafts>
     **/
    CommonResult<Drafts> queryDraftsById(QueryDto<String> queryDto);
    /**
     * 功能描述: 修改草稿箱
     * @Author: xinYu.hou
     * @Date: 2022/2/17 16:37
     * @param drafts
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> updateDrafts(Drafts drafts);
}
