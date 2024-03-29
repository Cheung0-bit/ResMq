package io.github.resmq.dashboard.service;

import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.dashboard.model.ReturnT;

import java.util.Date;
import java.util.Map;

/**
 * <主页表格系统参数服务>
 *
 * @author zhanglin
 * @createTime 2024/1/31 20:29
 */
public interface IndexParamService {

    void getClusterNodes();

    ResMqProperties getResMqProperties();

    Map<String, Integer> getCount(long now);

    ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate);
}
