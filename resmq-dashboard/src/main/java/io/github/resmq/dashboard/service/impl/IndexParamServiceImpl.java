package io.github.resmq.dashboard.service.impl;

import com.alibaba.fastjson.JSON;
import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.model.ReturnT;
import io.github.resmq.dashboard.service.IndexParamService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

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

    @Override
    public Map<String, Integer> getCount(long now) {
        Map<String, Integer> map = new HashMap<>();
        Integer topicCount = Optional.ofNullable(stringRedisTemplate.keys(Constants.GROUP_COUNT_KEY + "*"))
                .map(Set::size)
                .orElse(0);
        map.put("topicCount", topicCount);
        String key = Constants.MESSAGE_COUNT_KEY + now + "*";
        Set<String> countKeys = stringRedisTemplate.keys(key);
        if (countKeys == null || countKeys.isEmpty()) {
            map.put("totalCount", 0);
            map.put("dlqCount", 0);
            map.put("ackCount", 0);
        } else {
            int totalCount = 0, dlqCount = 0, ackCount = 0;
            for (String countKey : countKeys) {
                Integer totalCountValue = Optional.ofNullable(stringRedisTemplate.opsForHash().get(countKey, "total-count"))
                        .map(value -> Integer.parseInt(value.toString()))
                        .orElse(0);
                totalCount += totalCountValue;

                Integer ackCountValue = Optional.ofNullable(stringRedisTemplate.opsForHash().get(countKey, "ack-count"))
                        .map(value -> Integer.parseInt(value.toString()))
                        .orElse(0);
                ackCount += ackCountValue;

                Integer dlqCountValue = Optional.ofNullable(stringRedisTemplate.opsForHash().get(countKey, "dlq-count"))
                        .map(value -> Integer.parseInt(value.toString()))
                        .orElse(0);
                dlqCount += dlqCountValue;
            }
            map.put("totalCount", totalCount);
            map.put("dlqCount", dlqCount);
            map.put("ackCount", ackCount);
        }
        Set<String> delayKeys = stringRedisTemplate.keys(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY + "*");
        if (delayKeys == null || delayKeys.isEmpty()) {
            map.put("delayCount", 0);
        } else {
            int delayCount = 0;
            for (String delayKey : delayKeys) {
                delayCount += Optional.ofNullable(stringRedisTemplate.opsForZSet().zCard(delayKey))
                        .map(Long::intValue)
                        .orElse(0) - 1;
            }
            map.put("delayCount", delayCount);
        }
        return map;
    }

    @Override
    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        // process
        List<String> messageDay_list = new ArrayList<>();
        List<Integer> newNum_list = new ArrayList<>();
        List<Integer> successNum_list = new ArrayList<>();
        List<Integer> failNum_list = new ArrayList<>();
        List<Integer> unConsume_list = new ArrayList<>();

        // 创建一个 SimpleDateFormat 对象，指定日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // 获取 Calendar 对象，并设置为开始日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        // 循环遍历日期，并格式化为指定形式
        while (calendar.getTime().compareTo(endDate) <= 0) {
            Date currentDate = calendar.getTime();
            String messageDay = dateFormat.format(currentDate);
            messageDay_list.add(messageDay);
            // 增加一天
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        int newNum_total = 0;
        int successNum_total = 0;
        int failNum_total = 0;
        int unConsume_total = 0;

        long start = startDate.getTime() / (24 * 60 * 60 * 1000) + 1;
        long end = endDate.getTime() / (24 * 60 * 60 * 1000) + 1;

        for (long i = start; i <= end; i++) {
            Map<String, Integer> map = this.getCount(i);
            int newNum = map.get("totalCount");
            int failNum = map.get("dlqCount");
            int successNum = map.get("ackCount");
            int unConsumeNum = newNum - failNum - successNum;

            newNum_list.add(newNum);
            successNum_list.add(successNum);
            failNum_list.add(failNum);
            unConsume_list.add(unConsumeNum);

            newNum_total += newNum;
            successNum_total += successNum;
            failNum_total += failNum;
            unConsume_total += unConsumeNum;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("messageDay_list", messageDay_list);
        result.put("newNum_list", newNum_list);
        result.put("unConsume_list", unConsume_list);
        result.put("successNum_list", successNum_list);
        result.put("failNum_list", failNum_list);

        result.put("newNum_total", newNum_total);
        result.put("successNum_total", successNum_total);
        result.put("failNum_total", failNum_total);
        result.put("unConsume_total", unConsume_total);

        return new ReturnT<>(result);
    }
}
