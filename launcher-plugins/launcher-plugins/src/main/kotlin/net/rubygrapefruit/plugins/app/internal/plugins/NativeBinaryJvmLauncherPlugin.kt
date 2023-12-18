package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.JvmApplicationWithNativeBinary
import net.rubygrapefruit.plugins.app.internal.MutableJvmApplication
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.tasks.NativeBinary
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
                    it.javaVersion.set(app.targetJavaVersion)
                    it.modulePath.from(app.runtimeModulePath)
                }

                app.distributionContainer.each { dist ->
                    dist.launcherFilePath.set(app.appName.map { HostMachine.current.exeName(it) })
                    dist.launcherFile.set(binaryTask.flatMap { it.launcherFile.map { layout.projectDirectory.file(HostMachine.current.exeName(it.asFile.absolutePath)) } })
                }
            }
        }
    }
}