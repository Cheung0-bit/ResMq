package io.github.resmq.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <消费者工具类>
 *
 * @Author zhanglin
 * @createTime 2024/1/30 19:22
 */
public class ConsumerUtil {

    /**
     * 取得Host的信息
     *
     * @return
     */
    public static String getHostAddress() throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        if (null != localhost) {
            return localhost.getHostAddress();
        }
        return null;
    }

    /**
     * 获取当前进程 PID
     *
     * @return 当前进程 ID
     */
    public static long getCurrentPid() {
        return Long.parseLong(getRuntimeMxBean().getName().split("@")[0]);
    }

    /**
     * 返回Java虚拟机运行时系统相关属性
     *
     * @return {@link RuntimeMXBean}
     */
    public static RuntimeMXBean getRuntimeMxBean() {
        return ManagementFactory.getRuntimeMXBean();
    }

}
