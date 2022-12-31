package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.JvmApplicationWithEmbeddedJvm
import net.rubygrapefruit.app.internal.MutableJvmApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.EmbeddedJvmLauncher
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
                app.packaging = JvmApplicationWithEmbeddedJvm()

                val embeddedJvmTask = tasks.register("embeddedJvm", EmbeddedJvmLauncher::class.java) { t ->
                    t.imageDirectory.set(layout.buildDirectory.dir("embedded-jvm"))
                    t.module.set(app.module.name)
                    t.modulePath.from(app.runtimeModulePath)
                    val toolchainService = extensions.getByType(JavaToolchainService::class.java)
                    val javaExtension = extensions.getByType(JavaPluginExtension::class.java)
                    val launcher = toolchainService.launcherFor(javaExtension.toolchain)
                    t.jlinkPath.set(launcher.map { it.executablePath.asFile.toPath().parent.resolve("jlink").pathString })
                }

                val jvmDir = "jvm"
                app.distribution.javaLauncherPath.set("$jvmDir/bin/java")

                applications.applyToDistribution { dist ->
                    dist.includeDir(jvmDir, embeddedJvmTask.flatMap { e -> e.imageDirectory })
                }
            }
        }
    }
}