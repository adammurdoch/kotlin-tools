package net.rubygrapefruit.plugins.stage1;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.initialization.Settings;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("unused")
public class IncludedBuildPlugin implements Plugin<Settings> {
    @Override
    public void apply(@NotNull Settings target) {
        target.getGradle().rootProject(project -> {
            project.getPlugins().apply("lifecycle-base");
            addLifecycle(project, "build");
            addLifecycle(project, "assemble");
            addLifecycle(project, "check");
            addTask(project, "verifySamples");
        });
    }

    private void addTask(Project project, String taskName) {
        TaskProvider<Task> provider = project.getTasks().register(taskName);
        addTaskDependencies(project, taskName, provider);
    }

    private static void addLifecycle(Project project, String taskName) {
        TaskProvider<Task> provider = project.getTasks().named(taskName);
        addTaskDependencies(project, taskName, provider);
    }

    private static void addTaskDependencies(Project project, String taskName, TaskProvider<Task> provider) {
        provider.configure(task -> task.dependsOn((Callable<List<Task>>) () -> {
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
