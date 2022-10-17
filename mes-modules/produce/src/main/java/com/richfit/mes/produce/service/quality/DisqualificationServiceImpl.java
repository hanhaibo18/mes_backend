package com.richfit.mes.produce.service.quality;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Disqualification;
import com.richfit.mes.common.model.produce.DisqualificationUserOpinion;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.quality.DisqualificationMapper;
import com.richfit.mes.produce.dao.quality.DisqualificationUserOpinionMapper;
import com.richfit.mes.produce.entity.quality.QueryCheckDto;
import com.richfit.mes.produce.entity.quality.QueryInspectorDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: DisqualificationServiceImpl.java
 * @Author: Hou XinYu
 * @CreateTime: 2022年09月29日 15:15:00
 */
@Service
public class DisqualificationServiceImpl extends ServiceImpl<DisqualificationMapper, Disqualification> implements DisqualificationService {

    @Resource
    private DisqualificationUserOpinionService userOpinionService;

    @Resource
    private DisqualificationUserOpinionMapper userOpinionMapper;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Override
    public IPage<Disqualification> queryInspector(QueryInspectorDto queryInspectorDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        //图号查询
        if (StrUtil.isNotBlank(queryInspectorDto.getDrawingNo())) {
            queryWrapper.like("drawing_no", queryInspectorDto.getDrawingNo());
        }
        //产品名称
        if (StrUtil.isNotBlank(queryInspectorDto.getProductName())) {
            queryWrapper.like("product_name", queryInspectorDto.getProductName());
        }
        //跟单号
        if (StrUtil.isNotBlank(queryInspectorDto.getTrackNo())) {
            queryInspectorDto.setTrackNo(queryInspectorDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + queryInspectorDto.getTrackNo() + "%'");
        }
        //申请单号
        if (StrUtil.isNotBlank(queryInspectorDto.getProcessSheetNo())) {
            queryWrapper.like("process_sheet_no", queryInspectorDto.getProcessSheetNo());
        }
        // 发布/未发布
        if (null != queryInspectorDto.getIsIssue()) {
            queryWrapper.eq("is_issue", queryInspectorDto.getIsIssue());
        }
        try {
            //开始时间
            if (StrUtil.isNotBlank(queryInspectorDto.getStartTime())) {
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + queryInspectorDto.getStartTime() + " 00:00:00')");
            }
            //结束时间
            if (StrUtil.isNotBlank(queryInspectorDto.getEndTime())) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(queryInspectorDto.getEndTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
            }
        } catch (Exception e) {
            throw new GlobalException("时间格式处理错误", ResultCode.FAILED);
        }
        //只查询本人创建的不合格品申请单
        queryWrapper.eq("create_by", SecurityUtils.getCurrentUser().getUsername());
        return this.page(new Page<>(queryInspectorDto.getPage(), queryInspectorDto.getLimit()), queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveDisqualification(Disqualification disqualification) {
        this.save(disqualification);
        savePerson(disqualification.getUserList(), disqualification.getId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDisqualification(Disqualification disqualification) {
        return this.updateById(disqualification);
    }

    /**
     * 功能描述: 保存派工人员接口
     *
     * @param userList
     * @param id
     * @Author: xinYu.hou
     * @Date: 2022/10/14 17:04
     * @return: void
     **/
    private void savePerson(List<TenantUserVo> userList, String id) {
        try {
            List<DisqualificationUserOpinion> userOpinions = new ArrayList<>();
            for (TenantUserVo user : userList) {
                DisqualificationUserOpinion opinion = new DisqualificationUserOpinion();
                opinion.setDisqualificationId(id);
                //赋值用户唯一Id
                opinion.setUserId(user.getId());
                //赋值用户车间
                opinion.setUserBranch(user.getBelongOrgId());
                userOpinions.add(opinion);
            }
            userOpinionService.saveBatch(userOpinions);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("保存人员失败!", ResultCode.FAILED);
        }
    }

    @Override
    public Boolean updateIsIssue(String id, String state) {
        UpdateWrapper<Disqualification> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_issue", state);
        //开单状态增加开单时间
        if ("1".equals(state)) {
            updateWrapper.set("order_time", new Date());
        }
        return this.update(updateWrapper);
    }

    @Override
    public List<TenantUserVo> queryUser() {
        System.out.println(SecurityUtils.getCurrentUser().getUserId());
        //获取branchCode
        String value = value("qualityManagement");
        //获取TenantId
        String tenantId = baseServiceClient.queryTenantIdByBranchCode(value);
        //根据租户Id查询人员列表
        return systemServiceClient.queryUserByTenantId(tenantId);
    }

    @Override
    public IPage<Disqualification> queryCheck(QueryCheckDto queryCheckDto) {
        QueryWrapper<Disqualification> queryWrapper = new QueryWrapper<>();
        //图号查询
        if (StrUtil.isNotBlank(queryCheckDto.getDrawingNo())) {
            queryWrapper.like("dis.drawing_no", queryCheckDto.getDrawingNo());
        }
        //产品名称
        if (StrUtil.isNotBlank(queryCheckDto.getProductName())) {
            queryWrapper.like("dis.product_name", queryCheckDto.getProductName());
        }
        //跟单号
        if (StrUtil.isNotBlank(queryCheckDto.getTrackNo())) {
            queryCheckDto.setTrackNo(queryCheckDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(dis.track_no, char(13), ''), char(10), ''),' ', '') like '%" + queryCheckDto.getTrackNo() + "%'");
        }
        //申请单号
        if (StrUtil.isNotBlank(queryCheckDto.getProcessSheetNo())) {
            queryWrapper.like("dis.process_sheet_no", queryCheckDto.getProcessSheetNo());
        }
        // 处理/未处理
        if (null != queryCheckDto.getIsDispose()) {
            queryWrapper.apply(Boolean.TRUE.equals(queryCheckDto.getIsDispose()), "opinion.opinion IS NOT NULL");
            queryWrapper.apply(Boolean.FALSE.equals(queryCheckDto.getIsDispose()), "opinion.opinion IS NULL");
        }
        try {
            //开始时间
            if (StrUtil.isNotBlank(queryCheckDto.getStartTime())) {
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + queryCheckDto.getStartTime() + " 00:00:00')");
            }
            //结束时间
            if (StrUtil.isNotBlank(queryCheckDto.getEndTime())) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(queryCheckDto.getEndTime()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
            }
        } catch (Exception e) {
            throw new GlobalException("时间格式处理错误", ResultCode.FAILED);
        }
        queryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUserId());
        return userOpinionMapper.queryCheck(new Page<>(queryCheckDto.getPage(), queryCheckDto.getLimit()), queryWrapper);
    }

    /**
     * 功能描述: 获取字典
     *
     * @param code
     * @Author: xinYu.hou
     * @Date: 2022/10/14 15:16
     * @return: String
     **/
    private String value(String code) {
        try {
            CommonResult<List<ItemParam>> result = systemServiceClient.selectItemClass(code, null);
            ItemParam itemParam = result.getData().get(0);
            return itemParam.getCode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("获取检测部门错误", ResultCode.FAILED);
        }
    }

}