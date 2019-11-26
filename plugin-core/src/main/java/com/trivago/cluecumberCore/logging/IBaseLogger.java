package com.trivago.cluecumberCore.logging;

public interface IBaseLogger {
    void info(String msg);
    void warn(String msg);
    void error(String msg);
}
