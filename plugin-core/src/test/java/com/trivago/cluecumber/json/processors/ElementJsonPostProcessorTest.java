package com.trivago.cluecumber.json.processors;

import com.trivago.cluecumberCore.constants.MimeType;
import com.trivago.cluecumberCore.filesystem.FileIO;
import com.trivago.cluecumberCore.json.pojo.Element;
import com.trivago.cluecumberCore.json.pojo.Embedding;
import com.trivago.cluecumberCore.json.pojo.ResultMatch;
import com.trivago.cluecumberCore.json.pojo.Step;
import com.trivago.cluecumberCore.json.processors.ElementJsonPostProcessor;
import com.trivago.cluecumberCore.logging.LoggerUtils;
import com.trivago.cluecumberCore.properties.PropertyManager;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class ElementJsonPostProcessorTest {
    private ElementJsonPostProcessor elementJsonPostProcessor;

    @Before
    public void setup() {
        PropertyManager propertyManager = mock(PropertyManager.class);
        FileIO fileIO = mock(FileIO.class);
        LoggerUtils logger = mock(LoggerUtils.class);
        elementJsonPostProcessor = new ElementJsonPostProcessor(propertyManager, fileIO, logger);
    }

    @Test
    public void postDesiralizeAddScenarioIndexBackgroundScenarioTest() {
        Element element = new Element();
        assertThat(element.getScenarioIndex(), is(0));
        elementJsonPostProcessor.postDeserialize(element, null, null);
        assertThat(element.getScenarioIndex(), is(0));
    }

    @Test
    public void postDeserializeTest() {
        Element element = new Element();
        List<Step> steps = new ArrayList<>();
        Step step = new Step();
        List<Embedding> embeddings = new ArrayList<>();
        Embedding embedding = new Embedding();
        embedding.setMimeType(MimeType.PNG);
        embedding.setData("123");
        embeddings.add(embedding);
        step.setEmbeddings(embeddings);

        List<ResultMatch> before = new ArrayList<>();
        ResultMatch resultMatchBefore = new ResultMatch();
        before.add(resultMatchBefore);
        step.setBefore(before);

        List<ResultMatch> after = new ArrayList<>();
        ResultMatch resultMatchAfter = new ResultMatch();
        after.add(resultMatchAfter);
        step.setAfter(after);

        steps.add(step);
        element.setSteps(steps);

        List<ResultMatch> elementAfter = new ArrayList<>();
        ResultMatch resultMatchAfterElement = new ResultMatch();
        elementAfter.add(resultMatchAfter);
        element.setAfter(elementAfter);

        assertThat(embedding.getData(), is("123"));

        elementJsonPostProcessor.postDeserialize(element, null, null);
        assertThat(embedding.getFilename(), is("attachment001.png"));
    }

    @Test
    public void postSerializeTest() {
        elementJsonPostProcessor.postSerialize(null, null, null);
    }
}
