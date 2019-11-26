package com.trivago.cluecumber.logging;

import com.trivago.cluecumberCore.logging.BaseLogger;
import com.trivago.cluecumberCore.logging.IBaseLogger;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CluecumberLoggerTest {

    private IBaseLogger mockedLogger;
    private BaseLogger logger;

    @Before
    public void setup() {
        mockedLogger = mock(IBaseLogger.class);
        logger = new BaseLogger();
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
