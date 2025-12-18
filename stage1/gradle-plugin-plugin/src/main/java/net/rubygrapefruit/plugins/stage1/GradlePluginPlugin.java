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
        target.getPlugins().apply("org.jetbrains.kotlin.jvm");
        target.getPlugins().apply("java-gradle-plugin");
        target.getPlugins().apply(BuildConstants.constants.stage0.buildConstants.pluginId);

        target.getRepositories().mavenCentral();

        KotlinProjectExtension kotlin = target.getExtensions().getByType(KotlinProjectExtension.class);
        kotlin.jvmToolchain(BuildConstants.constants.plugins.jvm.version);

        GradlePluginDevelopmentExtension pluginDevExtension = target.getExtensions().getByType(GradlePluginDevelopmentExtension.class);
        target.getExtensions().create("pluginBundle", PluginBundle.class, pluginDevExtension);
    }
}
