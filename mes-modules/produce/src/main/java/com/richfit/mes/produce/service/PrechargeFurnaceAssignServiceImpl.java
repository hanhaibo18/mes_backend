package com.richfit.mes.produce.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.dao.PrechargeFurnaceAssignMapper;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.dao.TrackAssignPersonMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * (PrechargeFurnaceAssign)表服务实现类
 *
 * @author makejava
 * @since 2023-05-19 10:36:13
 */
@Service("prechargeFurnaceAssignService")
public class PrechargeFurnaceAssignServiceImpl extends ServiceImpl<PrechargeFurnaceAssignMapper, PrechargeFurnaceAssign> implements PrechargeFurnaceAssignService {

    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private TrackItemMapper trackItemMapper;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private TrackAssignService trackAssignService;
    @Autowired
    private TrackHeadFlowService trackHeadFlowService;
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private TrackAssignPersonMapper trackAssignPersonMapper;
    @Autowired
    private ActionService actionService;
    @Autowired
    private TrackAssignMapper trackAssignMapper;
    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;
    @Autowired
    private PrechargeFurnaceAssignService prechargeFurnaceAssignService;
    @Autowired
    private PrechargeFurnaceAssignPersonService prechargeFurnaceAssignPersonService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    @Autowired
    private TrackCompleteServiceImpl trackCompleteServiceImpl;


