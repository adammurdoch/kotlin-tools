package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.LibraryRegistry
import net.rubygrapefruit.app.internal.checkSettingsPluginApplied
import org.gradle.api.Plugin
import org.gradle.api.Project

class LibraryBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            checkSettingsPluginApplied()

            repositories.mavenCentral()

            target.extensions.create("libraries", LibraryRegistry::class.java)
        }
    }
}