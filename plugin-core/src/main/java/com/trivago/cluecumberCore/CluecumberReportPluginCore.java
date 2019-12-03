package com.trivago.cluecumberCore;/*
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

import com.trivago.cluecumberCore.constants.PluginSettings;
import com.trivago.cluecumberCore.exceptions.CluecumberPluginException;
import com.trivago.cluecumberCore.filesystem.FileIO;
import com.trivago.cluecumberCore.filesystem.FileSystemManager;
import com.trivago.cluecumberCore.json.JsonPojoConverter;
import com.trivago.cluecumberCore.json.pojo.Report;
import com.trivago.cluecumberCore.json.processors.ElementIndexPreProcessor;
import com.trivago.cluecumberCore.logging.ICluecumberLogger;
import com.trivago.cluecumberCore.logging.LoggerUtils;
import com.trivago.cluecumberCore.properties.PropertyManager;
import com.trivago.cluecumberCore.rendering.ReportGenerator;
import com.trivago.cluecumberCore.rendering.pages.pojos.pagecollections.AllScenariosPageCollection;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

import static com.trivago.cluecumberCore.logging.LoggerUtils.CluecumberLogLevel.COMPACT;
import static com.trivago.cluecumberCore.logging.LoggerUtils.CluecumberLogLevel.DEFAULT;

/**
 * The main plugin class.
 */
public class CluecumberReportPluginCore {

    private final PropertyManager propertyManager;
    private final FileSystemManager fileSystemManager;
    private final FileIO fileIO;
    private final JsonPojoConverter jsonPojoConverter;
    private final ElementIndexPreProcessor elementIndexPreProcessor;
    private final ReportGenerator reportGenerator;


    public CluecumberReportPluginCore(
            final PropertyManager propertyManager,
            final FileSystemManager fileSystemManager,
            final FileIO fileIO,
            final JsonPojoConverter jsonPojoConverter,
            final ElementIndexPreProcessor elementIndexPreProcessor,
            final ReportGenerator reportGenerator
    ) {
        this.propertyManager = propertyManager;
        this.fileSystemManager = fileSystemManager;
        this.fileIO = fileIO;
        this.jsonPojoConverter = jsonPojoConverter;
        this.elementIndexPreProcessor = elementIndexPreProcessor;
        this.reportGenerator = reportGenerator;
    }

    /**
     * Cluecumber Report start method.
     *
     * @throws CluecumberPluginException When thrown, the plugin execution is stopped.
     */
    public void taskCore(ICluecumberLogger ilogger,
                         Boolean skip,
                         String logLevel,
                         String sourceJsonReportDirectory,
                         String generatedHtmlReportDirectory,
                         LinkedHashMap<String, String> customParameters,
                         String customParametersFile,
                         boolean failScenariosOnPendingOrUndefinedSteps,
                         String customCss,
                         boolean expandBeforeAfterHooks,
                         boolean expandStepHooks,
                         boolean expandDocStrings,
                         String customStatusColorPassed,
                         String customStatusColorFailed,
                         String customStatusColorSkipped,
                         String customPageTitle) throws CluecumberPluginException {

        LoggerUtils.initialize(ilogger, logLevel);

        if (skip) {
            LoggerUtils.info("Cluecumber report generation was skipped using the <skip> property.",
                    DEFAULT);
            return;
        }

        LoggerUtils.logInfoSeparator(DEFAULT);
        LoggerUtils.info(String.format(" Cluecumber Report Plugin, version %s", getClass().getPackage()
                .getImplementationVersion()), DEFAULT);
        LoggerUtils.logInfoSeparator(DEFAULT, COMPACT);

        try {
            // Initialize and validate passed properties on gradle.build
            propertyManager.setSourceJsonReportDirectory(sourceJsonReportDirectory);
            propertyManager.setGeneratedHtmlReportDirectory(generatedHtmlReportDirectory);
            propertyManager.setCustomParameters(customParameters);
            propertyManager.setCustomParametersFile(customParametersFile);
            propertyManager.setFailScenariosOnPendingOrUndefinedSteps(failScenariosOnPendingOrUndefinedSteps);
            propertyManager.setExpandBeforeAfterHooks(expandBeforeAfterHooks);
            propertyManager.setExpandStepHooks(expandStepHooks);
            propertyManager.setExpandDocStrings(expandDocStrings);
            propertyManager.setCustomCssFile(customCss);
            propertyManager.setCustomStatusColorPassed(customStatusColorPassed);
            propertyManager.setCustomStatusColorFailed(customStatusColorFailed);
            propertyManager.setCustomStatusColorSkipped(customStatusColorSkipped);
            propertyManager.setCustomPageTitle(customPageTitle);
        } catch (Exception e) {
            throw new CluecumberPluginException(e.getMessage());
        }
        propertyManager.logProperties();

        // Create attachment directory here since they are handled during json generation.
        fileSystemManager.createDirectory(propertyManager.getGeneratedHtmlReportDirectory() + "/attachments");

        AllScenariosPageCollection allScenariosPageCollection = new AllScenariosPageCollection(propertyManager.getCustomPageTitle());
        List<Path> jsonFilePaths = fileSystemManager.getJsonFilePaths(propertyManager.getSourceJsonReportDirectory());
        for (Path jsonFilePath : jsonFilePaths) {
            String jsonString = fileIO.readContentFromFile(jsonFilePath.toString());
            try {
                Report[] reports = jsonPojoConverter.convertJsonToReportPojos(jsonString);
                allScenariosPageCollection.addReports(reports);
            } catch (CluecumberPluginException e) {
                LoggerUtils.warn("Could not parse JSON in file '" + jsonFilePath.toString() + "': " + e.getMessage());
            }
        }
        elementIndexPreProcessor.addScenarioIndices(allScenariosPageCollection.getReports());
        reportGenerator.generateReport(allScenariosPageCollection);
        LoggerUtils.info(
                "=> Cluecumber Report: " + propertyManager.getGeneratedHtmlReportDirectory() + "/" +
                        PluginSettings.SCENARIO_SUMMARY_PAGE_PATH + PluginSettings.HTML_FILE_EXTENSION,
                DEFAULT,
                COMPACT,
                LoggerUtils.CluecumberLogLevel.MINIMAL
        );
    }
}



