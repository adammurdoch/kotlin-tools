package net.rubygrapefruit.plugins.stage0;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

@SuppressWarnings("unused")
public class JavaGradlePluginPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getPlugins().apply("java-gradle-plugin");

        target.getRepositories().mavenCentral();

        JavaPluginExtension javaExtension = target.getExtensions().getByType(JavaPluginExtension.class);

        javaExtension.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(BuildConstants.constants.plugins.jvm.version));
    }
}
