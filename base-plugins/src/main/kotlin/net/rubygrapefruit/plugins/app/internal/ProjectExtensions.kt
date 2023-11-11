package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val Project.applications: ApplicationRegistry
    get() = extensions.getByType(ApplicationRegistry::class.java)

val Project.multiplatformComponents: MultiPlatformComponentRegistry
    get() = extensions.getByType(MultiPlatformComponentRegistry::class.java)

val Project.kotlin: KotlinMultiplatformExtension
    get() = extensions.getByType(KotlinMultiplatformExtension::class.java)

internal fun Project.settingsPluginApplied() {
    extensions.add("__settings_plugin_applied__", "true")
}

internal fun Project.checkSettingsPluginApplied() {
    if (rootProject.extensions.findByName("__settings_plugin_applied__") == null) {
        throw IllegalStateException("Plugin 'net.rubygrapefruit.kotlin-base' must be applied to the settings of this build.")
    }
}
