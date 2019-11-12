package com.trivago.cluecumber;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CluecumberReportGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().create("runCluecumberReport", CluecumberReportGradleTask.class);
    }
}
