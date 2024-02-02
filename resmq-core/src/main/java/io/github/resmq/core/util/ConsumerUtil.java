package io.github.resmq.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <consumer util>
 *
 * @author zhanglin
 */
public class ConsumerUtil {

    /**
     * Get information about the Host
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
     * Getting PID of the current process
     *
     * @return PID
     */
    public static long getCurrentPid() {
        return Long.parseLong(getRuntimeMxBean().getName().split("@")[0]);
    }

    /**
     * Returns system-specific properties when the Java virtual Machine is running
     *
     * @return {@link RuntimeMXBean}
     */
    public static RuntimeMXBean getRuntimeMxBean() {
        return ManagementFactory.getRuntimeMXBean();
    }

}
