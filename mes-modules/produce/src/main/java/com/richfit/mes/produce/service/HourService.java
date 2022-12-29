package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Hour;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 *热工工时版本
 */
public interface HourService extends IService<Hour> {

    CommonResult importExcel(MultipartFile file, String branchCode, String verId);


    void exportExcel(String parentId, String branchCode, HttpServletResponse rsp);
}
