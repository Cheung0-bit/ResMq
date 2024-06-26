package io.github.resmq.core.constant;

/**
 * <Constant>
 *
 * @author zhanglin
 */
public class Constants {
    /**
     * Lua execution success flag
     */
    public static final String EXECUTE_SUCCESS = "OK";
    /**
     * Configuration file prefix
     */
    public static final String CONFIGURATION_KEY = "_resmq:properties";
    /**
     * Group Count Key
     */
    public static final String GROUP_COUNT_KEY = "_resmq:group-count:";
    /**
     * Message Count Key
     */
    public static final String MESSAGE_COUNT_KEY = "_resmq:message-count:";
    /**
     * RedisTemplate Message queue topic uniform prefix
     */
    public static final String TOPIC_PREFIX = "io:github:resmq:";

    /**
     * RedisTemplate Delays the prefix of the message queue TTL key
     */
    public static final String DELAY_MESSAGE_TTL_PREFIX_KEY = "DM:TTL:";

}