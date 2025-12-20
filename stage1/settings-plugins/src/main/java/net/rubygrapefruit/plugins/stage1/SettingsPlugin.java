package net.rubygrapefruit.plugins.stage1;

import net.rubygrapefruit.plugins.stage0.BuildConstants;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class SettingsPlugin implements Plugin<Settings> {
    @Override
    public void apply(@NotNull Settings target) {
        target.getPlugins().apply(BuildConstants.constants.foojay.plugin.id);

        target.getGradle().rootProject(project -> {
            project.getBuildscript().getRepositories().mavenCentral();
            project.getBuildscript().getDependencies().add("classpath", BuildConstants.constants.kotlin.plugin.coordinates);
            project.getBuildscript().getDependencies().add("classpath", BuildConstants.constants.serialization.plugin.coordinates);
        });
    }
}
