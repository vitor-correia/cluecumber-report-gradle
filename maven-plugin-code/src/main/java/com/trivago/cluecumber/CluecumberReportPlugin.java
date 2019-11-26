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

import com.trivago.cluecumberCore.CluecumberReportPluginCore;
import com.trivago.cluecumberCore.exceptions.CluecumberPluginException;
import com.trivago.cluecumberCore.filesystem.FileIO;
import com.trivago.cluecumberCore.filesystem.FileSystemManager;
import com.trivago.cluecumberCore.json.JsonPojoConverter;
import com.trivago.cluecumberCore.json.processors.ElementIndexPreProcessor;
import com.trivago.cluecumber.logging.MavenCluecumberLogger;
import com.trivago.cluecumberCore.logging.BaseLogger;
import com.trivago.cluecumberCore.logging.IBaseLogger;
import com.trivago.cluecumberCore.properties.PropertyManager;
import com.trivago.cluecumberCore.rendering.ReportGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;
import java.util.LinkedHashMap;

/**
 * The main plugin class.
 */
@Mojo(name = "reporting")
public final class CluecumberReportPlugin extends AbstractMojo {

    private final BaseLogger logger;
    private final PropertyManager propertyManager;
    private final FileSystemManager fileSystemManager;
    private final FileIO fileIO;
    private final JsonPojoConverter jsonPojoConverter;
    private final ElementIndexPreProcessor elementIndexPreProcessor;
    private final ReportGenerator reportGenerator;
    private final CluecumberReportPluginCore cluecumberReportPluginCore;

    /**
     * The path to the Cucumber JSON files.
     */
    @Parameter(property = "reporting.sourceJsonReportDirectory", required = true)
    private String sourceJsonReportDirectory = "";

    /**
     * The location of the generated report.
     */
    @Parameter(property = "reporting.generatedHtmlReportDirectory", required = true)
    private String generatedHtmlReportDirectory = "";

    /**
     * Custom parameters to add to the report.
     */
    @Parameter(property = "reporting.customParameters")
    private LinkedHashMap<String, String> customParameters = new LinkedHashMap<>();

    /**
     * Path to a properties file. The included properties are converted to custom parameters and added to the others.
     * <pre>
     * My_Custom_Parameter=This is my custom value
     * My_Custom_Parameter2=This is another value
     * </pre>
     */
    @Parameter(property = "reporting.customParametersFile")
    private String customParametersFile = "";

    /**
     * Mark scenarios as failed if they contain pending or undefined steps (default: false).
     */
    @Parameter(property = "reporting.failScenariosOnPendingOrUndefinedSteps", defaultValue = "false")
    private boolean failScenariosOnPendingOrUndefinedSteps;

    /**
     * Custom CSS that is applied on top of Cluecumber's default styles.
     */
    @Parameter(property = "reporting.customCss")
    private String customCss = "";

    /**
     * Custom flag that determines if before and after hook sections of scenario detail pages should be expanded (default: false).
     */
    @Parameter(property = "reporting.expandBeforeAfterHooks", defaultValue = "false")
    private boolean expandBeforeAfterHooks;

    /**
     * Custom flag that determines if step hook sections of scenario detail pages should be expanded (default: false).
     */
    @Parameter(property = "reporting.expandStepHooks", defaultValue = "false")
    private boolean expandStepHooks;

    /**
     * Custom flag that determines if doc string sections of scenario detail pages should be expanded (default: false).
     */
    @Parameter(property = "reporting.expandDocStrings", defaultValue = "false")
    private boolean expandDocStrings;

    /**
     * Custom hex color for passed scenarios (e.g. '#00ff00')'.
     */
    @Parameter(property = "reporting.customStatusColorPassed")
    private String customStatusColorPassed;

    /**
     * Custom hex color for failed scenarios (e.g. '#ff0000')'.
     */
    @Parameter(property = "reporting.customStatusColorFailed")
    private String customStatusColorFailed;

    /**
     * Custom hex color for skipped scenarios (e.g. '#ffff00')'.
     */
    @Parameter(property = "reporting.customStatusColorSkipped")
    private String customStatusColorSkipped;

    /**
     * Custom page title for the generated report.
     */
    @Parameter(property = "reporting.customPageTitle")
    private String customPageTitle;

    /**
     * Optional log level to control what information is logged in the console.
     * Allowed values: default, compact, minimal, off
     */
    @Parameter(property = "parallel.logLevel", defaultValue = "default")
    String logLevel;

    /**
     * Skip Cluecumber report generation.
     */
    @Parameter(defaultValue = "false", property = "skip")
    private boolean skip;

    @Inject
    public CluecumberReportPlugin(
            final BaseLogger logger,
            final PropertyManager propertyManager,
            final FileSystemManager fileSystemManager,
            final FileIO fileIO,
            final JsonPojoConverter jsonPojoConverter,
            final ElementIndexPreProcessor elementIndexPreProcessor,
            final ReportGenerator reportGenerator) {
        this.logger = logger;
        this.propertyManager = propertyManager;
        this.fileSystemManager = fileSystemManager;
        this.fileIO = fileIO;
        this.jsonPojoConverter = jsonPojoConverter;
        this.elementIndexPreProcessor = elementIndexPreProcessor;
        this.reportGenerator = reportGenerator;
        this.cluecumberReportPluginCore = new CluecumberReportPluginCore(
                propertyManager,
                fileSystemManager,
                fileIO,
                jsonPojoConverter,
                elementIndexPreProcessor,
                reportGenerator
                );
    }

    /**
     * Cluecumber Report start method.
     *
     * @throws CluecumberPluginException When thrown, the plugin execution is stopped.
     */
    public void execute() throws CluecumberPluginException {
        cluecumberReportPluginCore.taskCore(
                logger,
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



