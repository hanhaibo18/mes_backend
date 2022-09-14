package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.QualityInspectionRules;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:51
 */
@Component
public class SystemServiceClientFallbackImpl implements SystemServiceClient {

    @Override
    public CommonResult<TenantUserVo> getUserById(String id) {
        return CommonResult.success(null);
    }

    @Override
    public List<Attachment> selectAttachmentsList(List<String> idList) {
        return null;
    }

    @Override
    public CommonResult<TenantUserVo> queryByUserAccount(String userAccount) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Boolean> delete(String id) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<ItemParam>> selectItemClass(String name, String code, String header) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<ItemParam>> selectItemClass(String code, String name) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<ItemParam> findItemParamByCode(String code) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Attachment> attachment(@PathVariable String id) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<byte[]> getAttachmentInputStream(@PathVariable String id) {
        return null;
    }


    @Override
    public CommonResult<Boolean> savenote(String sendUser,
                                          String sendTitle,
                                          String sendContent,
                                          String reseiverUsers,
                                          String branchCode,
                                          String tenantId) {
        return null;
    }

    @Override
    public CommonResult<QualityInspectionRules> queryQualityInspectionRulesById(String id) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Attachment> uploadFile(byte[] fileBytes, String fileName) {
        return null;
    }

    @Override
    public CommonResult<Object> getBase64Code(String id) throws GlobalException {
        return null;
    }

    @Override
    public CommonResult<TenantUserVo> queryByUserId(String userId) {
        return CommonResult.success(null);
    }
}
