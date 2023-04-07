package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.router.Zc80Ppif026Response;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 向ERP推送工艺
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
 */
public interface RouterService {

    public Zc80Ppif026Response push(List<Router> routers) throws Exception;

}
