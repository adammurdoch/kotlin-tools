package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.ApplicationRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            repositories.mavenCentral()

            target.extensions.create("applications", ApplicationRegistry::class.java)
        }
    }
}