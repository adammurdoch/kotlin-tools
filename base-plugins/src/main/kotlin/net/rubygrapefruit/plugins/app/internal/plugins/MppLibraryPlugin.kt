package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.ComponentTargets
import net.rubygrapefruit.plugins.app.internal.JvmModuleRegistry
import net.rubygrapefruit.plugins.app.internal.multiplatformComponents
import net.rubygrapefruit.plugins.app.internal.toModuleName
import net.rubygrapefruit.plugins.bootstrap.Versions
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

            val lib = extensions.create("library", JvmLibrary::class.java)
            lib.module.name.convention(toModuleName(project.name))
            lib.targetJavaVersion.convention(Versions.java)

            multiplatformComponents.registerSourceSets(ComponentTargets(lib.targetJavaVersion, setOf(NativeMachine.LinuxX64, NativeMachine.MacOSX64, NativeMachine.MacOSArm64, NativeMachine.WindowsX64)))

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