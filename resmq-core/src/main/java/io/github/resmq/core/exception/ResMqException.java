package io.github.resmq.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <Build custom exceptions>
 *
 * @author zhanglin
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
