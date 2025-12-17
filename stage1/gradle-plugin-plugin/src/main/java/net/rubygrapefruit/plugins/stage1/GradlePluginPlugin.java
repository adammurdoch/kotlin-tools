package net.rubygrapefruit.plugins.stage1;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class GradlePluginPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project target) {
        target.getPlugins().apply("org.jetbrains.kotlin.jvm");
        target.getPlugins().apply("java-gradle-plugin");
    }
}
