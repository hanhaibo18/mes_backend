package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.PdmBomMapper;
import com.richfit.mes.common.model.base.PdmBom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 11:23
 */
@Service
public class PdmBomServiceImpl extends ServiceImpl<PdmBomMapper, PdmBom> implements PdmBomService {

    @Autowired
    private PdmBomMapper pdmBomMapper;

    @Override
    public PdmBom getBomByProcessIdAndRev(String id, String ver) {
        QueryWrapper<PdmBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                .eq("ver", ver)
                .eq("p_id", 0)
                .orderByDesc("order_no+1");
        List<PdmBom> list = this.list(queryWrapper);
        if (list.size() > 0) {
            PdmBom pdmBom = new PdmBom();
            pdmBom = list.get(0);
            String pid = pdmBom.getId() + '@' + pdmBom.getVer();
            QueryWrapper<PdmBom> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("p_id", pid);
            List<PdmBom> childBom = this.list(queryWrapper2);
            //递归往下找
            getChildBom(childBom);
            pdmBom.setChildBom(childBom);
            return pdmBom;
        } else {
            return null;
        }
    }

    @Override
    public List<PdmBom> getBomByProcessIdAndRevTree(String id, String ver) {
        String pid = id + "@" + ver;
        QueryWrapper<PdmBom> bomQueryWrapper = new QueryWrapper<>();
        bomQueryWrapper.eq("p_id",pid);
        List<PdmBom> childBom = this.list();
        return getChildBomTree(childBom,childBom);
    }

    //递归函数
    public void getChildBom(List<PdmBom> pdmBoms) {
        if (pdmBoms.size() > 0) {
            for (PdmBom pdmBom : pdmBoms) {
                String pid = pdmBom.getId() + '@' + pdmBom.getVer();
                QueryWrapper<PdmBom> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("p_id", pid);
                List<PdmBom> childBom = this.list(queryWrapper);
                getChildBom(childBom);
                pdmBom.setChildBom(childBom);
            }
        }
    }

    //递归函数
    public List<PdmBom> getChildBomTree(List<PdmBom> pdmBoms,List<PdmBom> pdmBomsShow) {
        if (pdmBoms.size() > 0) {
            for (PdmBom pdmBom : pdmBoms) {
                String pid = pdmBom.getId() + '@' + pdmBom.getVer();
                QueryWrapper<PdmBom> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("p_id", pid);
                List<PdmBom> childBom = this.list(queryWrapper);
                if (!childBom.isEmpty()){
                    pdmBom.setIsLeafNodes(true);
                    for (PdmBom bom : childBom) {
                        bom.setFid(pdmBom.getId());
                    }
                    pdmBomsShow.addAll(childBom);
                }
                pdmBomsShow = getChildBomTree(childBom,pdmBomsShow);
            }
        }
        return pdmBomsShow;
    }
}
