package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.NativeBinary
import org.gradle.api.Plugin
import org.gradle.api.Project

class NativeBinaryJvmLauncherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withJvmApp { app ->
                val binaryTask = tasks.register("native-binary", NativeBinary::class.java) {
                    it.launcherFile.set(layout.buildDirectory.file("native-binary/launcher"))
                }
                app.distribution.launcherFile.set(binaryTask.flatMap { it.launcherFile })
            }
        }
    }
}