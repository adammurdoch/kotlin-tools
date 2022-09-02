package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.JvmApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.EmbeddedJvmLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project

open class EmbeddedJvmLauncherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<JvmApplication> { app ->
                val embeddedJvmTask = tasks.register("embeddedJvm", EmbeddedJvmLauncher::class.java) { t ->
                    t.imageDirectory.set(layout.buildDirectory.dir("embedded-jvm"))
                    t.module.set(app.module)
                    t.modulePath.from(app.outputModulePath)
                }

                val jvmDir = "jvm"
                app.distribution.javaLauncherPath.set("$jvmDir/bin/java")
                app.distribution.modulePath.setFrom()
                app.distribution.modulePathNames.empty()

                applications.applyToDistribution { t ->
                    t.includeDir(jvmDir, embeddedJvmTask.flatMap { e -> e.imageDirectory })
                }
            }
        }
    }
}