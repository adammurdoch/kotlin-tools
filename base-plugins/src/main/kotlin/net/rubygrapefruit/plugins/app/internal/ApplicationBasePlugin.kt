package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            checkSettingsPluginApplied()

            repositories.mavenCentral()

            target.extensions.create("multiplatformComponents", MultiPlatformComponentRegistry::class.java)
            target.extensions.create("applications", ApplicationRegistry::class.java)
            target.extensions.create("versions", net.rubygrapefruit.plugins.app.Versions::class.java)
        }
    }
}