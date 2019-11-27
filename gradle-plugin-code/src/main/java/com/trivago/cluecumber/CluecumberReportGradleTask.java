package com.trivago.cluecumber;/*
 * Copyright 2019 trivago N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.trivago.cluecumber.logging.CluecumberLogger;

import com.trivago.cluecumberCore.CluecumberReportPluginCore;
import com.trivago.cluecumberCore.exceptions.CluecumberPluginException;
import com.trivago.cluecumberCore.filesystem.FileIO;
import com.trivago.cluecumberCore.filesystem.FileSystemManager;
import com.trivago.cluecumberCore.json.JsonPojoConverter;
import com.trivago.cluecumberCore.json.processors.ElementIndexPreProcessor;
import com.trivago.cluecumberCore.json.processors.ElementJsonPostProcessor;
import com.trivago.cluecumberCore.json.processors.ReportJsonPostProcessor;
import com.trivago.cluecumberCore.logging.LoggerUtils;
import com.trivago.cluecumberCore.logging.ICluecumberLogger;
import com.trivago.cluecumberCore.properties.PropertiesFileLoader;
import com.trivago.cluecumberCore.properties.PropertyManager;
import com.trivago.cluecumberCore.rendering.ReportGenerator;
import com.trivago.cluecumberCore.rendering.pages.charts.ChartJsonConverter;
import com.trivago.cluecumberCore.rendering.pages.renderering.*;
import com.trivago.cluecumberCore.rendering.pages.templates.TemplateConfiguration;
import com.trivago.cluecumberCore.rendering.pages.templates.TemplateEngine;
import com.trivago.cluecumberCore.constants.ChartConfiguration;
import com.trivago.cluecumberCore.rendering.pages.visitors.*;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.util.LinkedHashMap;

/**
 * The main plugin class.
 */
public class CluecumberReportGradleTask extends DefaultTask {

    private final ICluecumberLogger ilogger = new CluecumberLogger(Logging.getLogger(CluecumberLogger.class));

    private LoggerUtils logger = new LoggerUtils();

    private final FileSystemManager fileSystemManager = new FileSystemManager();
    private final FileIO fileIO = new FileIO();
    private PropertiesFileLoader  propertiesFileLoader = new PropertiesFileLoader(fileIO);
    private final PropertyManager propertyManager = new PropertyManager(logger, fileIO, propertiesFileLoader);
    private final ElementIndexPreProcessor elementIndexPreProcessor = new ElementIndexPreProcessor();
    private final ReportJsonPostProcessor jsonPro = new ReportJsonPostProcessor();
    private final ElementJsonPostProcessor eJsonPro = new ElementJsonPostProcessor(propertyManager, fileIO, logger);
    private final JsonPojoConverter jsonPojoConverter = new JsonPojoConverter(jsonPro, eJsonPro);
    private final CustomCssRenderer customCssRenderer = new CustomCssRenderer(propertyManager);
    private final TemplateConfiguration templateConfiguration = new TemplateConfiguration();
    private final TemplateEngine templateEngine = new TemplateEngine(templateConfiguration);
    private final ChartJsonConverter chartJsonConverter = new ChartJsonConverter();
    private final ChartConfiguration chartConfiguration = new ChartConfiguration(propertyManager);

    private final AllScenariosPageRenderer allScenariosPageRenderer = new AllScenariosPageRenderer(chartJsonConverter,chartConfiguration, propertyManager);
    private final AllFeaturesPageRenderer allFeaturePageRenderer = new AllFeaturesPageRenderer(chartJsonConverter,chartConfiguration);
    private final AllTagsPageRenderer allTagsPageRenderer = new AllTagsPageRenderer(chartJsonConverter, chartConfiguration);
    private final AllStepsPageRenderer allStepsPageRenderer = new AllStepsPageRenderer(chartJsonConverter, chartConfiguration);

