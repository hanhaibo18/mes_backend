package com.richfit.mes.produce.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.common.model.produce.PhysChemResult;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.PhysChemOrderMapper;
import com.richfit.mes.produce.entity.phyChemTestVo.PhyChemTaskVo;
import com.richfit.mes.produce.utils.WordUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 理化检验接口
 */
@Service
@Slf4j
public class PhyChemTestService{


    @Autowired
    private PhysChemOrderService physChemOrderService;
    @Autowired
    private PhysChemResultService physChemResultService;
    @Autowired
    private WordUtil wordUtil;
    @Autowired
    private PhysChemOrderMapper physChemOrderMapper;
    @Autowired
    private TrackHeadService trackHeadService;

    /**
     * 查询跟单工序发起委托列表
     * @param phyChemTaskVo
     * @return
     */
    public IPage<PhysChemOrder> page(PhyChemTaskVo phyChemTaskVo) {
        phyChemTaskVo.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        //对应mapper文件中的别名
        String orderTableName = null;
        //跟单排序字段
        List<String> list = new ArrayList<>();
        list.add("product_name");
        list.add("drawing_no");
        list.add("track_no");
        if(!StringUtils.isEmpty(phyChemTaskVo.getOrderCol()) && !StringUtils.isEmpty(phyChemTaskVo.getOrder())){
            if(list.contains(StrUtil.toUnderlineCase(phyChemTaskVo.getOrderCol()))){
                orderTableName = "head";
            }else{
                orderTableName = "py_order";
            }
            phyChemTaskVo.setOrderCol(StrUtil.toUnderlineCase(phyChemTaskVo.getOrderCol()));
        }

        IPage<PhysChemOrder> physChemOrderIPage = physChemOrderMapper.queryTestPageList(new Page(phyChemTaskVo.getPage(), phyChemTaskVo.getLimit()), phyChemTaskVo,orderTableName);

        return physChemOrderIPage;
    }


    /**
     * 修改委托单状态
     * @param status
     * @param id
     */
    public void changeOrderStaus(String status,String id){
        PhysChemOrder physChemOrder = new PhysChemOrder();
        physChemOrder.setId(id);
        physChemOrder.setStatus(status);
        physChemOrderService.updateById(physChemOrder);
    }

    /**
     * 同步试验结果
     * @param itemIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncResult(List<String> itemIds){
        //构造存储本地理化试验结果数据
        List<PhysChemResult> physChemResults = new ArrayList<>();
        for (String itemId : itemIds) {
            //调用理化试验接口获取试验结果数据
            try {

            }catch (Exception e){
                log.error("调用化验同步数据接口错误！", e);
            }
        }

        //删除旧的实验结果数据(调试时需要确定重复同步情况是否更新)
        List<PhysChemResult> oldResults = physChemResultService.list(new QueryWrapper<PhysChemResult>().in("item_id", itemIds));
        if(oldResults.size()>0){
            List<String> oldResultsIds = oldResults.stream().map(PhysChemResult::getId).collect(Collectors.toList());
            physChemResultService.removeByIds(oldResultsIds);
        }
        //保存新的测试结果数据
        physChemResultService.saveBatch(physChemResults);
    }

    /**
     * 理化检测报告导出
     * @param response
     * @throws IOException
     * @throws TemplateException
     */
    public void exoprtReport(HttpServletResponse response,String hid) throws IOException, TemplateException {
        //跟单数据
        TrackHead trackHead = trackHeadService.getById(hid);
        //委托单数据 根据batch_no查询
        QueryWrapper<PhysChemOrder> physChemOrderQueryWrapper = new QueryWrapper<>();
        physChemOrderQueryWrapper.eq("batch_no",trackHead.getBatchNo());
        List<PhysChemOrder> orders = physChemOrderService.list(physChemOrderQueryWrapper);
        //试验结果数据
        QueryWrapper<PhysChemResult> physChemResultQueryWrapper = new QueryWrapper<>();
        physChemResultQueryWrapper.eq("batch_no",trackHead.getBatchNo());
        List<PhysChemResult> results = physChemResultService.list(physChemResultQueryWrapper);

        //构造填充数据
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("rel","Rel");
        dataMap.put("forceTensileElongation","A50mm");
        dataMap.put("forceBendDirection","横向");
        dataMap.put("forceImpactDirection","横向");
        dataMap.put("forceTensileDirection","纵向");
        dataMap.put("forceBendType","截面1/2");
        dataMap.put("forceFlaserNumber","1232");
        dataMap.put("w","10mm");
        dataMap.put("kp","300j");
        dataMap.put("kv2","kv2");
        List<String> headerNames = new ArrayList<>();
        headerNames.add("V");
        headerNames.add("Nb");
        headerNames.add("Ti");
        dataMap.put("headerNames",headerNames);
        createDataMap(dataMap);
        //导出
        wordUtil.exoprtReport(response,dataMap,"lhjcTemp.ftl","理化检测报告");
    }

    /**
     * 构造模板参数
     * @param dataMap
     */
    private void createDataMap(Map<String, Object> dataMap){


    }

}
