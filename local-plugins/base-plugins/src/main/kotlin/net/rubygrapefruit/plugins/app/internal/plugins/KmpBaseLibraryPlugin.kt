package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import net.rubygrapefruit.plugins.app.internal.DefaultMultiPlatformLibrary
import net.rubygrapefruit.plugins.app.internal.JvmModuleRegistry
import net.rubygrapefruit.plugins.app.internal.kotlin
import net.rubygrapefruit.plugins.app.internal.multiplatformComponents
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import kotlin.math.max

class KmpBaseLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(LibraryBasePlugin::class.java)
            plugins.apply(JvmConventionsPlugin::class.java)

            JvmConventionsPlugin.addApiConstraints(project, "commonMainApi")

            val lib = extensions.create(
                MultiPlatformLibrary::class.java,
                "library",
                DefaultMultiPlatformLibrary::class.java,
                multiplatformComponents,
                objects,
                project
            ) as DefaultMultiPlatformLibrary

            multiplatformComponents.jvmTarget {
                val extension = kotlin
                val jvmTarget = extension.targets.getByName("jvm") as KotlinJvmTarget

                val apiConfig = configurations.getByName("jvmCompileClasspath")
                val runtimeClasspath = configurations.getByName("jvmRuntimeClasspath")

                val apiClasspath = configurations.create("apiClasspath")
                apiClasspath.extendsFrom(apiConfig)

                val compilation = jvmTarget.compilations.first()

                val classesDir = files(compilation.compileTaskProvider.flatMap { (it as KotlinJvmCompile).destinationDirectory })

                val moduleInfoCp = extensions.getByType(JvmModuleRegistry::class.java)
                    .inspectClassPathsFor(lib.module, null, classesDir, apiClasspath, runtimeClasspath)
                    .moduleInfoClasspath
                tasks.named("jvmJar", Jar::class.java) {
                    it.from(moduleInfoCp)
                }

                jvmTarget.testRuns.configureEach { testRun ->
                    // Run tests in parallel
                    testRun.executionTask.configure { jvmTest ->
                        jvmTest.maxParallelForks = max(1, Runtime.getRuntime().availableProcessors() / 3)
                    }
                }
            }
        }
    }
}