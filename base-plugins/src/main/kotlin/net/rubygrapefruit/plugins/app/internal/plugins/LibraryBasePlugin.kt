package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.Versions
import net.rubygrapefruit.plugins.app.internal.MultiPlatformComponentRegistry
import net.rubygrapefruit.plugins.app.internal.checkSettingsPluginApplied
import org.gradle.api.Plugin
import org.gradle.api.Project

class LibraryBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            checkSettingsPluginApplied()

            repositories.mavenCentral()

            target.extensions.create("multiplatformComponents", MultiPlatformComponentRegistry::class.java)
            target.extensions.create("versions", Versions::class.java)
        }
    }
}