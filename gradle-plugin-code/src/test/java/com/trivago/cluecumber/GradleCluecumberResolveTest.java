package com.trivago.cluecumber;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.String.format;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.StringContains.containsString;


public class GradleCluecumberResolveTest {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;
    private File settingsFile;
    private String outputDir;

    @Test
    public void GradleCluecumberResolveTest() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle");
        settingsFile = testProjectDir.newFile("settings.gradle");

        outputDir = testProjectDir.getRoot().toString() + "/serendroid";
        String runCluecumberReport = "runCluecumberReport";

        String buildFileContent =
                "plugins {\n" +
                        "    id 'java'\n" +
                        "    id 'com.trivago.cluecumber'\n" +
                        "}\n" +
                        "sourceSets {\n" +
                        "    test {\n" +
                        "        java {\n" +
                        "            srcDirs('" + toNormalizedPath(new File("src/test/java").getAbsolutePath()) + "')\n" +
                        "            exclude('**/*Test.java')\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n" +
                        "repositories {\n" +
                        "    jcenter()\n" +
                        "    mavenLocal()\n" +
                        "    mavenCentral()\n" +
                        "}\n" +
                        "dependencies {  \n" +
                        "    implementation 'com.trivago.rta:cluecumber-plugin-core:2.3.2'\n" +
                        "    implementation 'org.apache.maven.plugin-tools:maven-plugin-annotations:3.6.0'\n" +
                        "    implementation 'org.apache.maven:maven-plugin-api:3.6.1'\n" +
                        "    implementation 'com.google.code.gson:gson:2.8.5'\n" +
                        "    implementation 'io.gsonfire:gson-fire:1.9.0-alpha1'\n" +
                        "    implementation 'org.apache.kafka:kafka_2.11:2.2.0'\n" +
                        "    implementation 'junit:junit:4.13-beta-3'\n" +
                        "    implementation 'org.freemarker:freemarker:2.3.28'\n" +
                        "    implementation 'org.codehaus.plexus:plexus-utils:3.2.1'\n" +
                        "    testImplementation 'com.openpojo:openpojo:0.8.12'\n" +
                        "    testImplementation 'junit:junit:4+'\n" +
                        "    testImplementation 'org.mockito:mockito-core:3.0.0'\n" +
                        "    testImplementation 'org.mockito:mockito-all:2.0.2-beta'\n" +
                        "    testImplementation 'org.junit.vintage:junit-vintage-engine:5.5.0'\n" +
                        "    testImplementation 'org.apache.maven.plugin-testing:maven-plugin-testing-harness:3.3.0'\n" +
                        "    testImplementation 'org.codehaus.plexus:plexus-component-annotations:2.0.0'\n" +
                        "\n" +
                        "\n" +
                        "}\n" +
                        runCluecumberReport + " {\n" +
                        "    sourceJsonReportDirectory = '" + testProjectDir.getRoot().toString() + "/serendroid/'\n" +
                        "        generatedHtmlReportDirectory = '" + testProjectDir.getRoot().toString() + "/serendroid/'\n" +
                        "}";


        String settingsFileContent = "pluginManagement {\n" +
                "    repositories {\n" +
                "        maven {\n" +
                "            url mavenLocal().url\n" +
                "        }\n" +
                "        gradlePluginPortal()\n" +
                "    }\n" +
                "}\n" +
                "include 'cluecumberReport'\n" +
                "rootProject.name = 'gradle-test'\n" +
                "\n";
        writeFile(buildFile, buildFileContent);
        writeFile(settingsFile, settingsFileContent);

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.getRoot())
                .withDebug(true)
                .withArguments(runCluecumberReport, "--stacktrace", "--info")
                .forwardOutput()
                .build();

        assertThat(result.taskPaths(SUCCESS), hasItem(format(":%s", runCluecumberReport)));
        assertThat(new File(outputDir + "/index.html").exists(), is(true));
        assertThat(new String(Files.readAllBytes(Paths.get(outputDir, "index.html")), StandardCharsets.UTF_8), containsString("All Scenarios"));
    }

    private static void writeFile(File destination, String content) throws IOException {
        try (BufferedWriter output = new BufferedWriter(new FileWriter(destination))) {
            output.write(content);
        }
    }

    private static String toNormalizedPath(String path) {
        return path.replace("\\", "/"); // necessary on windows
    }
}
