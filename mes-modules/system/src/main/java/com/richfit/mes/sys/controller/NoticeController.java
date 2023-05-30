package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Notice;
import com.richfit.mes.sys.entity.dto.SalesSchedulingDto;
import com.richfit.mes.sys.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName: NoticeController.java
 * @Author: Hou XinYu
 * @Description: 排产通知
 * @CreateTime: 2023年05月30日 14:50:00
 */

@Api("排产通知")
@RestController
@RequestMapping("/api/sys/notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    /**
     * 功能描述: 销售排产报公告分页查询接口
     *
     * @param salesSchedulingDto
     * @Author: xinYu.hou
     * @Date: 2023/5/29 19:06
     * @return: IPage<Notice>
     **/
    @ApiOperation(value = "销售排产分页查询", notes = "根据条件查询销售排产数据")
    @PostMapping("/query_sales_page")
    public CommonResult<IPage<Notice>> queryPage(@RequestBody SalesSchedulingDto salesSchedulingDto) {
        return CommonResult.success(noticeService.queryPage(salesSchedulingDto));
    }

    /**
     * 功能描述: 接受通知
     *
     * @param id
     * @Author: xinYu.hou
     * @Date: 2023/5/29 19:08
     * @return: Boolean
     **/
    @ApiOperation(value = "接受通知", notes = "根据ID修改通知状态")
    @GetMapping("/acceptance_notice")
    public CommonResult<Boolean> acceptanceNotice(String id) {
        return CommonResult.success(noticeService.acceptanceNotice(id));
    }
}
