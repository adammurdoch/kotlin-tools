package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.checkSettingsPluginApplied
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class NativeLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            checkSettingsPluginApplied()

            plugins.apply("org.jetbrains.kotlin.multiplatform")

            repositories.mavenCentral()

            with(extensions.getByType(KotlinMultiplatformExtension::class.java)) {
                macosX64()
                macosArm64()
                linuxX64()
                mingwX64()
                val unixMain = sourceSets.create("unixMain") {
                    it.dependsOn(sourceSets.getByName("commonMain"))
                }
                val macosMain = sourceSets.create("macosMain") {
                    it.dependsOn(unixMain)
                }
                sourceSets.getByName("macosX64Main") {
                    it.dependsOn(macosMain)
                }
                sourceSets.getByName("macosArm64Main") {
                    it.dependsOn(macosMain)
                }
                sourceSets.getByName("linuxX64Main") {
                    it.dependsOn(unixMain)
                }
            }
        }
    }
}