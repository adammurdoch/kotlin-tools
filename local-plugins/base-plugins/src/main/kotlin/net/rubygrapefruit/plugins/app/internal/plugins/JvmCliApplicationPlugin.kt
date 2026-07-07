package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.tasks.LauncherBashScript
import net.rubygrapefruit.plugins.app.internal.tasks.LauncherBatScript
import org.gradle.api.Plugin
import org.gradle.api.Project

private const val LIB_DIR_PATH = "lib"

@Suppress("unused")
class JvmCliApplicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            plugins.apply(JvmApplicationBasePlugin::class.java)
            plugins.apply(CliApplicationBasePlugin::class.java)

            componentRegistry.deriveFrom<DefaultJvmCliApplication> { app ->
                if (app.distributionContainer.empty) {
                    app.distributionContainer.add("noJvm", true, DefaultHasLauncherScriptsDistribution::class.java)
                }
                app.distributionContainer.each {
                    derive(this)
                }
            }
            componentRegistry.applyToProject<DefaultJvmCliApplication, HasLauncherScripts> { app, dist ->
                val libNames = app.runtimeModulePath.elements.map { it.map { f -> f.asFile.name } }

                // Generate the scripts per distribution, because each distribution may have a different `java` path
                val bashScript = tasks.register(dist.taskName("bashLauncher"), LauncherBashScript::class.java) {
                    it.scriptFile.set(layout.buildDirectory.file(dist.buildDirName("scripts") + "/launcher.sh"))
                    it.module.set(app.module.name)
                    it.mainClass.set(app.mainClass)
                    it.libsDirPath.set(LIB_DIR_PATH)
                    it.javaLauncherPath.set(dist.javaLauncherPath)
                    it.modulePath.set(libNames)
                }
                val batScript = tasks.register(dist.taskName("batLauncher"), LauncherBatScript::class.java) {
                    it.scriptFile.set(layout.buildDirectory.file(dist.buildDirName("scripts") + "/launcher.bat"))
                    it.module.set(app.module.name)
                    it.mainClass.set(app.mainClass)
                    it.libsDirPath.set(LIB_DIR_PATH)
                    it.javaLauncherPath.set(dist.javaLauncherPath)
                    it.modulePath.set(libNames)
                }

                dist.withImage {
                    includeFilesInDir(LIB_DIR_PATH, app.runtimeModulePath)
                }

                if (HostMachine.current is Windows) {
                    dist.launcherFile.set(batScript.flatMap { it.scriptFile })
                    dist.launcherFilePath.set(app.appName.map { "$it.bat" })
                    dist.withImage {
                        includeFile(app.appName, bashScript.flatMap { it.scriptFile })
                    }
                } else {
                    dist.launcherFile.set(bashScript.flatMap { it.scriptFile })
                    dist.withImage {
                        includeFile(app.appName.map { "$it.bat" }, batScript.flatMap { it.scriptFile })
                    }
                }
            }

            val app = extensions.create("application", DefaultJvmCliApplication::class.java)
            applications.register(app)
        }
    }
}