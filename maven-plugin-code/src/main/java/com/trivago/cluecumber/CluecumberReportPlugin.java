/*
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

package com.trivago.cluecumber;

import com.trivago.cluecumberCore.constants.PluginSettings;
import com.trivago.cluecumberCore.exceptions.CluecumberPluginException;
import com.trivago.cluecumberCore.filesystem.FileIO;
import com.trivago.cluecumberCore.filesystem.FileSystemManager;
import com.trivago.cluecumberCore.json.JsonPojoConverter;
import com.trivago.cluecumberCore.json.pojo.Report;
import com.trivago.cluecumberCore.json.processors.ElementIndexPreProcessor;
import com.trivago.cluecumber.logging.MavenCluecumberLogger;
import com.trivago.cluecumberCore.properties.PropertyManager;
import com.trivago.cluecumberCore.rendering.ReportGenerator;
import com.trivago.cluecumberCore.rendering.pages.pojos.pagecollections.AllScenariosPageCollection;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.List;

import static com.trivago.cluecumberCore.logging.BaseLogger.CluecumberLogLevel.COMPACT;
import static com.trivago.cluecumberCore.logging.BaseLogger.CluecumberLogLevel.DEFAULT;

/**
 * The main plugin class.
 */
@Mojo(name = "reporting")
public final class CluecumberReportPlugin extends PropertyCollector {

    private final MavenCluecumberLogger logger;
    private final PropertyManager propertyManager;
    private final FileSystemManager fileSystemManager;
    private final FileIO fileIO;
    private final JsonPojoConverter jsonPojoConverter;
    private final ElementIndexPreProcessor elementIndexPreProcessor;
    private final ReportGenerator reportGenerator;

    /**
     * Skip Cluecumber report generation.
     */
    @Parameter(defaultValue = "false", property = "skip")
    private boolean skip;

    @Inject
    public CluecumberReportPlugin(
            final MavenCluecumberLogger logger,
            final PropertyManager propertyManager,
            final FileSystemManager fileSystemManager,
            final FileIO fileIO,
            final JsonPojoConverter jsonPojoConverter,
            final ElementIndexPreProcessor elementIndexPreProcessor,
            final ReportGenerator reportGenerator
    ) {
        super(propertyManager);
        this.propertyManager = propertyManager;
        this.fileSystemManager = fileSystemManager;
        this.fileIO = fileIO;
        this.jsonPojoConverter = jsonPojoConverter;
        this.logger = logger;
        this.elementIndexPreProcessor = elementIndexPreProcessor;
        this.reportGenerator = reportGenerator;
    }

    /**
     * Cluecumber Report start method.
     *
     * @throws CluecumberPluginException When thrown, the plugin execution is stopped.
     */
    public void execute() throws CluecumberPluginException {
        // Initialize logger to be available outside the AbstractMojo class
        logger.initialize(getLog(), logLevel);

        if (skip) {
            logger.info("Cluecumber report generation was skipped using the <skip> property.",
                    DEFAULT);
            return;
        }

        logger.logInfoSeparator(DEFAULT);
        logger.info(String.format(" Cluecumber Report Maven Plugin, version %s", getClass().getPackage()
                .getImplementationVersion()), DEFAULT);
        logger.logInfoSeparator(DEFAULT, COMPACT);

        super.execute();

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
                logger.warn("Could not parse JSON in file '" + jsonFilePath.toString() + "': " + e.getMessage());
            }
        }
        elementIndexPreProcessor.addScenarioIndices(allScenariosPageCollection.getReports());
        reportGenerator.generateReport(allScenariosPageCollection);
        logger.info(
                "=> Cluecumber Report: " + propertyManager.getGeneratedHtmlReportDirectory() + "/" +
                        PluginSettings.SCENARIO_SUMMARY_PAGE_PATH + PluginSettings.HTML_FILE_EXTENSION,
                DEFAULT,
                COMPACT,
                MavenCluecumberLogger.CluecumberLogLevel.MINIMAL
        );
    }
}



