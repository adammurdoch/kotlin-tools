package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class JvmBasePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("org.jetbrains.kotlin.jvm")

            repositories.mavenCentral()

            extensions.getByType(KotlinJvmProjectExtension::class.java).run {
                jvmToolchain(Versions.pluginsJava)
            }
        }
    }
}