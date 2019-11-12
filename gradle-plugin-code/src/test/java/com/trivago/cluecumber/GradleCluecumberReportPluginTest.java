package com.trivago.cluecumber;

import com.trivago.cluecumber.logging.GradleCluecumberLogger;
import com.trivago.cluecumberCore.exceptions.CluecumberPluginException;
import com.trivago.cluecumberCore.filesystem.FileIO;
import com.trivago.cluecumberCore.filesystem.FileSystemManager;
import com.trivago.cluecumberCore.json.JsonPojoConverter;
import com.trivago.cluecumberCore.json.processors.ElementIndexPreProcessor;
import com.trivago.cluecumberCore.properties.PropertyManager;
import com.trivago.cluecumberCore.rendering.ReportGenerator;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CluecumberReportPluginTest {

    private CluecumberReportGradleTask cluecumberReportPlugin;
    private JsonPojoConverter jsonPojoConverter;
    private FileIO fileIO;

    @Before
    public void setup() throws CluecumberPluginException {
        GradleCluecumberLogger cluecumberLogger = mock(GradleCluecumberLogger.class);
        PropertyManager propertyManager = mock(PropertyManager.class);

        FileSystemManager fileSystemManager = mock(FileSystemManager.class);
        List<Path> fileList = new ArrayList<>();
        Path path = mock(Path.class);
        fileList.add(path);
        when(fileSystemManager.getJsonFilePaths(anyString())).thenReturn(fileList);

        fileIO = mock(FileIO.class);
        jsonPojoConverter = mock(JsonPojoConverter.class);
        ElementIndexPreProcessor elementIndexPostProcessor = mock(ElementIndexPreProcessor.class);
        ReportGenerator reportGenerator = mock(ReportGenerator.class);

        cluecumberReportPlugin = new CluecumberReportGradleTask();
    }

    @Test
    public void executeTest() throws CluecumberPluginException {
        cluecumberReportPlugin.resolve();
    }

    @Test
    public void noErrorOnUnparsableJsonTest() throws CluecumberPluginException {
        when(fileIO.readContentFromFile(any())).thenReturn("json");
        long endTime = 0;
        when(jsonPojoConverter.convertJsonToReportPojos("json")).thenThrow(new CluecumberPluginException("failure"));
        cluecumberReportPlugin.resolve();
    }
}
