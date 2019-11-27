package com.trivago.cluecumber.logging;

import com.trivago.cluecumberCore.logging.LoggerUtils;
import com.trivago.cluecumberCore.logging.ICluecumberLogger;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CluecumberLoggerTest {

    private ICluecumberLogger mockedLogger;
    private LoggerUtils logger;

    @Before
    public void setup() {
        mockedLogger = mock(ICluecumberLogger.class);
        logger = new LoggerUtils();
        logger.initialize(mockedLogger, null);
    }

    @Test
    public void infoTest() {
        logger.info("Test");
        verify(mockedLogger, times(1))
                .info("Test");
    }

    @Test
    public void warnTest() {
        logger.warn("Test");
        verify(mockedLogger, times(1))
                .warn("Test");
    }

    @Test
    public void separatorTest() {
        logger.logInfoSeparator();
        verify(mockedLogger, times(1))
                .info("------------------------------------------------------------------------");
    }
}
