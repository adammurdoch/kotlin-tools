package net.rubygrapefruit.plugins.stage1;

import net.rubygrapefruit.plugins.stage0.BuildConstants;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.initialization.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("unused")
public class SettingsPlugin implements Plugin<Settings> {
    @Override
    public void apply(@NotNull Settings target) {
        target.getPlugins().apply(BuildConstants.constants.foojay.plugin.id);

        target.getGradle().rootProject(project -> {
            project.getPlugins().apply("lifecycle-base");
            addTask(project, "build");
            addTask(project, "assemble");
            addTask(project, "check");
        });
    }

    private static void addTask(Project project, String taskName) {
        project.getTasks().named(taskName, task -> task.dependsOn((Callable<List<Task>>) () -> {
            List<Task> result = new ArrayList<>();
            for (Project subproject : project.getSubprojects()) {
                Task subprojectTask = subproject.getTasks().findByName(taskName);
                if (subprojectTask != null) {
                    result.add(subprojectTask);
                }
            }
            return result;
        }));
    }
}
