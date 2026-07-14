package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import net.rubygrapefruit.plugins.app.Versions
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
            plugins.apply(MultiPlatformComponentBasePlugin::class.java)
            plugins.apply(JvmConventionsPlugin::class.java)

            JvmConventionsPlugin.addApiConstraints(project, "commonMainApi")

            componentRegistry.each<DefaultJvmLibrary> {
                initialize { library ->
                    library.module.name.convention(toModuleName(project.name))
                    library.targetJvmVersion.convention(Versions.libs.jvm.version)

                    val kotlin = target.kotlin
                    kotlin.jvmToolchain {
                        it.languageVersion.convention(library.targetJvmVersion.map { JavaLanguageVersion.of(it) })
                    }
                    // Register the target with the Kotlin plugin during configuration, so that tasks are created
                    kotlin.jvm()
                }

                derive { component ->
                    val jvmTarget = target.kotlin.jvm()
                    val apiConfig = configurations.getByName("jvmCompileClasspath")
                    val runtimeClasspath = configurations.getByName("jvmRuntimeClasspath")

                    val apiClasspath = configurations.create("apiClasspath")
                    apiClasspath.extendsFrom(apiConfig)

                    val compilation = jvmTarget.compilations.first()

                    val classesDir = files(compilation.compileTaskProvider.flatMap { (it as KotlinJvmCompile).destinationDirectory })

                    val moduleInfoCp = extensions.getByType(JvmModuleRegistry::class.java)
                        .inspectClassPathsFor(component.module, null, classesDir, apiClasspath, runtimeClasspath)
                        .moduleInfoClasspath

                    tasks.named(jvmTarget.artifactsTaskName, Jar::class.java) { task ->
                        task.from(moduleInfoCp)
                    }

                    jvmTarget.testRuns.configureEach { testRun ->
                        // Run tests in parallel
                        testRun.executionTask.configure { jvmTest ->
                            jvmTest.maxParallelForks = max(1, Runtime.getRuntime().availableProcessors() / 3)
                        }
                    }
                }
            }

            componentRegistry.each<DefaultBrowserLibrary> {
                derive { _ ->
                    kotlin.js {
                        browser {
                            testTask { task ->
                                task.useMocha()
                            }
                        }
                    }
                }
            }

            val lib = extensions.create(
                MultiPlatformLibrary::class.java,
                "library",
                DefaultMultiPlatformLibrary::class.java,
                multiplatformComponents,
                objects,
                componentRegistry.factory
            ) as DefaultMultiPlatformLibrary
            componentRegistry.register(lib)
        }
    }
}