    private final ScenarioDetailsPageRenderer scenarioDetailsPageRenderer = new ScenarioDetailsPageRenderer(chartJsonConverter, chartConfiguration, propertyManager);
    private final ScenarioVisitor scenarioVisitor = new ScenarioVisitor(fileIO,templateEngine,propertyManager,allScenariosPageRenderer,scenarioDetailsPageRenderer);
    private final FeatureVisitor featureVisitor = new FeatureVisitor(fileIO,templateEngine,propertyManager,allFeaturePageRenderer,allScenariosPageRenderer);
    private final TagVisitor tagVisitor = new TagVisitor(fileIO,templateEngine,propertyManager,allTagsPageRenderer,allScenariosPageRenderer);
    private final StepVisitor stepVisitor = new StepVisitor(fileIO,templateEngine,propertyManager,allStepsPageRenderer,allScenariosPageRenderer);

    private final VisitorDirectory visitorDirectory = new VisitorDirectory(scenarioVisitor,featureVisitor,tagVisitor,stepVisitor);
    private final ReportGenerator reportGenerator = new ReportGenerator(fileIO, templateEngine, propertyManager, fileSystemManager, customCssRenderer,visitorDirectory);

    /**
     * Skip Cluecumber report generation.
     */
    private boolean skip = false;

    @Input @Optional
    public boolean getSkip(){
        return skip;
    }

    public void setSkip(boolean skip){
        this.skip = skip;
    }

    /**
     * The path to the Cucumber JSON files.
     */
    @Input
    private String sourceJsonReportDirectory = "";

    public String getSourceJsonReportDirectory() {
        return sourceJsonReportDirectory;
    }

    public void setSourceJsonReportDirectory(String sourceJsonReportDirectory) {
        this.sourceJsonReportDirectory = sourceJsonReportDirectory;
    }

    /**
     * The location of the generated report.
     */
    @Input
    private String generatedHtmlReportDirectory = "";

    public String getGeneratedHtmlReportDirectory() {
        return generatedHtmlReportDirectory;
    }

    public void setGeneratedHtmlReportDirectory(String generatedHtmlReportDirectory) {
        this.generatedHtmlReportDirectory = generatedHtmlReportDirectory;
    }

    /**
     * Custom parameters to add to the report.
     */
    @Input @Optional
    private LinkedHashMap<String, String> customParameters = new LinkedHashMap<>();

    public LinkedHashMap<String, String> getCustomParameters() {
        return customParameters;
    }

    public void setCustomParameters(LinkedHashMap<String, String> customParameters) {
        this.customParameters = customParameters;
    }

    /**
     * Path to a properties file. The included properties are converted to custom parameters and added to the others.
     * <pre>
     * My_Custom_Parameter=This is my custom value
     * My_Custom_Parameter2=This is another value
     * </pre>
     */
    @Input @Optional
    private String customParametersFile="";

    public String getCustomParametersFile() {
        return customParametersFile;
    }

    public void setCustomParametersFile(String customParametersFile) {
        this.customParametersFile = customParametersFile;
    }

    /**
     * Mark scenarios as failed if they contain pending or undefined steps (default: false).
     */
    @Input @Optional
    private boolean failScenariosOnPendingOrUndefinedSteps = false;

    public boolean isFailScenariosOnPendingOrUndefinedSteps() {
        return failScenariosOnPendingOrUndefinedSteps;
    }

    public void setFailScenariosOnPendingOrUndefinedSteps(boolean failScenariosOnPendingOrUndefinedSteps) {
        this.failScenariosOnPendingOrUndefinedSteps = failScenariosOnPendingOrUndefinedSteps;
    }

    /**
     * Custom CSS that is applied on top of Cluecumber's default styles.
     */
    @Input @Optional
    private String customCss="";

    public String getCustomCss() {
        return customCss;
    }

    public void setCustomCss(String customCss) {
        this.customCss = customCss;
    }

