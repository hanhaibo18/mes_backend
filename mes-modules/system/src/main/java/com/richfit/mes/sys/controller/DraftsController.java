package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Drafts;
import com.richfit.mes.sys.entity.dto.QueryDto;
import com.richfit.mes.sys.service.DraftsService;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: DraftsContoller.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年03月09日 14:42:00
 */
@Api("站内短信-草稿箱")
@RestController
@RequestMapping("/api/sys/drafts")
public class DraftsController {
    @Resource
    private DraftsService draftsService;

    /**
     * 功能描述: 草稿箱列表查询
     * @Author: xinYu.hou
     * @Date: 2022/2/17 16:36
     * @param queryDto
     * @return: CommonResult<IPage<Drafts>>
     **/
    @PostMapping("/query/page")
    public CommonResult<IPage<Drafts>> queryDraftsList(@RequestBody QueryDto<String> queryDto){
        return draftsService.queryDraftsList(queryDto);
    }
    /**
     * 功能描述: 根据ID物理删除
     * @Author: xinYu.hou
     * @Date: 2022/2/17 16:37
     * @param idList
     * @return: CommonResult<Boolean>
     **/
    @DeleteMapping("/delete")
    public CommonResult<Boolean> deleteDraftsById(@RequestBody List<Drafts> idList) {
        Boolean delete = false;
        for (Drafts drafts : idList) {
            delete = draftsService.deleteDraftsById(drafts.getId());
        }
        return CommonResult.success(delete);
    }
    /**
     * 功能描述: 查询草稿箱详情
     * @Author: xinYu.hou
     * @Date: 2022/2/17 16:37
     * @param id
     * @return: CommonResult<Drafts>
     **/
    @GetMapping("/query/query_draft_one/{id}")
    public CommonResult<Drafts> queryDraftsById(@PathVariable String id){
        return draftsService.queryDraftsById(id);
    }
    /**
     * 功能描述: 修改草稿箱
     * @Author: xinYu.hou
     * @Date: 2022/2/17 16:37
     * @param drafts
     * @return: CommonResult<Boolean>
     **/
    @PutMapping("/update")
    public CommonResult<Boolean> updateDrafts(Drafts drafts){
        return draftsService.updateDrafts(drafts);
    }

    /**
     * 功能描述: 保存草稿
     * @Author: xinYu.hou
     * @Date: 2022/3/9 16:43
     * @param drafts
     * @return: CommonResult<Boolean>
     **/
    @PostMapping("/save")
    public CommonResult<Boolean> saveDrafts(@RequestBody Drafts drafts){
        return draftsService.saveDrafts(drafts);
    }
}
