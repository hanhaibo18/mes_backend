package com.bsjx.mes.pdm.service;

import com.bsjx.mes.pdm.entity.PdmMesMap;
import com.bsjx.mes.pdm.repository.PdmMesMapRepository;
import com.bsjx.mes.pdm.repository.DrawingApplyRepository;
import com.bsjx.mes.pdm.xml.map.PlmRowData;
import com.bsjx.mes.pdm.xml.map.PlmXml;
import com.bsjx.mes.pdm.xml.map.ROW;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PdmMapServiceImpl implements PdmMapService {

    @Autowired
    private PdmMesMapRepository pdmMesMapRepository;

    public void tmp(String filePath) {
        try {
            String xml = getXml(filePath);
            PlmXml plmXml;
            //返回值转换对象异常
            plmXml = (PlmXml) xmlStrToObj(PlmXml.class, xml);
            log.info("SIZE{}", plmXml.getProduct().size());

            List<PdmMesMap> pdmMesMapList = plmXml.getProduct().stream().map(p -> {
                PdmMesMap pdmMesMap = new PdmMesMap();
                pdmMesMap.setId(p.getId());
                pdmMesMap.setPdmName(p.getName());
                pdmMesMap.setPdmDrawNo(p.getProductId());
                String filterDrawNo = p.getProductId().replaceAll("[^(A-Za-z0-9)]", "");
                pdmMesMap.setFilterDrawNo(filterDrawNo);
                return pdmMesMap;
            }).collect(Collectors.toList());

            //pdmMesMapList = pdmMesMapList.parallelStream().distinct().collect(Collectors.toList());
            //pdmMesMapRepository.saveAll(pdmMesMapList);
            log.info("-----------------------------------------{}", pdmMesMapList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void pdmMesMap(String filePath) {
        try {
            String xml = getXml(filePath);
            PlmRowData plmRowData;
            //返回值转换对象异常
            plmRowData = (PlmRowData) xmlStrToObj(PlmRowData.class, xml);
            log.info("PlmRowData-----------------------------------------{}", plmRowData.getROW().size());

            List<String> list2 = plmRowData.getROW().stream().map(ROW::getPITEMID).collect(Collectors.toList());
            list2 = list2.parallelStream().distinct().collect(Collectors.toList());

            log.info("list2-----------------------------------------{}", list2.size());
            List<String> list = list2.stream().map(p -> p.replaceAll("[^(A-Za-z0-9)]", "")).collect(Collectors.toList());
            log.info("list-----------------------------------------{}", list.size());

            List<String> result = new ArrayList<>();
            Map<String, Long> collect = list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            collect.forEach((k, v) -> {
                if (v > 1){
                    result.add(k);
                }
            });
            log.info(result.toString());
            log.info("list-----------------------------------------{}", result.size());
            list = list.parallelStream().distinct().collect(Collectors.toList());
            log.info("list-----------------------------------------{}", list.size());

            List<PdmMesMap> pdmMesMapList = plmRowData.getROW().stream().map(p -> {
                PdmMesMap pdmMesMap = new PdmMesMap();
                pdmMesMap.setId(p.getC0());
                pdmMesMap.setPdmDrawNo(p.getPITEMID());
                pdmMesMap.setPdmName(p.getPNAME());
                String filterDrawNo = p.getPITEMID().replaceAll("[^(A-Za-z0-9)]", "");
                if(result.contains(filterDrawNo)){
                    log.info("{}@{}",p.getC0(),p.getPITEMID());
                }
                pdmMesMap.setFilterDrawNo(filterDrawNo);
                return pdmMesMap;
            }).collect(Collectors.toList());
            log.info("pdmMesMapList-----------------------------------------{}", pdmMesMapList.size());
            pdmMesMapList = pdmMesMapList.parallelStream().distinct().collect(Collectors.toList());

            log.info("pdmMesMapList-----------------------------------------{}", pdmMesMapList.size());
            pdmMesMapRepository.saveAll(pdmMesMapList);
            log.info("-----------------------------------------{}", pdmMesMapList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Object xmlStrToObj(Class<?> clazz, String xmlStr) throws Exception {
        Object xmlObject;
        Reader reader;
        JAXBContext context = JAXBContext.newInstance(clazz);

        // XML 转为对象的接口
        Unmarshaller unmarshaller = context.createUnmarshaller();

        reader = new StringReader(xmlStr);
        xmlObject = unmarshaller.unmarshal(reader);

        reader.close();

        return xmlObject;
    }

    private String getXml(String filePath) throws IOException {
        // 读取XML文件
        Resource resource = new ClassPathResource(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            builder.append(line);
        }

        br.close();
        return builder.toString();
    }

}
