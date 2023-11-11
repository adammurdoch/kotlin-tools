package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import net.rubygrapefruit.plugins.app.internal.DefaultMultiPlatformLibrary
import net.rubygrapefruit.plugins.app.internal.JvmModuleRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(KmpBaseLibraryPlugin::class.java)
            plugins.apply(JvmConventionsPlugin::class.java)

            val lib = extensions.getByType(MultiPlatformLibrary::class.java) as DefaultMultiPlatformLibrary
            lib.jvm()
            lib.nativeDesktop()

            val extension = extensions.getByType(KotlinMultiplatformExtension::class.java)
            val jvmTarget = extension.targets.getByName("jvm") as KotlinJvmTarget

            val apiConfig = configurations.getByName(jvmTarget.apiElementsConfigurationName)

            val apiClasspath = configurations.create("apiClasspath")
            apiClasspath.extendsFrom(apiConfig)

            val compilation = jvmTarget.compilations.first()

            val classesDir = compilation.compileKotlinTaskProvider.flatMap { it.destinationDirectory }

            val moduleInfoCp = extensions.getByType(JvmModuleRegistry::class.java).moduleInfoClasspathEntryFor(lib.module, files(classesDir), apiClasspath, compilation.compileDependencyFiles)
            tasks.named("jvmJar", Jar::class.java) {
                it.from(moduleInfoCp)
            }
        }
    }
}