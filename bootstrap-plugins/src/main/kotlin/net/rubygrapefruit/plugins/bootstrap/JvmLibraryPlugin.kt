package net.rubygrapefruit.plugins.bootstrap

import net.rubygrapefruit.plugins.app.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class JvmLibraryPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("java-library")
            plugins.apply(JvmBasePlugin::class.java)
            group = Versions.libs.group
        }
    }
}