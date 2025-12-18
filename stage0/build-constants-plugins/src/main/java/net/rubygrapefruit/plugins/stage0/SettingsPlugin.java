package net.rubygrapefruit.plugins.stage0;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class SettingsPlugin implements Plugin<Settings> {
    @Override
    public void apply(@NotNull Settings target) {
        target.getExtensions().create("buildConstants", BuildConstants.class);
    }
}
