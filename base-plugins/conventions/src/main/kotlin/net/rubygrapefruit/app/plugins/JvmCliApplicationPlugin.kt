package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultJvmApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.LauncherScript
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(JvmApplicationBasePlugin::class.java)
            applications.withApp<DefaultJvmApplication> { app ->
                val libsDirPath = "lib"

                val launcherTask = tasks.register("launcherScript", LauncherScript::class.java) {
                    it.scriptFile.set(layout.buildDirectory.file("app/launcher.sh"))
                    it.module.set(app.module)
                    it.mainClass.set(app.mainClass)
                    it.libsDirPath.set(libsDirPath)
                    it.javaLauncherPath.set(app.distribution.javaLauncherPath)
                    it.modulePath.addAll(app.distribution.modulePathNames)
                }

                applications.applyToDistribution { t ->
                    t.includeFilesInDir(libsDirPath, app.distribution.modulePath)
                }

                app.distribution.launcherFile.set(launcherTask.flatMap { it.scriptFile })
            }

            val app = extensions.create("application", DefaultJvmApplication::class.java)
            applications.register(app, app.distribution)
        }
    }
}