package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.ApplicationRegistry
import net.rubygrapefruit.app.internal.MultiPlatformComponentRegistry
import net.rubygrapefruit.app.internal.checkSettingsPluginApplied
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            checkSettingsPluginApplied()

            repositories.mavenCentral()

            target.extensions.create("multiplatformComponents", MultiPlatformComponentRegistry::class.java)
            target.extensions.create("applications", ApplicationRegistry::class.java)
        }
    }
}