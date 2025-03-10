package com.trivago.cluecumberCore.rendering.pages.visitors;

import com.trivago.cluecumberCore.constants.PluginSettings;
import com.trivago.cluecumberCore.exceptions.CluecumberPluginException;
import com.trivago.cluecumberCore.filesystem.FileIO;
import com.trivago.cluecumberCore.json.pojo.Element;
import com.trivago.cluecumberCore.json.pojo.Report;
import com.trivago.cluecumberCore.properties.PropertyManager;
import com.trivago.cluecumberCore.rendering.pages.pojos.pagecollections.AllScenariosPageCollection;
import com.trivago.cluecumberCore.rendering.pages.pojos.pagecollections.ScenarioDetailsPageCollection;
import com.trivago.cluecumberCore.rendering.pages.renderering.AllScenariosPageRenderer;
import com.trivago.cluecumberCore.rendering.pages.renderering.ScenarioDetailsPageRenderer;
import com.trivago.cluecumberCore.rendering.pages.templates.TemplateEngine;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.trivago.cluecumberCore.rendering.pages.templates.TemplateEngine.Template.*;

@Singleton
public class ScenarioVisitor implements PageVisitor {

    private final FileIO fileIO;
    private final TemplateEngine templateEngine;
    private final PropertyManager propertyManager;
    private final AllScenariosPageRenderer allScenariosPageRenderer;
    private final ScenarioDetailsPageRenderer scenarioDetailsPageRenderer;

    @Inject
    public ScenarioVisitor(
            final FileIO fileIO,
            final TemplateEngine templateEngine,
            final PropertyManager propertyManager,
            final AllScenariosPageRenderer allScenariosPageRenderer,
            final ScenarioDetailsPageRenderer scenarioDetailsPageRenderer
    ) {
        this.fileIO = fileIO;
        this.templateEngine = templateEngine;
        this.propertyManager = propertyManager;
        this.allScenariosPageRenderer = allScenariosPageRenderer;
        this.scenarioDetailsPageRenderer = scenarioDetailsPageRenderer;
    }

    @Override
    public void visit(final AllScenariosPageCollection allScenariosPageCollection) throws CluecumberPluginException {
        // All scenarios page
        fileIO.writeContentToFile(
                allScenariosPageRenderer.getRenderedContent(allScenariosPageCollection,
                        templateEngine.getTemplate(ALL_SCENARIOS)),
                propertyManager.getGeneratedHtmlReportDirectory() + "/" +
                        PluginSettings.SCENARIO_SUMMARY_PAGE_PATH + PluginSettings.HTML_FILE_EXTENSION);

        // Scenario sequence page
        fileIO.writeContentToFile(
                allScenariosPageRenderer.getRenderedContent(
                        allScenariosPageCollection,
                        templateEngine.getTemplate(SCENARIO_SEQUENCE)
                ),
                propertyManager.getGeneratedHtmlReportDirectory() + "/" + PluginSettings.PAGES_DIRECTORY + "/" +
                        PluginSettings.SCENARIO_SEQUENCE_PAGE_PATH + PluginSettings.HTML_FILE_EXTENSION);

        // Scenario detail pages
        ScenarioDetailsPageCollection scenarioDetailsPageCollection;
        for (Report report : allScenariosPageCollection.getReports()) {
            for (Element element : report.getElements()) {
                scenarioDetailsPageCollection = new ScenarioDetailsPageCollection(element, propertyManager.getCustomPageTitle());
                fileIO.writeContentToFile(
                        scenarioDetailsPageRenderer.getRenderedContent(
                                scenarioDetailsPageCollection,
                                templateEngine.getTemplate(SCENARIO_DETAILS)
                        ),
                        propertyManager.getGeneratedHtmlReportDirectory() + "/" +
                                PluginSettings.PAGES_DIRECTORY + PluginSettings.SCENARIO_DETAIL_PAGE_FRAGMENT +
                                element.getScenarioIndex() + PluginSettings.HTML_FILE_EXTENSION);
            }
        }
    }
}
