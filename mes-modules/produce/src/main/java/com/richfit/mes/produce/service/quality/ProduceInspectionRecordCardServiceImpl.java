package com.richfit.mes.produce.service.quality;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.RouterCheck;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.quality.ProduceInspectionRecordCardContentMapper;
import com.richfit.mes.produce.dao.quality.ProduceInspectionRecordCardMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.utils.InspectionRecordCardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022.8.25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProduceInspectionRecordCardServiceImpl extends ServiceImpl<ProduceInspectionRecordCardMapper, ProduceInspectionRecordCard> implements ProduceInspectionRecordCardService {

    @Autowired
    public ProduceInspectionRecordCardContentMapper produceInspectionRecordCardContentMapper;

    @Autowired
    public TrackHeadService trackHeadService;

    @Autowired
    public TrackHeadFlowService trackHeadFlowService;

    @Autowired
    public TrackCheckService trackCheckService;

    @Autowired
    public TrackCheckDetailService trackCheckDetailService;

    @Autowired
    public TrackItemService trackItemService;

    @Autowired
    public CodeRuleService codeRuleService;

    @Resource
    private BaseServiceClient baseServiceClient;

    /**
     * 功能描述: 质量检验卡保存
     *
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    @Override
    public void saveProduceInspectionRecordCard(ProduceInspectionRecordCard produceInspectionRecordCard) {
        produceInspectionRecordCard.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        this.saveOrUpdate(produceInspectionRecordCard);
    }

    /**
     * 功能描述: 质量检验卡质检明细信息更新
     *
     * @param produceInspectionRecordCardContent 质量检验卡明细信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    @Override
    public void updateTrackCheckDetail(ProduceInspectionRecordCardContent produceInspectionRecordCardContent) {
        TrackFlow trackFlow = trackHeadFlowService.getById(produceInspectionRecordCardContent.getFlowId());
        trackFlow.setIsExamineCardData(TrackFlow.EXAMINE_CARD_DATA_XG);
        trackHeadFlowService.updateById(trackFlow);

        TrackCheckDetail trackCheckDetail = trackCheckDetailService.getById(produceInspectionRecordCardContent.getId());
        trackCheckDetail.setValue(produceInspectionRecordCardContent.getInspectionResult());
        trackCheckDetail.setResult(Integer.parseInt(produceInspectionRecordCardContent.getInspectionQualified()));
        trackCheckDetailService.updateById(trackCheckDetail);
    }

    /**
     * 功能描述: 质量检验卡更新
     *
     * @param produceInspectionRecordCard 质量检验卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    @Override
    public void updateProduceInspectionRecordCard(ProduceInspectionRecordCard produceInspectionRecordCard) {
        produceInspectionRecordCard.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        this.updateById(produceInspectionRecordCard);
    }

    /**
     * 功能描述: 质量检验卡查询
     *
     * @param flowId 质量检测卡id/flowID
     * @return ProduceInspectionRecordCard 质量检测卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    @Override
    public ProduceInspectionRecordCard selectProduceInspectionRecordCard(String flowId) throws Exception {
        ProduceInspectionRecordCard produceInspectionRecordCard = this.getById(flowId);
        //质量检测卡基本信息查询
        TrackFlow trackFlow = trackHeadFlowService.getById(flowId);
        TrackHead trackHead = trackHeadService.getById(trackFlow.getTrackHeadId());
        trackHead.setFlowId(flowId);
        trackHead.setIsCardData(trackFlow.getIsCardData());
        trackHead.setProductNo(trackFlow.getProductNo());
        produceInspectionRecordCard = ProduceInspectionRecordCard.byTrackHead(produceInspectionRecordCard, trackHead);
        //给质量检测卡添加号码
        if (StrUtil.isBlank(produceInspectionRecordCard.getCardNo())) {
            //质量检测卡流水号获取
            InspectionRecordCardUtil.cardNo(produceInspectionRecordCard, codeRuleService);
            this.saveOrUpdate(produceInspectionRecordCard);
        }

        //质量检测卡内容明细list
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = new ArrayList<>();

        //获取质检信息与其他信息（质检信息、工序合格证、探伤记录）
        produceInspectionRecordCardContentList.addAll(this.selectItemCheckList(flowId, null));

        //材料追溯（炉号）
        produceInspectionRecordCardContentList.addAll(ProduceInspectionRecordCardContent.listByTrackHead(produceInspectionRecordCard));

        //质量检测卡序号统一处理
        int i = 1;
        for (ProduceInspectionRecordCardContent p : produceInspectionRecordCardContentList) {
            p.setInspectionNo(i++ + "");
        }

        //数据整合
        produceInspectionRecordCard.setProduceInspectionRecordCardContentList(produceInspectionRecordCardContentList);
        return produceInspectionRecordCard;
    }

    /**
     * 功能描述: 工序质检信息查询
     *
     * @param flowId 质量检测卡id/flowID，如果itemId有值flowId可以为null
     * @param itemId 工序id，null为查询全部工序质检信息
     * @return ProduceInspectionRecordCard 质量检测卡信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/29 15:06
     */
    @Override
    public List<ProduceInspectionRecordCardContent> selectItemCheckList(String flowId, String itemId) throws Exception {
        //记录检验卡明细
        List<ProduceInspectionRecordCardContent> produceInspectionRecordCardContentList = new ArrayList<>();
        //获取工序信息
        QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<>();
        queryWrapperTrackItem.eq(!StrUtil.isBlank(flowId), "flow_id", flowId);
        queryWrapperTrackItem.eq(!StrUtil.isBlank(itemId), "id", itemId);
        queryWrapperTrackItem.orderByAsc("opt_sequence");
        List<TrackItem> trackItemList = trackItemService.list(queryWrapperTrackItem);
        //质检信息
        QueryWrapper<TrackCheck> queryWrapperTrackCheck = new QueryWrapper<>();
        queryWrapperTrackCheck.eq(!StrUtil.isBlank(flowId), "flow_id", flowId);
        queryWrapperTrackCheck.eq(!StrUtil.isBlank(itemId), "ti_id", itemId);
        List<TrackCheck> trackCheckList = trackCheckService.list(queryWrapperTrackCheck);
        //质检明细
        QueryWrapper<TrackCheckDetail> queryWrapperTrackCheckDetail = new QueryWrapper<>();
        queryWrapperTrackCheckDetail.eq(!StrUtil.isBlank(flowId), "flow_id", flowId);
        queryWrapperTrackCheckDetail.eq(!StrUtil.isBlank(itemId), "ti_id", itemId);
        List<TrackCheckDetail> trackCheckDetailList = trackCheckDetailService.list(queryWrapperTrackCheckDetail);
        //质检信息数据重组（将质检明细与质检信息合并）
        for (TrackCheck trackCheck : trackCheckList) {
            List<TrackCheckDetail> list = new ArrayList<>();
            for (TrackCheckDetail trackCheckDetail : trackCheckDetailList) {
                //当质检的id等于质检明细的质检id时进行数据的封装
                if (trackCheck.getId().equals(trackCheckDetail.getTrackCheckId())) {
                    CommonResult<RouterCheck> result = baseServiceClient.routerCheckSelectById(trackCheckDetail.getCheckId());
                    trackCheckDetail.setRouterCheck(result.getData());
                    list.add(trackCheckDetail);
                }
            }
            trackCheck.setCheckDetailsList(list);
        }
        //获取质检信息与其他信息（质检信息、工序合格证、探伤记录）
        for (TrackItem trackItem : trackItemList) {
            produceInspectionRecordCardContentList.addAll(ProduceInspectionRecordCardContent.listByTrackItem(trackItem, trackCheckList));
        }
        return produceInspectionRecordCardContentList;
    }
}
