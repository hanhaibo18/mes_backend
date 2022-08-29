package com.richfit.mes.produce.controller;

import com.richfit.mes.produce.service.PublicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: PublicController.java
 * @Author: Hou XinYu
 * @Description: 工序激活
 * @CreateTime: 2022年07月22日 09:51:00
 */

@Slf4j
@Api("接口")
@RestController
@RequestMapping("/api/produce/public")
public class PublicController {
    @Resource
    private PublicService publicService;


    @ApiOperation(value = "查询计划信息", notes = "根据查询条件返回计划信息")
    @GetMapping("/thirdPartyAction")
    public Boolean thirdPartyAction() {
        //测试
        String trackHeadId = "a2157b705a8f459da440b11dc1dcf76b";
        String certificateNo = "123";
        List<String> optSequenceList = new ArrayList<>();
        optSequenceList.add("2");
        optSequenceList.add("3");
        return publicService.thirdPartyAction(trackHeadId, certificateNo, optSequenceList);
    }
}
