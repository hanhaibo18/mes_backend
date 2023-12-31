package com.richfit.mes.produce.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.NormalizeDehydroExecuteRecord;
import com.richfit.mes.common.model.produce.NormalizeDehydroRecord;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.NormalizeDehydroRecordExecuteMapper;
import com.richfit.mes.produce.dao.NormalizeDehydroRecordMapper;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import com.richfit.mes.produce.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 正火去氢工序控制记录(ProduceNormalizeDehydroRecord)表服务实现类
 *
 * @author makejava
 * @since 2023-03-23 14:18:24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class NormalizeDehydroRecordServiceImpl extends ServiceImpl<NormalizeDehydroRecordMapper, NormalizeDehydroRecord> implements NormalizeDehydroRecordService {

    @Autowired
    private NormalizeDehydroRecordMapper normalizeDehydroRecordMapper;
    @Autowired
    private NormalizeDehydroRecordExecuteMapper normalizeDehydroRecordExecuteMapper;
    @Autowired
    private NormalizeDehydroExecuteRecordService normalizeDehydroExecuteRecordService;
    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;
    @Autowired
    private SystemServiceClient systemServiceClient;
    /**
     * 添加正火去氢记录
     * @param record
     * @return
     */
    @Override
    public Boolean saveNormalizeDehydroRecord(NormalizeDehydroRecord record) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //记录编号
        String timeStemp = String.valueOf(System.currentTimeMillis());
        String yyyyMMddhhmmss = DateUtils.dateToString(new Date(), "yyyyMMddhhmmss");
        //记录编号
        record.setSerialNo(yyyyMMddhhmmss+timeStemp.substring(timeStemp.length()-4));
        //'审核状态 0 未审核  1 通过,2 未通过
        record.setAuditStatus(0);
        int insert = normalizeDehydroRecordMapper.insert(record);
        if (CollectionUtils.isNotEmpty(record.getExecuteRecord())){
            for (NormalizeDehydroExecuteRecord normalizeDehydroExecuteRecord : record.getExecuteRecord()) {
                normalizeDehydroExecuteRecord.setRecordId(record.getId());
            }
            //保存工艺执行记录
            normalizeDehydroExecuteRecordService.saveBatch(record.getExecuteRecord());
        }

        if(insert>0){
            //修改装炉为已生成记录 record_status 记录状态  0 未生成记录，3已生成记录， 1 审核通过,2 审核未通过
            //同步装炉记录状态
            if(StringUtils.isNotEmpty(record.getFurnaceId())){
                this.synchronizationRecordStatus(record.getFurnaceId(),"3");
            }else {
                throw new GlobalException("缺少预装炉id", ResultCode.FAILED);
            }
            return true;
        }else {
            return false;
        }

    }



    /**
     *设置装炉中的正火去氢记录审核状态
     * @param furnaceId  炉号
     * @param status  record_status 记录状态  0 未生成记录，3已生成记录， 1 审核通过,2 审核未通过
     */
    @Override
    public void synchronizationRecordStatus(String furnaceId, String status) {
        prechargeFurnaceService.updateRecordStatus(Long.valueOf(furnaceId),status);
    }

    /**
     * 修改正火去氢记录
     * @param normalizeDehydroRecord
     * @return
     */

    @Override
    public boolean updateNormalizeDehydroRecord(NormalizeDehydroRecord normalizeDehydroRecord) {
        TenantUserDetails currentUser = SecurityUtils.getCurrentUser();
        //只有班组长或者自己创建的记录能够执行修改
        if(this.isBzz(currentUser)||currentUser.getUsername().equals(normalizeDehydroRecord.getCreateBy())){
            if (CollectionUtils.isNotEmpty(normalizeDehydroRecord.getExecuteRecord())){
                QueryWrapper<NormalizeDehydroExecuteRecord> queryWrapper=new QueryWrapper();
                queryWrapper.in("record_id",normalizeDehydroRecord.getId());
                //删除工艺执行记录
                normalizeDehydroExecuteRecordService.remove(queryWrapper);
                //添加工艺执行记录
                normalizeDehydroExecuteRecordService.saveBatch(normalizeDehydroRecord.getExecuteRecord());
            }
            //每次修改需要把状态改为未审核(需要重新审核)
            normalizeDehydroRecord.setAuditStatus(0);
            int i = normalizeDehydroRecordMapper.updateById(normalizeDehydroRecord);
            if(i>0){
                //修改装炉为已生成记录 record_status 记录状态  0 未生成记录，3已生成记录， 1 审核通过,2 审核未通过
                //同步装炉记录状态
                if(StringUtils.isNotEmpty(normalizeDehydroRecord.getFurnaceId())){
                    this.synchronizationRecordStatus(normalizeDehydroRecord.getFurnaceId(),"3");
                }
                return true;
            }else {
                return false;
            }
        }else {
            throw new GlobalException("修改失败!无班组长权限",ResultCode.FAILED);
        }

    }


    /**
     * 根据id查询记录
     * @param id
     * @return
     */
    @Override
    public NormalizeDehydroRecord getById(String id) {
        NormalizeDehydroRecord normalizeDehydroRecord = normalizeDehydroRecordMapper.selectById(id);
        if(ObjectUtil.isNotEmpty(normalizeDehydroRecord)){
            QueryWrapper<NormalizeDehydroExecuteRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("record_id",id);
            List<NormalizeDehydroExecuteRecord> list = normalizeDehydroExecuteRecordService.list(queryWrapper);
            normalizeDehydroRecord.setExecuteRecord(list);
        }
        return normalizeDehydroRecord;
    }

    /**
     * 是否为班组长
     * @return
     */
    @Override
    public boolean isBzz(TenantUserDetails currentUser) {
        //获取登录用户权限
        List<Role> roles = systemServiceClient.queryRolesByUserId(currentUser.getUserId());
        Set<String> rolesCode = roles.stream().map(Role::getRoleCode).collect(Collectors.toSet());
        //班组长标识
        String bzzBs = "JMAQ_BZZZ";
        boolean isBzz = false;
        for (String code : rolesCode) {
            if (code.endsWith(bzzBs)) {
                isBzz = true;
                break;
            }
        }
        return isBzz;
    }


}