    @Override
    public boolean furnaceAssign(@RequestBody Assign assign, List<Long> furnaceIds) {
        //获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            for (Long furnaceId : furnaceIds) {
                //要派工的工序
                QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
                trackItemQueryWrapper.eq("precharge_furnace_id",furnaceId);
                List<TrackItem> itemList = trackAssignMapper.getPageAssignsHot(trackItemQueryWrapper);
                if (CollectionUtil.isEmpty(itemList)) {
                    throw new GlobalException("未找到可派工的工序", ResultCode.FAILED);
                }
                //更新预装炉子信息（已派工）
                UpdateWrapper<PrechargeFurnace> prechargeFurnaceWrapper = new UpdateWrapper<>();
                prechargeFurnaceWrapper.eq("id",furnaceId)
                        .set("assign_status",1);
                prechargeFurnaceService.update(prechargeFurnaceWrapper);
                //派工
                String furnaceAssignId = UUID.randomUUID().toString().replaceAll("-", "");
                for (TrackItem trackItem : itemList) {
                    TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                    //派工数量校验
                    if (trackItem.getAssignableQty() < assign.getQty()) {
                        throw new GlobalException(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty(),ResultCode.FAILED);
                    }
                    trackItem.setAssignableQty(trackItem.getAssignableQty() - assign.getQty());
                    //可派工数量为0时 工序变为已派工状态
                    if (0 == trackItem.getAssignableQty()) {
                        trackItem.setIsSchedule(1);
                    }
                    //设置派工设备
                    trackItem.setDeviceId(assign.getDeviceId());
                    //锻造计算额定工时
                    if ("4".equals(trackHead.getClasses())) {
                        trackAssignService.calculationSinglePieceHours(trackHead, trackItem);
                    }
                    if (!StringUtils.isNullOrEmpty(trackHead.getStatus()) || "0".equals(trackHead.getStatus())) {
                        //将跟单状态改为在制
                        trackHead.setStatus("1");
                        trackHeadService.updateById(trackHead);
                        UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
                        update.set("status", "1");
                        update.eq("id", trackItem.getFlowId());
                        trackHeadFlowService.update(update);
                    }
                    //构造派工信息
                    constructAssignInfo(assign, trackItem, trackHead);
                    trackAssignService.save(assign);
                    //保存派工人员信息
                    for (AssignPerson person : assign.getAssignPersons()) {
                        person.setModifyTime(new Date());
                        person.setAssignId(assign.getId());
                        trackAssignPersonMapper.insert(person);
                    }
                    //保存预装炉派工信息
                    constructFurnaceAssignInfo(assign, furnaceId, trackItem, trackHead,furnaceAssignId);
                    //保存工序信息
                    trackItemService.updateById(trackItem);

                    systemServiceClient.savenote(assign.getAssignBy(),
                            "您有新的派工跟单需要报工！",
                            assign.getTrackNo(),
                            assign.getUserId().substring(0, assign.getUserId().length() - 1),
                            assign.getBranchCode(),
                            assign.getTenantId());

                    //保存派工操作记录
                    actionService.saveAction(ActionUtil.buildAction
                            (assign.getBranchCode(), "4", "2",
                                    "跟单派工，跟单号：" + assign.getTrackNo(),
                                    OperationLogAspect.getIpAddress(request)));
                }
            }
            return true;
        } catch (Exception e) {
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    private void constructFurnaceAssignInfo(@RequestBody Assign assign, Long furnaceId, TrackItem trackItem, TrackHead trackHead,String furnaceAssignId) {
        PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(furnaceId);
        //预装炉派工表
        PrechargeFurnaceAssign prechargeFurnaceAssign = new PrechargeFurnaceAssign();
        prechargeFurnaceAssign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
        prechargeFurnaceAssign.setAssignTime(new Date());
        prechargeFurnaceAssign.setFurnaceId(furnaceId);
        prechargeFurnaceAssign.setAssignSiteId(assign.getSiteId());
        prechargeFurnaceAssign.setAssignSiteName(assign.getSiteName());
        prechargeFurnaceAssign.setAssignUser(assign.getUserId());
        prechargeFurnaceAssign.setAssignUserName(assign.getEmplName());
        prechargeFurnaceAssign.setOptType(trackItem.getOptType());
        prechargeFurnaceAssign.setTexture(trackHead.getTexture());
        prechargeFurnaceAssign.setWorkblankType(trackHead.getWorkblankType());
        prechargeFurnaceAssign.setIngotCase(trackItem.getIngotCase());
        prechargeFurnaceAssign.setTotalMoltenSteel(prechargeFurnace.getTotalMoltenSteel());
        prechargeFurnaceAssign.setSmeltingEquipment(assign.getDeviceName());
        prechargeFurnaceAssign.setBranchCode(trackItem.getBranchCode());
        prechargeFurnaceAssign.setTenantId(trackItem.getTenantId());
        prechargeFurnaceAssign.setId(furnaceAssignId);
        prechargeFurnaceAssignService.saveOrUpdate(prechargeFurnaceAssign);
        //预装炉派工id
        trackItem.setPrechargeFurnaceAssignId(prechargeFurnaceAssign.getId());
        //预装炉派工人员信息
        for (AssignPerson person : assign.getAssignPersons()) {
            PrechargeFurnaceAssignPerson prechargeFurnaceAssignPerson = new PrechargeFurnaceAssignPerson();
            prechargeFurnaceAssignPerson.setPrechargeFurnaceAssignId(prechargeFurnaceAssign.getId());
            prechargeFurnaceAssignPerson.setPrechargeFurnaceId(furnaceId);
            prechargeFurnaceAssignPerson.setUserId(person.getUserId());
            prechargeFurnaceAssignPersonService.save(prechargeFurnaceAssignPerson);
        }
    }

    /**
     * 派工构造派工信息
     * @param assign
     * @param trackItem
     * @param trackHead
     */
    private void constructAssignInfo(@RequestBody Assign assign, TrackItem trackItem, TrackHead trackHead) {
        assign.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        if (null != SecurityUtils.getCurrentUser()) {
            assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            assign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
        }
        CommonResult<TenantUserVo> user = systemServiceClient.queryByUserId(assign.getAssignBy());
        assign.setAssignName(user.getData().getEmplName());
        assign.setAssignTime(new Date());
        assign.setModifyTime(new Date());
        assign.setCreateTime(new Date());
        assign.setAvailQty(assign.getQty());
        assign.setFlowId(trackItem.getFlowId());
        assign.setTiId(trackItem.getId());
        assign.setClasses(trackHead.getClasses());
        assign.setBranchCode(trackItem.getBranchCode());
        assign.setTenantId(trackItem.getTenantId());
        assign.setState(0);
        if (StringUtils.isNullOrEmpty(assign.getTrackId())) {
            assign.setTrackNo(trackHead.getTrackNo());
            assign.setTrackId(trackHead.getId());
        }
        if (StringUtils.isNullOrEmpty(assign.getTenantId())) {
            assign.setTenantId(trackHead.getTenantId());
        }
        //处理派工人员信息  (前端没有处理userId 和userName  assignPerson为派工人列表)
        if (StringUtils.isNullOrEmpty(assign.getUserId()) && !CollectionUtil.isEmpty(assign.getAssignPersons())) {
            StringBuilder userId = new StringBuilder();
            StringBuilder userName = new StringBuilder();
            for (AssignPerson assignPerson : assign.getAssignPersons()) {
                if (!StringUtils.isNullOrEmpty(String.valueOf(userId))) {
                    userId.append(",");
                    userName.append(",");
                }
                userId.append(assignPerson.getUserId());
                userName.append(assignPerson.getUserName());
            }
            assign.setUserId(String.valueOf(userId));
            assign.setEmplName(String.valueOf(userName));
        }
        boolean isAllUser = assign.getUserId().contains("/") ? true : false;
        if (isAllUser) {
            assign.setUserId("/");
            assign.setEmplName("/");
        }
    }
}

