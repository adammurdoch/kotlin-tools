package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.EmbeddedJvmLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project

open class EmbeddedJvmLauncherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withJvmApp { app ->
                val embeddedJvmTask = tasks.register("embeddedJvm", EmbeddedJvmLauncher::class.java) { t ->
                    t.imageDirectory.set(layout.buildDirectory.dir("embedded-jvm"))
                    t.launcherName.set(app.appName)
                    t.module.set(app.module)
                    t.mainClass.set(app.mainClass)
                    t.modulePath.from(app.distribution.libraries)
                }
                app.distribution.launcherFile.set(embeddedJvmTask.flatMap { t -> t.imageDirectory.map { it.file("bin/${t.launcherName.get()}") } })
            }
        }
    }
}