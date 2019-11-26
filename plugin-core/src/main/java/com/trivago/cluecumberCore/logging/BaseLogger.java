package com.trivago.cluecumberCore.logging;

import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class BaseLogger {

    private IBaseLogger logger;
    private CluecumberLogLevel currentLogLevel;
    /**
     //     * Set the mojo logger so it can be used in any class that injects a CluecumberLogger.
     //     *
     //     * @param logger
     //     * @param currentLogLevel the log level that the logger should react to.
     //     */
    public void initialize(final IBaseLogger logger, final String currentLogLevel) {
        this.logger = logger;
        if (currentLogLevel == null) {
            this.currentLogLevel = CluecumberLogLevel.DEFAULT;
            return;
        }

        try {
            this.currentLogLevel = CluecumberLogLevel.valueOf(currentLogLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.currentLogLevel = CluecumberLogLevel.DEFAULT;
            warn("Log level " + currentLogLevel + " is unknown. Cluecumber will use 'default' logging.");
        }
    }

    public void logInfoSeparator(final CluecumberLogLevel... cluecumberLogLevels) {
        info("------------------------------------------------------------------------", cluecumberLogLevels);
    }

    /**
     * Info logging based on the provided Cluecumber log levels.
     *
     * @param logString           The {@link String} to be logged.
     * @param cluecumberLogLevels The log levels ({@link CluecumberLogLevel} list) in which the message should be displayed.
     */
    public void info(final String logString, CluecumberLogLevel... cluecumberLogLevels) {
        log(LogLevel.INFO, logString, cluecumberLogLevels);
    }

    /**
     * Warn logging. This is always displayed unless logging is off.
     *
     * @param logString The {@link String} to be logged.
     */
    public void warn(final String logString) {
        CluecumberLogLevel[] logLevels =
                new CluecumberLogLevel[]{CluecumberLogLevel.DEFAULT, CluecumberLogLevel.COMPACT, CluecumberLogLevel.MINIMAL};
        log(LogLevel.WARN, logString, logLevels);
    }

    /**
     //     * Logs a message based on the provided log levels.
     //     *
     //     * @param logString           The {@link String} to be logged.
     //     * @param CluecumberLogLevels The log levels in which the message should be displayed.
     //     */
    protected void log(final LogLevel logLevel, final String logString, CluecumberLogLevel... CluecumberLogLevels) {
        if (currentLogLevel == CluecumberLogLevel.OFF) {
            return;
        }

        if (currentLogLevel != null
                && CluecumberLogLevels != null
                && CluecumberLogLevels.length > 0
                && Arrays.stream(CluecumberLogLevels)
                .noneMatch(CluecumberLogLevel -> CluecumberLogLevel == currentLogLevel)) {
            return;
        }
        switch (logLevel) {
            case INFO:
                logger.info(logString);
                break;
            case WARN:
                logger.warn(logString);
                break;
        }
    }

    public enum CluecumberLogLevel {
        DEFAULT, COMPACT, MINIMAL, OFF
    }

    public enum LogLevel {
        INFO, WARN
    }
}
