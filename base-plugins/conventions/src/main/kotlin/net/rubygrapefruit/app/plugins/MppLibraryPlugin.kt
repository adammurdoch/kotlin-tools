package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.JvmLibrary
import net.rubygrapefruit.app.internal.JvmModuleRegistry
import net.rubygrapefruit.app.internal.checkSettingsPluginApplied
import net.rubygrapefruit.app.internal.toModuleName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

class MppLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            checkSettingsPluginApplied()

            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(JvmConventionsPlugin::class.java)

            repositories.mavenCentral()

            with(extensions.getByType(KotlinMultiplatformExtension::class.java)) {
                jvm()
                macosX64()
                macosArm64()
                linuxX64()
                mingwX64()
                val nativeMain = sourceSets.create("nativeMain") {
                    it.dependsOn(sourceSets.getByName("commonMain"))
                }
                val unixMain = sourceSets.create("unixMain") {
                    it.dependsOn(nativeMain)
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
                sourceSets.getByName("mingwX64Main") {
                    it.dependsOn(nativeMain)
                }
            }

            val lib = extensions.create("library", JvmLibrary::class.java)
            lib.module.name.convention(toModuleName(project.name))

            val extension = extensions.getByType(KotlinMultiplatformExtension::class.java)
            val target = extension.targets.getByName("jvm") as KotlinJvmTarget
            println("-> api elements config: ${target.apiElementsConfigurationName}")
            println("-> compilation API config: ${target.compilations.first().apiConfigurationName}")
            println("-> compilation impl config: ${target.compilations.first().implementationConfigurationName}")
            println("-> compilation comp dep files: ${target.compilations.first().compileDependencyFiles}")
            println("-> compilation runtime dep files: ${target.compilations.first().runtimeDependencyFiles}")

            val moduleInfoCp = extensions.getByType(JvmModuleRegistry::class.java).moduleInfoClasspathEntryFor(lib.module, target.compilations.first().compileDependencyFiles)
            tasks.named("jvmJar", Jar::class.java) {
                it.from(moduleInfoCp)
            }
        }
    }
}