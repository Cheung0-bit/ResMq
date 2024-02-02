package io.github.resmq.core.util;

import io.github.resmq.core.constant.Constants;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.connection.stream.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <Lua util>
 *
 * @author zhanglin
 */
@SuppressWarnings("rawtypes")
public class LuaScriptUtil {

    /**
     * Obtaining groups of consumers
     * xinfo GROUPS key
     */
    public static final RedisScript<List> SCRIPT_GROUP = new DefaultRedisScript<>("return redis.call('xinfo', 'GROUPS', KEYS[1])", List.class);
    /**
     * Create a consumer group MKSTREAM to solve the problem that topic doesn't exist
     * xgroup CREATE key groupname id-or-$ MKSTREAM
     */
    public static final RedisScript<String> SCRIPT_CREATE_GROUP = new DefaultRedisScript<>("return redis.call('xgroup', 'create', KEYS[1], ARGV[1], ARGV[2], 'MKSTREAM')", String.class);
    /**
     * Gets a summary of unACK messages for the consumer group
     * xpending key group
     */
    public static final RedisScript<List> SCRIPT_PENDING_GROUP = new DefaultRedisScript<>("local msg=redis.call('xpending', KEYS[1], ARGV[1]) return msg", List.class);
    /**
     * Gets a summary of unACK messages from consumers in a consumer group
     * xpending key group start end count consumer
     */
    public static final RedisScript<List> SCRIPT_PENDING_GROUP_CONSUMER = new DefaultRedisScript<>("local msg=redis.call('xpending', KEYS[1], ARGV[1], ARGV[2], ARGV[3], ARGV[4], ARGV[5]) return msg", List.class);

    /**
     * Get the consumer group for the topic
     *
     * @param topic
     * @param stringRedisTemplate
     * @return
     */
    @SuppressWarnings("unchecked")
    public static StreamInfo.XInfoGroups getInfoGroups(String topic, StringRedisTemplate stringRedisTemplate) {
        List<Object> parts = null;
        try {
            parts = stringRedisTemplate.execute(SCRIPT_GROUP, Collections.singletonList(topic));
        } catch (Exception e) {
            return null;
        }
        return StreamInfo.XInfoGroups.fromList(parts);
    }

    /**
     * Creating a Consumer group
     *
     * @param topic
     * @param readOffset
     * @param group
     * @return
     */
    public static boolean createGroup(String topic, ReadOffset readOffset, String group, StringRedisTemplate stringRedisTemplate) {
        String flag = stringRedisTemplate.execute(SCRIPT_CREATE_GROUP, Collections.singletonList(topic),
                group, readOffset.getOffset());
        return Objects.equals(Constants.EXECUTE_SUCCESS, flag);
    }

    /**
     * Gets a summary of unACK messages for the consumer group
     *
     * @param topic topic
     * @param group group
     * @return
     */
    @SuppressWarnings("unchecked")
    public static PendingMessagesSummary getPendingMessagesSummary(String topic, String group, StringRedisTemplate stringRedisTemplate) {
        List<?> parts = stringRedisTemplate.execute(SCRIPT_PENDING_GROUP, Collections.singletonList(topic), group);
        if (parts.isEmpty()) {
            return null;
        }
        PendingMessagesSummary pendingMessagesSummary = null;
        List<List<String>> customerParts = (List<List<String>>) parts.get(3);
        if (customerParts.isEmpty()) {
            pendingMessagesSummary = new PendingMessagesSummary(group, 0, Range.unbounded(), Collections.emptyMap());
        } else {
            // 多个消费者处理
            Map<String, Long> map = customerParts.stream().collect(Collectors.toMap(e -> e.get(0), e -> Long.valueOf(e.get(1)),
                    (u, v) -> {
                        throw new IllegalStateException("Duplicate key: " + u);
                    },
                    HashMap::new));
            Range<String> range = Range.open(parts.get(1).toString(), parts.get(2).toString());
            pendingMessagesSummary = new PendingMessagesSummary(group, (Long) parts.get(0), range, map);
        }
        return pendingMessagesSummary;
    }

    /**
     * Gets a summary of unACK messages from consumers in a consumer group
     *
     * @param topic    topic
     * @param group    group
     * @param consumer consumer
     * @param range    range
     * @param count    count
     * @return
     */
    public static PendingMessages getPendingMessages(String topic, String group, String consumer, Range<?> range, long count, StringRedisTemplate stringRedisTemplate) {
        Optional<?> lowerOptional = range.getLowerBound().getValue();
        Optional<?> upperOptional = range.getUpperBound().getValue();

        Object lowerValue = null;
        if (lowerOptional.isPresent()) {
            lowerValue = lowerOptional.get();
        }
        Object upperValue = null;
        if (upperOptional.isPresent()) {
            upperValue = upperOptional.get();
        }
        List<?> parts = stringRedisTemplate.execute(
                SCRIPT_PENDING_GROUP_CONSUMER,
                Collections.singletonList(topic),
                group, lowerValue, upperValue, String.valueOf(count), consumer
        );
        int size = parts.size();
        List<PendingMessage> pms = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            List params = (List) parts.get(i);
            PendingMessage pm = new PendingMessage(RecordId.of(params.get(0).toString()),
                    Consumer.from(group, params.get(1).toString()),
                    Duration.of(Long.parseLong(params.get(2).toString()), ChronoUnit.MILLIS),
                    Long.parseLong(params.get(3).toString()));
            pms.add(pm);
        }
        return new PendingMessages(group, range, pms);
    }

    public static Set<String> delayMessageKeys(StringRedisTemplate stringRedisTemplate) {
        return stringRedisTemplate.keys(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY + "*");
    }

}
