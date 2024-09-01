package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.DefaultHasLauncherScriptsDistribution
import net.rubygrapefruit.plugins.app.internal.DefaultJvmCliApplication
import net.rubygrapefruit.plugins.app.internal.JvmApplicationWithLauncherScripts
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.tasks.LauncherBashScript
import net.rubygrapefruit.plugins.app.internal.tasks.LauncherBatScript
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(JvmApplicationBasePlugin::class.java)
            applications.withApp<DefaultJvmCliApplication> { app ->
                val libsDirPath = "lib"

                val libNames = app.runtimeModulePath.elements.map { it.map { f -> f.asFile.name } }

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

                app.distributionContainer.add("launcherScripts", true, BuildType.Release, DefaultHasLauncherScriptsDistribution::class.java)

                app.distributionContainer.each { dist ->
                    dist.launcherFile.set(launcherTask.flatMap { it.scriptFile })
                    dist.distTask.configure { distImage ->
                        if (app.packaging.includeRuntimeModules) {
                            distImage.includeFilesInDir(libsDirPath, app.runtimeModulePath)
                        }
                        if (app.packaging is JvmApplicationWithLauncherScripts) {
                            distImage.includeFile(app.appName.map { "$it.bat" }, launcherBatTask.flatMap { it.scriptFile })
                        }
                    }
                }
            }

            val app = extensions.create("application", DefaultJvmCliApplication::class.java)
            applications.register(app)
        }
    }
}