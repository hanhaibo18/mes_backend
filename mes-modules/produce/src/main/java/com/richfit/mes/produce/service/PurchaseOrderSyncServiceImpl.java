package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.produce.dao.ProducePurchaseOrderMapper;
import com.richfit.mes.produce.entity.PurchaseOrderSynchronizationDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: ProducePurchaseOrderSyncServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 采购订单同步
 * @CreateTime: 2022年01月19日 15:53:00
 */
@Service
@Slf4j
public class PurchaseOrderSyncServiceImpl extends ServiceImpl<ProducePurchaseOrderMapper, ProducePurchaseOrder> implements PurchaseOrderSyncService {

    @Resource
    PurchaseOrderSyncService producePurchaseOrderSyncService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Value("${interface.erp.purchase-order-synchronization}")
    private String url;

    @Override
    public List<ProducePurchaseOrder> queryPurchaseSynchronization(PurchaseOrderSynchronizationDto purchaseOrderSynchronizationDto) {
        //时间参数
        String soapRequestData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<urn:ZC80_MMIF019>" +
                //拼接查询开始时间
                "<urn:I_DATE_CHANGE>" +
                purchaseOrderSynchronizationDto.getStartTime() +
//                .append("2021-10-01")
                "</urn:I_DATE_CHANGE>" +
                //拼接查询结束时间
                "<urn:I_DATE_UNTIL>" +
//                .append("2021-10-07")
                purchaseOrderSynchronizationDto.getEndTime() +
                "</urn:I_DATE_UNTIL>" +
                //固定时间参数
                "<urn:I_TIME_CHANGE>00:00:00</urn:I_TIME_CHANGE>" +
                "<urn:I_TIME_UNTIL>23:59:59</urn:I_TIME_UNTIL>" +
                "</urn:ZC80_MMIF019>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        //构造http请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("text/xml;charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<>(soapRequestData, headers);
        RestTemplateBuilder builder = new RestTemplateBuilder();
        //设置链接超时时间
        builder.setConnectTimeout(Duration.ofMinutes(1));
        builder.setReadTimeout(Duration.ofMinutes(1));
//        RestTemplate restTemplate = builder.basicAuthentication("zbzz_esb", "ZBZZOSBinterface1").build();
        RestTemplate restTemplate = builder.basicAuthentication("OSB_USER", "welcome1").build();
        //返回结果
        String resultStr = restTemplate.postForObject(url, formEntity, String.class);
        //转换返回结果中的特殊字符，返回的结果中会将xml转义，此处需要反转移
        String tmpStr = StringEscapeUtils.unescapeXml(resultStr);
        log.info("获取数据转换结束");
        //获取工厂ID
        return xmlAnalysis(tmpStr, purchaseOrderSynchronizationDto.getCode());
    }