    /**
     * Custom flag that determines if before and after hook sections of scenario detail pages should be expanded (default: false).
     */
    @Input @Optional
    private boolean expandBeforeAfterHooks = false;

    public boolean isExpandBeforeAfterHooks() {
        return expandBeforeAfterHooks;
    }

    public void setExpandBeforeAfterHooks(boolean expandBeforeAfterHooks) {
        this.expandBeforeAfterHooks = expandBeforeAfterHooks;
    }

    /**
     * Custom flag that determines if step hook sections of scenario detail pages should be expanded (default: false).
     */
    @Input @Optional
    private boolean expandStepHooks = false;

    public boolean isExpandStepHooks() {
        return expandStepHooks;
    }

    public void setExpandStepHooks(boolean expandStepHooks) {
        this.expandStepHooks = expandStepHooks;
    }

    /**
     * Custom flag that determines if doc string sections of scenario detail pages should be expanded (default: false).
     */
    @Input @Optional
    private boolean expandDocStrings = false;

    public boolean isExpandDocStrings() {
        return expandDocStrings;
    }

    public void setExpandDocStrings(boolean expandDocStrings) {
        this.expandDocStrings = expandDocStrings;
    }

    /**
     * Custom hex color for passed scenarios (e.g. '#00ff00')'.
     */
    @Input @Optional
    private String customStatusColorPassed = "#04B404";

    public String getCustomStatusColorPassed() {
        return customStatusColorPassed;
    }

    public void setCustomStatusColorPassed(String customStatusColorPassed) {
        this.customStatusColorPassed = customStatusColorPassed;
    }

    /**
     * Custom hex color for failed scenarios (e.g. '#ff0000')'.
     */
    @Input @Optional
    private String customStatusColorFailed = "#C94A38";

    public String getCustomStatusColorFailed() {
        return customStatusColorFailed;
    }

    public void setCustomStatusColorFailed(String customStatusColorFailed) {
        this.customStatusColorFailed = customStatusColorFailed;
    }

    /**
     * Custom hex color for skipped scenarios (e.g. '#ffff00')'.
     */
    @Input @Optional
    private String customStatusColorSkipped = "#F48F00";

    public String getCustomStatusColorSkipped() {
        return customStatusColorSkipped;
    }

    public void setCustomStatusColorSkipped(String customStatusColorSkipped) {
        this.customStatusColorSkipped = customStatusColorSkipped;
    }

    /**
     * Custom page title for the generated report.
     */
    @Input @Optional
    private String customPageTitle;

    public String getCustomPageTitle() {
        return customPageTitle;
    }

    public void setCustomPageTitle(String customPageTitle) {
        this.customPageTitle = customPageTitle;
    }

    /**
     * Optional log level to control what information is logged in the console.
     * Allowed values: default, compact, minimal, off
     */
    @Input @Optional
    String logLevel="default";

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Cluecumber Report start method.
     *
     * @throws CluecumberPluginException When thrown, the plugin execution is stopped.
     */
    @TaskAction
    public void resolve() throws CluecumberPluginException {
        final CluecumberReportPluginCore cluecumberReportPluginCore = new CluecumberReportPluginCore(
                propertyManager,
                fileSystemManager,
                fileIO,
                jsonPojoConverter,
                elementIndexPreProcessor,
                reportGenerator
        );

        logger.initialize(ilogger,logLevel);

        cluecumberReportPluginCore.taskCore(
                ilogger,
                skip,
                logLevel,
                sourceJsonReportDirectory,
                generatedHtmlReportDirectory,
                customParameters,
                customParametersFile,
                failScenariosOnPendingOrUndefinedSteps,
                customCss,
                expandBeforeAfterHooks,
                expandStepHooks,
                expandDocStrings,
                customStatusColorPassed,
                customStatusColorFailed,
                customStatusColorSkipped,
                customPageTitle
        );
    }
}



