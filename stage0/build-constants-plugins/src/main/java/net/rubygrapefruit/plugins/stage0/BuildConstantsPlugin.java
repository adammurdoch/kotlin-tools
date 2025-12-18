package net.rubygrapefruit.plugins.stage0;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BuildConstantsPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project target) {
        target.getExtensions().create("buildConstants", BuildConstants.class);
    }
}
