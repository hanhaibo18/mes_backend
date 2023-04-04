package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.sys.DataDictionary;
import com.richfit.mes.common.model.sys.DataDictionaryParam;
import com.richfit.mes.sys.dao.DataDictionaryParamDao;
import com.richfit.mes.sys.entity.dto.ItemClassDto;
import com.richfit.mes.sys.entity.dto.ItemParamDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 数据字典参数表(SysDataDictionaryParam)表服务实现类
 *
 * @author makejava
 * @since 2023-04-03 15:19:28
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DataDictionaryParamServiceImpl extends ServiceImpl<DataDictionaryParamDao, DataDictionaryParam> implements DataDictionaryParamService {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Override
    public String improtExcel(MultipartFile file, String id) {
        //根据字典id获取车间信息
        DataDictionary dataDictionary = dataDictionaryService.getById(id);
        if (dataDictionary == null) {
            throw new GlobalException("为查询到该字典ID！", ResultCode.FAILED);
        }
        //封装物料信息
        String[] materialColumns = {"orderNum", "materialNo", "materialName", "texture", "specifications"};
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
        try {
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<DataDictionaryParam> dataDictionaryParamList = ExcelUtils.importExcel(excelFile, DataDictionaryParam.class, materialColumns, 1, 0, 0, tempName.toString());
            //获取导入的物料编码
            List<String> materialNos = dataDictionaryParamList.stream().map(DataDictionaryParam::getMaterialNo).collect(Collectors.toList());
            //查询数据库中有没有已经存在的物料编码
            QueryWrapper<DataDictionaryParam> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("material_no", materialNos).eq("dictionary_id", id);
            List<DataDictionaryParam> existDictionaryParams = this.list(queryWrapper);
            //若导入的物料编码已存在，返回失败
            if (!existDictionaryParams.isEmpty()) {
                throw new GlobalException("物料编码已存在：" + existDictionaryParams.stream().map(DataDictionaryParam::getMaterialNo).collect(Collectors.toSet()), ResultCode.FAILED);
            }
            for (DataDictionaryParam dataDictionaryParam : dataDictionaryParamList) {
                dataDictionaryParam.setDictionaryId(id);
                dataDictionaryParam.setBranchCode(dataDictionary.getBranchCode());
            }
            this.saveBatch(dataDictionaryParamList);
        } catch (Exception e) {
            FileUtils.delete(excelFile);
            log.error("Excel导入物料编码异常！", e);
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
        FileUtils.delete(excelFile);
        return "import success!";
    }
}

