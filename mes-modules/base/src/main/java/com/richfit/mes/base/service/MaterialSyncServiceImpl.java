package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.ProductMapper;
import com.richfit.mes.base.entity.MaterialSyncDto;
import com.richfit.mes.base.entity.MaterialTypeDto;
import com.richfit.mes.base.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.security.constant.SecurityConstants;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: MaterialSyncServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年02月10日 09:07:00
 */
@Slf4j
@Service
@EnableScheduling
public class MaterialSyncServiceImpl extends ServiceImpl<ProductMapper, Product> implements MaterialSyncService {

    @Value("http://emaip.erp.cnpc:80/ZBZZ/ERPPP/IS_MES_SAPMaterialBase/MES_SAPMaterialBase")
    private String url;

    @Resource
    private MaterialSyncService materialSyncService;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Override
    public List<Product> queryProductSync(MaterialSyncDto materialSyncDto) {
        String soapRequestData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:Z_PPFM0004>\n" +
                "         <urn:ZDATUM>"+materialSyncDto.getDate()+"</urn:ZDATUM>\n" +
                "         <urn:ZWERKS>\n" +
                "         <urn:WERKS>"+materialSyncDto.getCode()+"</urn:WERKS>\n" +
                "         </urn:ZWERKS>\n" +
                "     \n" +
                "      </urn:Z_PPFM0004>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        //构造http请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("text/xml;charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<>(soapRequestData, headers);
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();
        //返回结果
        String resultStr = restTemplate.postForObject(url, formEntity, String.class);
        //转换返回结果中的特殊字符，返回的结果中会将xml转义，此处需要反转移
        String tmpStr = StringEscapeUtils.unescapeXml(resultStr);
        //获取工厂ID
        return xmlAnalysis(tmpStr);
    }
    private List<Product> xmlAnalysis(String xml){
        Document doc = null;
        char zero = 48;
        List<Product> list = new ArrayList<>();
        CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "", SecurityConstants.FROM_INNER);
        Map<String, ItemParam> maps = listCommonResult.getData().stream().collect(Collectors.toMap(ItemParam::getCode, Function.identity(), (key1, key2) -> key2));
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            Iterator<Element> body = rootElt.elementIterator("Body");
            while (body.hasNext()) {
                Element bodyNext =  body.next();
                Iterator<Element> response = bodyNext.elementIterator("Z_PPFM0004.Response");
                while (response.hasNext()) {
                    Element responseNext = response.next();
                    Iterator<Element> tMARA = responseNext.elementIterator("T_MARA");
                    while (tMARA.hasNext()) {
                        Element tMARANext = tMARA.next();
                        Iterator<Element> item = tMARANext.elementIterator("item");
                        while (item.hasNext()) {
                            Element itemNext = item.next();
                            String drawingNo = itemNext.elementTextTrim("ZEINR");
                            if (!StringUtils.isEmpty(drawingNo)){
                                Product product = new Product();
                                product.setMaterialNo(trimStringWith(itemNext.elementTextTrim("MATNR"),zero));
                                String name = itemNext.elementTextTrim("MAKTX");
                                String[] data = name.split("\\s+");
                                if(data.length>3){
                                    product.setProductName(data[1] +" " + data[2]);
                                }else {
                                    product.setProductName(data[1]);
                                }
                                if (data[data.length-1].matches("[a-zA-Z]+")|| "/".equals(data[data.length-1])){
                                    MaterialTypeDto type = materialType().get(data[data.length - 1]);
                                    product.setMaterialType(type.getNewCode());
                                    product.setMaterialTypeName(type.getDesc());
                                }
                                product.setMaterialDesc(name);
                                product.setDrawingNo(drawingNo);
                                product.setUnit(itemNext.elementTextTrim("ZYL1"));
                                String branchCode = maps.get(itemNext.elementTextTrim("WERKS")).getLabel();
                                product.setBranchCode(branchCode);
                                list.add(product);
                            }
                        }
                    }
                }
            }
            }catch (DocumentException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 功能描述:字符串截取
     * @Author: xinYu.hou
     * @Date: 2022/1/13 9:31
     * @param str
     * @param beTrim
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
        return st > 0 ? str.substring(st, len) : str;
    }

    public static Map<String, MaterialTypeDto> materialType(){
        Map<String, MaterialTypeDto> map = new HashMap<>(4);
        map.put("Z",new MaterialTypeDto("Z","0","铸件"));
        map.put("D",new MaterialTypeDto("D","1","锻件"));
        map.put("JZ",new MaterialTypeDto("JZ","2","精铸件"));
        map.put("/",new MaterialTypeDto("/","3","成品/半成品"));
        return map;
    }

    /**
     * 功能描述: 同步选中物料数据
     * @Author: xinYu.hou
     * @Date: 2022/2/10 16:00
     * @param productList
     * @return: CommonResult<Boolean>
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveProductSync(List<Product> productList) {
        boolean data = false;
        String message = "操作失败";
        for (Product product : productList) {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("material_no",product.getMaterialNo());
            boolean remove = materialSyncService.remove(queryWrapper);
            boolean save = materialSyncService.save(product);
            if (remove && save){
                data = true;
                message = "操作成功!";
            }
        }
        return CommonResult.success(data,message);
    }

    /**
     * 功能描述: 定时同步物料数据
     * @Author: xinYu.hou
     * @Date: 2022/2/10 16:01
     * @return: CommonResult<Boolean>
     **/
    @Override
//    @Scheduled(cron = "*/5 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveTimingProductSync() {
        boolean data = false;
        //获取所有工厂信息
        MaterialSyncDto materialSyncDto = new MaterialSyncDto();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        materialSyncDto.setDate(format.format(date));
        CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("erpCode", "", SecurityConstants.FROM_INNER);
        for (ItemParam itemParam : listCommonResult.getData()) {
            materialSyncDto.setCode(itemParam.getCode());
            List<Product> productList = materialSyncService.queryProductSync(materialSyncDto);
            for (Product product : productList) {
                product.setCreateBy("System");
                product.setModifyBy("System");
                product.setCreateTime(date);
                product.setModifyTime(date);
                product.setBranchCode(itemParam.getLabel());
                product.setTenantId(itemParam.getTenantId());
                QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("material_no",product.getMaterialNo());
                boolean remove = materialSyncService.remove(queryWrapper);
                boolean save = materialSyncService.save(product);
                if (remove && save){
                    data = true;
                }
            }
        }
        return CommonResult.success(data);
    }
}
