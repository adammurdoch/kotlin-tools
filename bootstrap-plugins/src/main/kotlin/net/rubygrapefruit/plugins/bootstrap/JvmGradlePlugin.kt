package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class JvmGradlePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("java-gradle-plugin")
            plugins.apply(JvmBasePlugin::class.java)
        }
    }
}