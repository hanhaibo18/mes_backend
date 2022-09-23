package com.richfit.mes.produce.service.print;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCard;

import java.io.File;

/**
 * 重写模板打印功能流程，支持合格证
 *
 * @author zhiqiang.lu
 * @date 2022.9.23
 */
public interface TemplateService extends IService<ProduceInspectionRecordCard> {
    /**
     * 功能描述: 通过合格证id生成合格证文件
     *
     * @param id   合格证id
     * @param path 生成文件路径
     * @return File 合格证文件
     * @Author: zhiqiang.lu
     * @Date: 2022/9/23
     */
    public File certById(String id, String path) throws Exception;


    /**
     * 功能描述: 通过合格证序号，工厂代码，生成合格证文件
     *
     * @param certNo     合格证号
     * @param branchCode 工厂代码
     * @param path       生成文件路径
     * @return File 合格证文件
     * @Author: zhiqiang.lu
     * @Date: 2022/9/23
     */
    public File certByNo(String certNo, String branchCode, String path) throws Exception;
}
