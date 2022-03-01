package com.richfit.mes.produce.service;
import cn.hutool.core.util.ObjectUtil;
import com.richfit.mes.produce.dao.SjtjMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class SjtjServiceImpl implements SjtjService {
    @Autowired
    private SjtjMapper sjtjMapper;
    @Override
    public List<Map> query1(String branchCode) {
        List<Map> list = new ArrayList<>();
        List<Map> maps = sjtjMapper.query1(branchCode);
        List<Map> maps2 = sjtjMapper.query2();
        List<Map> maps3 = sjtjMapper.query3();
        List<Map> maps4 = sjtjMapper.query4();
        list.addAll(maps);
        extracted(list, maps2,"qty");
        extracted(list, maps3,"completed_qty");
        extracted(list, maps4,"qualify");
        return list;
    }

    private void extracted(List<Map> list, List<Map> maps2,String flag) {
        for (Map map : maps2) {
            if(ObjectUtil.isEmpty(map.get("template_code"))){
                map.put("template_code","$");
            }
            for (Map listMap : list) {
                if(ObjectUtil.isEmpty(listMap.get("template_code"))){
                    listMap.put("template_code","$");
                }
                if(map.get("template_code").equals(listMap.get("template_code"))){
                    listMap.put(flag,map.get(flag));
                }
            }
        }
    }
}
