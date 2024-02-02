package io.github.resmq.dashboard.service;

import io.github.resmq.core.config.ResMqProperties;

/**
 * <主页表格系统参数服务>
 *
 * @author zhanglin
 * @createTime 2024/1/31 20:29
 */
public interface IndexParamService {

    void getClusterNodes();

    ResMqProperties getResMqProperties();

}
