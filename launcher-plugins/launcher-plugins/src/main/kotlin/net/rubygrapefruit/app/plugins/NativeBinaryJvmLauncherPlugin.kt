package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.JvmApplicationWithNativeBinary
import net.rubygrapefruit.app.tasks.NativeBinary
import net.rubygrapefruit.plugins.app.internal.ApplicationBasePlugin
import net.rubygrapefruit.plugins.app.internal.MutableJvmApplication
import net.rubygrapefruit.plugins.app.internal.applications
import org.gradle.api.Plugin
import org.gradle.api.Project

class NativeBinaryJvmLauncherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<MutableJvmApplication> { app ->
                app.packaging = JvmApplicationWithNativeBinary()

                val binaryTask = tasks.register("native-binary", NativeBinary::class.java) {
                    it.launcherFile.set(layout.buildDirectory.file("native-binary/launcher"))
                    it.module.set(app.module.name)
                    it.mainClass.set(app.mainClass)
                    it.modulePath.from(app.runtimeModulePath)
                }
                app.distribution.launcherFile.set(binaryTask.flatMap { it.launcherFile })
            }
        }
    }
}