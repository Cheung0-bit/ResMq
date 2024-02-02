package io.github.resmq.core.task;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.template.ResMqTemplate;
import io.github.resmq.core.util.LuaScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <Delay message timing processing>
 *
 * @author zhanglin
 */
public class ResMqDelayMessageTasker {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ScheduledExecutorService timer;
    private final StringRedisTemplate stringRedisTemplate;
    private final ResMqTemplate resMqTemplate;

    public ResMqDelayMessageTasker(StringRedisTemplate stringRedisTemplate, ResMqTemplate resMqTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.resMqTemplate = resMqTemplate;
    }

    public void start() {
        if (timer == null) {
            timer = new ScheduledThreadPoolExecutor(2
                    , new ThreadFactory() {
                private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = defaultFactory.newThread(r);
                    // 设置为守护线程
                    if (!thread.isDaemon()) {
                        thread.setDaemon(true);
                    }
                    thread.setName("ResMq-Delay-Message-" + threadNumber.getAndIncrement());
                    return thread;
                }
            });
        }
        timer.scheduleWithFixedDelay(new DelayMessageTasker(), 1
                , 1, TimeUnit.SECONDS);
    }

    public void stop() {
        if (timer != null) {
            timer.shutdown();
        }
    }

    class DelayMessageTasker extends TimerTask {
        @Override
        public void run() {
            try {
                Set<String> delayMessageKeys = LuaScriptUtil.delayMessageKeys(stringRedisTemplate);
                for (String key : delayMessageKeys) {
                    double now = System.currentTimeMillis() / 1000.0;
                    Set<String> rangeByScore = stringRedisTemplate.opsForZSet().rangeByScore(key, 1, now);
                    for (String s : rangeByScore) {
                        boolean isSuccess = resMqTemplate.syncSend(key.substring(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY.length()), s);
                        if (isSuccess) {
                            stringRedisTemplate.opsForZSet().remove(key, s);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
