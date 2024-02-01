package io.github.resmq.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <组建自定义异常>
 *
 * @Author zhanglin
 * @createTime 2024/1/29 16:13
 */
public class ResMqException extends RuntimeException {

    Logger logger = LoggerFactory.getLogger(getClass());

    public ResMqException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        logger.warn("ResMqException");
        return super.getMessage();
    }
}
