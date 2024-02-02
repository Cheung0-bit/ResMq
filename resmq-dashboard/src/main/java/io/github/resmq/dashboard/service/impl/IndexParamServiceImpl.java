package io.github.resmq.dashboard.service.impl;

import com.alibaba.fastjson.JSON;
import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.service.IndexParamService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/1/31 20:41
 */
@Service
public class IndexParamServiceImpl implements IndexParamService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void getClusterNodes() {

    }

    @Override
    public ResMqProperties getResMqProperties() {
        String jsonStr = stringRedisTemplate.opsForValue().get(Constants.CONFIGURATION_KEY);
        return JSON.parseObject(jsonStr, ResMqProperties.class);
    }
}
