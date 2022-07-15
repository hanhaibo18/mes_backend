package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmMesBomMapper;
import com.richfit.mes.common.model.base.PdmMesBom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmMesBomServiceImpl extends ServiceImpl<PdmMesBomMapper, PdmMesBom> implements PdmMesBomService {

    @Autowired
    private PdmMesBomMapper pdmMesBomMapper;

    @Override
    public PdmMesBom getBomByProcessIdAndRev(String id, String ver) {

        QueryWrapper<PdmMesBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                .eq("ver", ver)
                .eq("p_id", 0)
                .orderByDesc("order_no+1");
        List<PdmMesBom> list = this.list(queryWrapper);

        if (list.size() > 0) {
            PdmMesBom pdmBom = new PdmMesBom();
            pdmBom = list.get(0);
            String pid = pdmBom.getId() + '@' + pdmBom.getVer();
            QueryWrapper<PdmMesBom> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("p_id", pid);
            List<PdmMesBom> childBom = this.list(queryWrapper2);
            //递归往下找
            getChildBom(childBom);
            pdmBom.setChildBom(childBom);
            return pdmBom;
        } else {
            return null;
        }
    }

    //递归函数
    public void getChildBom(List<PdmMesBom> pdmBoms) {
        if (pdmBoms.size() > 0) {
            for (PdmMesBom pdmBom : pdmBoms) {
                String pid = pdmBom.getId() + '@' + pdmBom.getVer();
                QueryWrapper<PdmMesBom> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("p_id", pid);
                List<PdmMesBom> childBom = this.list(queryWrapper);
                getChildBom(childBom);
                pdmBom.setChildBom(childBom);
            }
        }
    }
}
