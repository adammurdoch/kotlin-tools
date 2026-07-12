package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import net.rubygrapefruit.plugins.app.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import kotlin.math.max

@Suppress("unused")
class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(LibraryBasePlugin::class.java)
            plugins.apply(ComponentBasePlugin::class.java)
            plugins.apply(JvmConventionsPlugin::class.java)

            JvmConventionsPlugin.addApiConstraints(project, "commonMainApi")

            componentRegistry.from<DefaultJvmLibrary> {
                derive { component ->
                    val kotlin = target.kotlin
                    kotlin.jvmToolchain {
                        it.languageVersion.set(component.targetJvmVersion.map { JavaLanguageVersion.of(it) })
                    }
                    val jvmTarget = kotlin.jvm()
                    val apiConfig = configurations.getByName("jvmCompileClasspath")
                    val runtimeClasspath = configurations.getByName("jvmRuntimeClasspath")

                    val apiClasspath = configurations.create("apiClasspath")
                    apiClasspath.extendsFrom(apiConfig)

                    val compilation = jvmTarget.compilations.first()

                    val classesDir = files(compilation.compileTaskProvider.flatMap { (it as KotlinJvmCompile).destinationDirectory })

                    val moduleInfoCp = extensions.getByType(JvmModuleRegistry::class.java)
                        .inspectClassPathsFor(component.module, null, classesDir, apiClasspath, runtimeClasspath)
                        .moduleInfoClasspath

                    tasks.whenObjectAdded { task ->
                        if (task.name == jvmTarget.artifactsTaskName) {
                            (task as Jar).from(moduleInfoCp)
                        }
                    }

                    jvmTarget.testRuns.configureEach { testRun ->
                        // Run tests in parallel
                        testRun.executionTask.configure { jvmTest ->
                            jvmTest.maxParallelForks = max(1, Runtime.getRuntime().availableProcessors() / 3)
                        }
                    }
                }
            }

            val lib = extensions.create(
                MultiPlatformLibrary::class.java,
                "library",
                DefaultMultiPlatformLibrary::class.java,
                multiplatformComponents,
                objects
            ) as DefaultMultiPlatformLibrary
            componentRegistry.register(lib)
        }
    }
}