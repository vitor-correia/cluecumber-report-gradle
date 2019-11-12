package com.trivago.cluecumberCore.logging;

abstract public class BaseLogger {

    public void initialize(final Object logger, final String currentLogLevel) {
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

    protected void log(final LogLevel logLevel, final String logString, CluecumberLogLevel... CluecumberLogLevels) {
    }

    public enum CluecumberLogLevel {
        DEFAULT, COMPACT, MINIMAL, OFF
    }

    public enum LogLevel {
        INFO, WARN
    }
}
