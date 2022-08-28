package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.EmbeddedJvmLauncher
import net.rubygrapefruit.app.tasks.LauncherScript
import org.gradle.api.Plugin
import org.gradle.api.Project

open class EmbeddedJvmLauncherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withJvmApp { app ->
                val embeddedJvmTask = tasks.register("embeddedJvm", EmbeddedJvmLauncher::class.java) { t ->
                    t.imageDirectory.set(layout.buildDirectory.dir("embedded-jvm"))
                    t.module.set(app.module)
                    t.modulePath.from(app.outputModulePath)
                }
                app.distribution.content.from(embeddedJvmTask.flatMap { t -> t.imageDirectory })
                app.distribution.libraries.setFrom()

                tasks.named("launcherScript", LauncherScript::class.java) {
                    it.javaCommand.set("bin/java")
                    it.modulePath.empty()
                }
            }
        }
    }
}