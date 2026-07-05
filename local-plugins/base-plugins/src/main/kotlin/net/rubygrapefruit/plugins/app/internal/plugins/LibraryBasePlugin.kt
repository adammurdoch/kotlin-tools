package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.MultiPlatformComponentRegistry
import net.rubygrapefruit.plugins.app.internal.component.ComponentRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project

class LibraryBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyBasePlugin()

            repositories.mavenCentral()

            target.extensions.create("multiplatformComponents", MultiPlatformComponentRegistry::class.java)
            target.extensions.create("componentRegistry", ComponentRegistry::class.java)
        }
    }
}