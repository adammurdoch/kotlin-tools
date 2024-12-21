package net.rubygrapefruit.plugins.bootstrap

import net.rubygrapefruit.plugins.app.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply("org.jetbrains.kotlin.multiplatform")

            repositories.mavenCentral()

            group = Versions.libs.group

            extensions.getByType(KotlinMultiplatformExtension::class.java).apply {
                jvmToolchain(Versions.plugins.java)
                jvm()
            }
        }
    }
}