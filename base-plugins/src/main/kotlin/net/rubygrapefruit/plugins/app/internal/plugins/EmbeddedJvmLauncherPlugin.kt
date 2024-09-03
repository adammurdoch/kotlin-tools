package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.DefaultHasEmbeddedJvmAndLauncherScriptsDistribution
import net.rubygrapefruit.plugins.app.internal.HasEmbeddedJvm
import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.MutableJvmApplication
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.tasks.EmbeddedJvmLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaToolchainService
import kotlin.io.path.pathString

open class EmbeddedJvmLauncherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<MutableJvmApplication> { app ->

                val embeddedJvmTask = tasks.register("embeddedJvm", EmbeddedJvmLauncher::class.java) { t ->
                    t.imageDirectory.set(layout.buildDirectory.dir("embedded-jvm"))
                    t.module.set(app.module.name)
                    t.modulePath.from(app.runtimeModulePath)
                    val toolchainService = extensions.getByType(JavaToolchainService::class.java)
                    val javaExtension = extensions.getByType(JavaPluginExtension::class.java)
                    val launcher = toolchainService.launcherFor(javaExtension.toolchain)
                    t.jlinkPath.set(launcher.map { it.executablePath.asFile.toPath().parent.resolve("jlink").pathString })
                }

                // Don't add the target if Kotlin cannot be built for this host.
                // Should instead add distribution for each target
                if (HostMachine.current.canBeBuilt) {
                    // TODO - the target machine is not necessarily the host machine; it depends on the JVM being used above
                    app.distributionContainer.add(
                        "embeddedJvm",
                        true,
                        true,
                        HostMachine.current.machine,
                        BuildType.Release,
                        DefaultHasEmbeddedJvmAndLauncherScriptsDistribution::class.java
                    )
                }

                val jvmPathInDistribution = "jvm"
                app.distributionContainer.eachOfType<HasEmbeddedJvm> {
                    withImage {
                        includeDir(jvmPathInDistribution, embeddedJvmTask.flatMap { task -> task.imageDirectory })
                    }
                    javaLauncherPath.set("$jvmPathInDistribution/bin/java")
                }
            }
        }
    }
}