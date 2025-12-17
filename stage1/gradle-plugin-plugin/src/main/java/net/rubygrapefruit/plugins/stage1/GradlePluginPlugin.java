package net.rubygrapefruit.plugins.stage1;

import net.rubygrapefruit.plugins.stage0.BuildConstants;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class GradlePluginPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project target) {
        System.out.println("-> USING: " + BuildConstants.kotlin.version);
    }
}

