package net.rubygrapefruit.plugins.stage1;

import net.rubygrapefruit.plugins.stage0.BuildConstants;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension;

@SuppressWarnings("unused")
public class GradlePluginPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project target) {
        target.getPlugins().apply(JvmBasePlugin.class);
        target.getPlugins().apply("java-gradle-plugin");

        target.getDependencies().add("implementation", BuildConstants.constants.stage0.buildConstants.coordinates);

        GradlePluginDevelopmentExtension pluginDevExtension = target.getExtensions().getByType(GradlePluginDevelopmentExtension.class);
        target.getExtensions().create("pluginBundle", PluginBundle.class, pluginDevExtension);
    }
}