    /**
     * 功能描述: 定时同步采购订单
     *
     * @Author: xinYu.hou
     * @Date: 2022/1/19 15:57
     * @return: CommonResult<Boolean>
     **/
    @Override
//    @Scheduled(cron = "${time.ourchase_order}")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveTimingProducePurchaseSynchronization() {
        //拿到今天的同步数据
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        PurchaseOrderSynchronizationDto synchronizationDto = new PurchaseOrderSynchronizationDto();
        Date date = new Date();
        synchronizationDto.setStartTime(format.format(date));
        synchronizationDto.setEndTime(format.format(date));
        //获取工厂列表
        Boolean saveData = false;
        try {
            CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "", SecurityConstants.FROM_INNER);
            for (ItemParam itemParam : listCommonResult.getData()) {
                synchronizationDto.setCode(itemParam.getCode());
                List<ProducePurchaseOrder> producePurchaseOrders = producePurchaseOrderSyncService.queryPurchaseSynchronization(synchronizationDto);
                for (ProducePurchaseOrder producePurchaseOrder : producePurchaseOrders) {
                    producePurchaseOrder.setBranchCode(itemParam.getLabel());
                    producePurchaseOrder.setTenantId(itemParam.getTenantId());
                    if (removeProducePurchaseOrder(producePurchaseOrder)) {
                        continue;
                    }
                    saveData = producePurchaseOrderSyncService.save(producePurchaseOrder);
                }
            }
        } catch (Exception e) {
            saveData = false;
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return CommonResult.success(saveData);
    }

    /**
     * 功能描述: 删除重复数据 以便重新保存
     *
     * @param producePurchaseOrder
     * @Author: xinYu.hou
     * @Date: 2022/1/20 14:02
     * @return: boolean
     **/
    private boolean removeProducePurchaseOrder(ProducePurchaseOrder producePurchaseOrder) {
        if (producePurchaseOrder.getMaterialCode() == null) {
            return true;
        }
        QueryWrapper<ProducePurchaseOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", producePurchaseOrder.getOrderNo());
        queryWrapper.eq("material_no", producePurchaseOrder.getMaterialNo());
        producePurchaseOrderSyncService.remove(queryWrapper);
        return false;
    }

    /**
     * 功能描述: 保存同步信息
     *
     * @param producePurchase
     * @Author: xinYu.hou
     * @Date: 2022/1/13 14:27
     * @return: CommonResult<Boolean>
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveProducePurchaseSynchronization(List<ProducePurchaseOrder> producePurchase) {
        for (ProducePurchaseOrder order : producePurchase) {
            if (removeProducePurchaseOrder(order)) {
                continue;
            }
            producePurchaseOrderSyncService.save(order);
        }

        return CommonResult.success(true, "新增成功");
    }

    /**
     * 功能描述: 字符串解析
     *
     * @param xml
     * @param factoryId
     * @Author: xinYu.hou
     * @Date: 2022/1/13 13:57
     * @return: List<ProducePurchaseSynchronization>
     **/
    private List<ProducePurchaseOrder> xmlAnalysis(String xml, String factoryId) {
        log.info("开始处理数据");
        Document doc = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<ProducePurchaseOrder> list = new ArrayList<>();
        CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "", SecurityConstants.FROM_INNER);
