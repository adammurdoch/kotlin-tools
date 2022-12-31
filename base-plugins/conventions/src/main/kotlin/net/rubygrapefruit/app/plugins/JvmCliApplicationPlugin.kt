package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultJvmCliApplication
import net.rubygrapefruit.app.internal.JvmApplicationWithLauncherScripts
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.LauncherBashScript
import net.rubygrapefruit.app.tasks.LauncherBatScript
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(JvmApplicationBasePlugin::class.java)
            applications.withApp<DefaultJvmCliApplication> { app ->
                val libsDirPath = "lib"
                val libNames = objects.listProperty(String::class.java)

                val launcherTask = tasks.register("launcherScript", LauncherBashScript::class.java) {
                    it.scriptFile.set(layout.buildDirectory.file("app/launcher.sh"))
                    it.module.set(app.module.name)
                    it.mainClass.set(app.mainClass)
                    it.libsDirPath.set(libsDirPath)
                    it.javaLauncherPath.set(app.javaLauncherPath)
                    it.modulePath.set(libNames)
                }
                val launcherBatTask = tasks.register("launcherBatScript", LauncherBatScript::class.java) {
                    it.scriptFile.set(layout.buildDirectory.file("app/launcher.bat"))
                    it.module.set(app.module.name)
                    it.mainClass.set(app.mainClass)
                    it.libsDirPath.set(libsDirPath)
                    it.javaLauncherPath.set(app.javaLauncherPath)
                    it.modulePath.set(libNames)
                }

                applications.applyToDistribution { dist ->
                    if (app.packaging.includeRuntimeModules) {
                        dist.includeFilesInDir(libsDirPath, app.runtimeModulePath)
                        libNames.set(app.runtimeModulePath.elements.map { it.map { f -> f.asFile.name } })
                    }
                    if (app.packaging is JvmApplicationWithLauncherScripts) {
                        dist.includeFile(app.appName.map { "$it.bat" }, launcherBatTask.flatMap { it.scriptFile })
                    }
                }

                app.distribution.launcherFile.set(launcherTask.flatMap { it.scriptFile })
            }

            val app = extensions.create("application", DefaultJvmCliApplication::class.java)
            applications.register(app)
        }
    }
}