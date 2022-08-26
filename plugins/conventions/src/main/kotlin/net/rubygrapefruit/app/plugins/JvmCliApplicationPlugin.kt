package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.JvmCliApplication
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.jvm")
            extensions.create("application", JvmCliApplication::class.java)

            repositories.mavenCentral()
        }
    }
}