package com.richfit.mes.produce.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.DevicePerson;
import com.richfit.mes.common.model.produce.HourStandard;
import com.richfit.mes.common.model.produce.RgDevice;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.produce.dao.HourStandardMapper;
import com.richfit.mes.produce.dao.RgDeviceMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author renzewen
 * @since 2022-12-23
 */
@Service
public class RgDeviceServiceImpl extends ServiceImpl<RgDeviceMapper, RgDevice> implements RgDeviceService {

}
