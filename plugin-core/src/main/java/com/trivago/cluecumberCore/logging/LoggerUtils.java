package com.trivago.cluecumberCore.logging;

import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class LoggerUtils {

    private static ICluecumberLogger sLogger;
    private static CluecumberLogLevel sCurrentLogLevel;


    /**
     //     * Set the mojo logger so it can be used in any class that injects a CluecumberLogger.
     //     *
     //     * @param logger
     //     * @param currentLogLevel the log level that the logger should react to.
     //     */
    public static void initialize(ICluecumberLogger logger, final String currentLogLevel) {
        sLogger = logger;
        if (currentLogLevel == null) {
            sCurrentLogLevel = CluecumberLogLevel.DEFAULT;
            return;
        }

        try {
            sCurrentLogLevel = CluecumberLogLevel.valueOf(currentLogLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            sCurrentLogLevel = CluecumberLogLevel.DEFAULT;
            logger.warn("Log level " + currentLogLevel + " is unknown. Cluecumber will use 'default' logging.");
        }
    }

    public static void logInfoSeparator(final CluecumberLogLevel... cluecumberLogLevels) {
        info("------------------------------------------------------------------------", cluecumberLogLevels);
    }

    /**
     * Info logging based on the provided Cluecumber log levels.
     *
     * @param logString           The {@link String} to be logged.
     * @param cluecumberLogLevels The log levels ({@link CluecumberLogLevel} list) in which the message should be displayed.
     */
    public static void info(final String logString, CluecumberLogLevel... cluecumberLogLevels) {
        log(LogLevel.INFO, logString, cluecumberLogLevels);
    }

    /**
     * Warn logging. This is always displayed unless logging is off.
     *
     * @param logString The {@link String} to be logged.
     */
    public static void warn(final String logString) {
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
    protected static void log(final LogLevel logLevel, final String logString, CluecumberLogLevel... CluecumberLogLevels) {
        if (sCurrentLogLevel == CluecumberLogLevel.OFF) {
            return;
        }

        if (sCurrentLogLevel != null
                && CluecumberLogLevels != null
                && CluecumberLogLevels.length > 0
                && Arrays.stream(CluecumberLogLevels)
                .noneMatch(CluecumberLogLevel -> CluecumberLogLevel == sCurrentLogLevel)) {
            return;
        }
        switch (logLevel) {
            case INFO:
                sLogger.info(logString);
                break;
            case WARN:
                sLogger.warn(logString);
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
