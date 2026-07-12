package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.tasks.EmbeddedJvmLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.toolchain.JavaToolchainService
import kotlin.io.path.pathString

private const val JVM_PATH_IN_DISTRIBUTION = "jvm"

@Suppress("unused")
class EmbeddedJvmLauncherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)

            componentRegistry.each<DefaultJvmCliApplication> {
                each<JvmCliApplicationDist> {
                    prepare { dist, app ->
                        // TODO - the target machine is not necessarily the host machine; it depends on the JVM being used to run jlink
                        // Should add distribution for each target, which should be declared
                        val embeddedJvmDist = app.distributionContainer.add(
                            null,
                            true,
                            true,
                            HostMachine.current.canBeBuilt,
                            HostMachine.current.machine,
                            BuildType.Release,
                            DefaultHasEmbeddedJvmAndLauncherScriptsDistribution::class.java
                        )
                        dist.dist = embeddedJvmDist
                    }
                }
            }

            componentRegistry.each<MutableJvmApplication> {
                derive { app ->
                    val embeddedJvmTask = tasks.register("embeddedJvm", EmbeddedJvmLauncher::class.java) { t ->
                        t.imageDirectory.set(layout.buildDirectory.dir("embedded-jvm"))
                        t.module.set(app.module.name)
                        t.modulePath.from(app.runtimeModulePath)
                        val toolchainService = extensions.getByType(JavaToolchainService::class.java)
                        val javaExtension = extensions.getByType(JavaPluginExtension::class.java)
                        val launcher = toolchainService.launcherFor(javaExtension.toolchain)
                        t.jlinkPath.set(launcher.map { it.executablePath.asFile.toPath().parent.resolve("jlink").pathString })
                    }
                    register(EmbeddedJvm(embeddedJvmTask))
                }

                each<HasEmbeddedJvm> {
                    require<EmbeddedJvm> {
                        derive { dist, app, jvm ->
                            dist.withImage {
                                includeDir(JVM_PATH_IN_DISTRIBUTION, jvm.embeddedJvmTask.flatMap { task -> task.imageDirectory })
                            }
                            dist.javaLauncherPath.set("$JVM_PATH_IN_DISTRIBUTION/bin/java")
                        }
                    }
                }
            }
        }
    }
}

private class EmbeddedJvm(val embeddedJvmTask: TaskProvider<EmbeddedJvmLauncher>)
