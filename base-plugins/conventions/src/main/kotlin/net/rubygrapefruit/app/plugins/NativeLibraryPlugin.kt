package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.checkSettingsPluginApplied
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class NativeLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            checkSettingsPluginApplied()

            repositories.mavenCentral()

            with(extensions.getByType(KotlinMultiplatformExtension::class.java)) {
                macosX64()
                macosArm64()
                linuxX64()
                val nativeMain = sourceSets.create("nativeMain") {
                    it.dependsOn(sourceSets.getByName("commonMain"))
                }
                sourceSets.getByName("macosX64Main") {
                    it.dependsOn(nativeMain)
                }
                sourceSets.getByName("macosArm64Main") {
                    it.dependsOn(nativeMain)
                }
                sourceSets.getByName("linuxX64Main") {
                    it.dependsOn(nativeMain)
                }
            }
        }
    }
}