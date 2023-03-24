package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.sys.ItemClass;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.dao.ItemParamMapper;
import com.richfit.mes.sys.entity.dto.ItemClassDto;
import com.richfit.mes.sys.entity.dto.ItemParamDto;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典分类 服务实现类
 * </p>
 *
 * @author 王瑞
 * @since 2020-08-05
 */
@Service
public class ItemParamServiceImpl extends ServiceImpl<ItemParamMapper, ItemParam> implements ItemParamService {

    @Resource
    ItemParamMapper itemParamMapper;

    @Resource
    private ItemClassService itemClassService;

    @Override
    public List<ItemParam> queryItemByCode(String code) throws Exception {
        QueryWrapper<ItemClass> queryWrapper = new QueryWrapper<ItemClass>();
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        List<ItemClass> iClasses = itemClassService.list(queryWrapper);
        if (iClasses.size() > 0) {
            QueryWrapper<ItemParam> wrapper = new QueryWrapper<>();
            wrapper.eq("class_id", iClasses.get(0).getId());
            if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
                wrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            }
            wrapper.orderByAsc("order_num");
            return this.list(wrapper);
        } else {
            throw new Exception("没有找到key=" + code + "的字典！");
        }
    }

    @Override
    public List<ItemParam> queryItemByCodeAndTenantId(String code, String tenantId) throws Exception {
        QueryWrapper<ItemClass> queryWrapper = new QueryWrapper<ItemClass>();
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
            queryWrapper.eq("tenant_id", tenantId);
        }
        List<ItemClass> iClasses = itemClassService.list(queryWrapper);
        if (iClasses.size() > 0) {
            QueryWrapper<ItemParam> wrapper = new QueryWrapper<>();
            wrapper.eq("class_id", iClasses.get(0).getId());
            if (tenantId != null) {
                wrapper.eq("tenant_id", tenantId);
            }
            wrapper.orderByAsc("order_num");
            return this.list(wrapper);
        } else {
            throw new Exception("没有找到key=" + code + "的字典！");
        }
    }

    /**
     * 字典导入excel
     * @param file
     * @return
     */
    @Override
    public CommonResult<String> importItemParamByExcel(MultipartFile file) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        String tenantId = currentUser.getTenantId();
        //封装字典信息实体类 code 唯一
        String[] ItemClassNames = {"ruleNo", "code", "name"};
        String[] ItemParamNames = {"ruleNo", "orderNo", "code", "label", "unit", "type", "orderNum"};

        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);

            //将导入的excel数据生成证件实体类list
            List<ItemClassDto> itemClassDtoList = ExcelUtils.importExcel(excelFile, ItemClassDto.class, ItemClassNames, 1, 0, 0, tempName.toString());
            List<ItemParamDto> itemParamDtoList = ExcelUtils.importExcel(excelFile, ItemParamDto.class, ItemParamNames, 1, 0, 1, tempName.toString());

            FileUtils.delete(excelFile);

            if (org.springframework.util.CollectionUtils.isEmpty(itemClassDtoList)) {
                return CommonResult.failed("未检测到有字典导入！");
            }

            // 判断该编码是否存在
            boolean exist = false;
            List<String> existRuleNoList = new ArrayList<>();
            for (ItemClassDto itemClassDto : itemClassDtoList) {
                if (StringUtils.isNullOrEmpty(itemClassDto.getName())) {
                    return CommonResult.failed("分类名不能为空！");
                }
                if (StringUtils.isNullOrEmpty(itemClassDto.getCode())) {
                    return CommonResult.failed("编码不能为空！");
                }
                itemClassDto.setTenantId(tenantId);

                if (CheckCodeExist(itemClassDto)) {
                    exist = true;
                    existRuleNoList.add(itemClassDto.getRuleNo());
                }
            }
            if (exist) {
                return CommonResult.failed("编码已存在，编码序号：" + existRuleNoList);
            }

            // 将itemClassDtoList 拷贝到 itemClassList
            List<ItemClass> itemClassList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(itemClassDtoList)) {
                for (ItemClassDto itemClassDto : itemClassDtoList) {
                    ItemClass itemClass = new ItemClass();
                    BeanUtils.copyProperties(itemClassDto, itemClass);
                    itemClassList.add(itemClass);
                }
            }

            // 批量保存
            itemClassService.saveBatch(itemClassList);

            if (CollectionUtils.isNotEmpty(itemParamDtoList)) {
                List<ItemParam> itemParamList = new ArrayList<>();
                for (ItemParamDto itemParamDto : itemParamDtoList) {
                    ItemParam itemParam = new ItemParam();
                    BeanUtils.copyProperties(itemParamDto, itemParam);
                    itemParamList.add(itemParam);
                }
                Map<String, List<ItemParam>> itemParamMap = itemParamList.stream().collect(Collectors.groupingBy(ItemParam::getRuleNo));
                for (ItemClass itemClass: itemClassList) {
                    String itemClassId = itemClass.getId();
                    if (!StringUtil.isNullOrEmpty(itemClass.getRuleNo())) {
                        List<ItemParam> list = itemParamMap.get(itemClass.getRuleNo());
                        for (ItemParam itemParam: list) {
                            itemParam.setClassId(itemClassId);
                            itemParam.setTenantId(tenantId);
                        }
                        this.saveBatch(itemParamMap.get(itemClass.getRuleNo()));
                    }
                }
            }

        } catch (IOException e) {
            log.error("解析excel失败:", e);
        }
        return CommonResult.success("excel导入成功！");
    }

    /**
     * 判断编码序号是否存在
     * @param itemClassDto
     * @return
     */
    private boolean CheckCodeExist(ItemClassDto itemClassDto) {
        String errorStr = "";
        QueryWrapper<ItemClass> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", itemClassDto.getCode());
        queryWrapper.eq("tenant_id", itemClassDto.getTenantId());
        List<ItemClass> itemClassList = itemClassService.list(queryWrapper);

        if (itemClassList.size() > 0) {
            errorStr = "编码序号" + itemClassDto.getCode() + "已存在，切勿重复添加";
        }

        if (!StringUtils.isNullOrEmpty(errorStr)) {
            throw new GlobalException(errorStr, ResultCode.FAILED);
        }
        return false;
    }

}