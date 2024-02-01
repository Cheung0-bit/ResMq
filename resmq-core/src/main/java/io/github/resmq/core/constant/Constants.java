package io.github.resmq.core.constant;

/**
 * <常量>
 *
 * @Author zhanglin
 * @createTime 2024/1/29 14:28
 */
public class Constants {
    /** Lua执行成功标志 */
    public static final String EXECUTE_SUCCESS = "OK";
    /**
     * 配置文件前缀
     */
    public static final String CONFIGURATION_KEY = "_resmq:properties";
    /** RedisTemplate 消息队列主题统一前缀 */
    public static final String TOPIC_PREFIX = "io:github:resmq:";

    /** RedisTemplate 延迟消息队列TTL key的前缀 */
    public static final String DELAY_MESSAGE_TTL_PREFIX_KEY = "DM:TTL:";

}