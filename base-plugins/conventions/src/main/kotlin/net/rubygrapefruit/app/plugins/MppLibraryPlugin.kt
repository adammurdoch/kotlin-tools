package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.JvmLibrary
import net.rubygrapefruit.app.NativeMachine
import net.rubygrapefruit.app.internal.ComponentTargets
import net.rubygrapefruit.app.internal.JvmModuleRegistry
import net.rubygrapefruit.app.internal.multiplatformComponents
import net.rubygrapefruit.app.internal.toModuleName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

class MppLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(LibraryBasePlugin::class.java)
            plugins.apply(JvmConventionsPlugin::class.java)

            multiplatformComponents.registerSourceSets(ComponentTargets(true, setOf(NativeMachine.LinuxX64, NativeMachine.MacOSX64, NativeMachine.MacOSArm64, NativeMachine.WindowsX64), false))

            val lib = extensions.create("library", JvmLibrary::class.java)
            lib.module.name.convention(toModuleName(project.name))

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