//        CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "");
        Map<String, ItemParam> maps = listCommonResult.getData().stream().collect(Collectors.toMap(ItemParam::getCode, Function.identity(), (key1, key2) -> key2));
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            Iterator<Element> body = rootElt.elementIterator("Body");
            while (body.hasNext()) {
                Element bodyNext = body.next();
                Iterator<Element> response = bodyNext.elementIterator("Z_MMFM0003.Response");
                while (response.hasNext()) {
                    Element responseNext = response.next();
                    Iterator<Element> tEKPO = responseNext.elementIterator("T_EKPO");
                    Iterator<Element> tEKKO = responseNext.elementIterator("T_EKKO");
                    while (tEKKO.hasNext()) {
                        Element tEKKONext = tEKKO.next();
                        Iterator<Element> itemTEKKON = tEKKONext.elementIterator("item");
                        while (itemTEKKON.hasNext()) {
                            Element itemTEKKONNext = itemTEKKON.next();
                            String BUKRS = itemTEKKONNext.elementTextTrim("BUKRS");
                            String EKORG = itemTEKKONNext.elementTextTrim("EKORG");
                            String FRGKE = itemTEKKONNext.elementTextTrim("FRGKE");
                            Boolean is_BUKRS = itemTEKKONNext.elementTextTrim("BUKRS") != null && "K923".equals(BUKRS);
                            Boolean is_EKORG = itemTEKKONNext.elementTextTrim("EKORG") != null && ("X092".equals(EKORG) || "X070".equals(EKORG));
                            Boolean is_FRGKE = itemTEKKONNext.elementTextTrim("FRGKE") != null && "T".equals(FRGKE);
                            if (is_BUKRS && is_EKORG && is_FRGKE) {
                                String orderNo = itemTEKKONNext.elementTextTrim("EBELN");
                                String orderType = itemTEKKONNext.elementTextTrim("BSART");
                                //先用String接收数据 以后再处理
                                Date purchaseDate = format.parse(itemTEKKONNext.elementTextTrim("BEDAT"));
                                String lifnr = itemTEKKONNext.elementTextTrim("LIFNR");
                                while (tEKPO.hasNext()) {
                                    Element tEKPONext = tEKPO.next();
                                    Iterator<Element> item = tEKPONext.elementIterator("item");
                                    while (item.hasNext()) {
                                        Element itemNext = item.next();
                                        if (itemNext.elementTextTrim("EBELN").equals(itemTEKKONNext.elementTextTrim("EBELN"))
                                                && itemNext.elementTextTrim("WERKS").equals(factoryId)) {
                                            ProducePurchaseOrder purchase = new ProducePurchaseOrder();
                                            purchase.setOrderNo(orderNo);
                                            purchase.setOrderType(orderType);
                                            purchase.setPurchaseDate(purchaseDate);
                                            purchase.setLifnr(lifnr);
                                            Boolean isLOEKZ = itemNext.elementTextTrim("LOEKZ") != null && itemNext.elementTextTrim("LOEKZ").trim().equals("L");
                                            Boolean isRETPO = itemNext.elementTextTrim("RETPO") != null && !itemNext.elementTextTrim("RETPO").trim().equals("");
                                            if (isLOEKZ || isRETPO) {
                                                continue;
                                            }
                                            char zero = 48;
                                            purchase.setProjectNo(trimStringWith(itemNext.elementTextTrim("EBELP"), zero));
                                            purchase.setMaterialNo(trimStringWith(itemNext.elementTextTrim("MATNR"), zero));
                                            if (purchase.getMaterialNo() == null || purchase.getMaterialNo() == "") {
                                                continue;
                                            }
                                            //上面获取所有列表转换成MAP 用KEY去查询返回在展示到列表上
//                                            purchase.setWerks(itemNext.elementTextTrim("WERKS"));c
                                            String branchCode = maps.get(itemNext.elementTextTrim("WERKS")).getLabel();
                                            purchase.setBranchCode(branchCode);
                                            purchase.setMaterialCode(itemNext.elementTextTrim("MATKL"));
                                            String menge = itemNext.elementTextTrim("MENGE");
                                            if (menge != null && !"".equals(menge)) {
                                                purchase.setNumber((int) Float.parseFloat(menge));
                                            }
                                            //收货数量映射
                                            purchase.setPackagesNumber(itemNext.elementTextTrim("ZMENGE"));
                                            purchase.setUnit(itemNext.elementTextTrim("MEINS"));
                                            //TODO 查询数量单位 暂时没有这张表 后续完善
//                                            DataTable dt = bll.GetDescByUnit(purchase.Unit).Tables[0];
//                                            if(dt.Rows.Count > 0)
//                                            {
//                                                purchase.Desc = dt.Rows[0]["Desc"].ToString();
//                                            }
                                            purchase.setLgort(itemNext.elementTextTrim("LGORT"));
                                            CommonResult<List<Product>> productList = baseServiceClient.selectProduct(purchase.getTenantId(),purchase.getMaterialNo(), "", "");
                                            if (null != productList.getData()) {
                                                for (Product product : productList.getData()) {
                                                    purchase.setMaterialRemark(product.getMaterialDesc());
                                                    purchase.setDrawingNo(product.getDrawingNo());
                                                }
                                            }
                                            boolean isHave = false;
                                            for (ProducePurchaseOrder purchaseSynchronization : list) {
                                                if (purchaseSynchronization.getOrderNo().equals(purchase.getOrderNo())
                                                        && purchaseSynchronization.getMaterialNo().equals(purchase.getMaterialNo())) {
                                                    isHave = true;
                                                    break;
                                                }
                                            }
                                            if (!isHave) {
                                                list.add(purchase);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (DocumentException | ParseException e) {
            e.printStackTrace();
        }
        log.info("数据处理结束");
        return list;
    }

    /**
     * 功能描述:字符串截取
     *
     * @param str
     * @param beTrim
     * @Author: xinYu.hou
     * @Date: 2022/1/13 9:31
     * @return: String
     **/
    private String trimStringWith(String str, char beTrim) {
        int st = 0;
        int len = str.length();
        char[] val = str.toCharArray();
        char sbeTrim = beTrim;
        while ((st < len) && (val[st] <= sbeTrim)) {
            st++;
        }
//        while ((st < len) && (val[len - 1] <= sbeTrim)) {
//            len--;
//        }
        return st > 0 ? str.substring(st, len) : str;
    }


}
