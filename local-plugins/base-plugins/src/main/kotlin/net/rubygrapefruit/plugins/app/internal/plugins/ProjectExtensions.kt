package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.checkSettingsPluginApplied
import org.gradle.api.Project

/**
 * Shared logic for each type of project.
 */
internal fun Project.applyBasePlugin() {
    checkSettingsPluginApplied()
    plugins.apply(BuildConstantsPlugin::class.java)
}