package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.*
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

                val dist = app.distributionContainer.add("noJvm", true, DefaultHasLauncherScriptsDistribution::class.java)
                dist.withImage {
                    includeFilesInDir(libsDirPath, app.runtimeModulePath)
                }

                app.distributionContainer.eachOfType<HasLauncherScripts> {
                    // Generate the scripts per distribution, because each distribution may have a different `java` path
                    val bashScript = tasks.register(taskName("bashLauncher"), LauncherBashScript::class.java) {
                        it.scriptFile.set(layout.buildDirectory.file(buildDirName("scripts") + "/launcher.sh"))
                        it.module.set(app.module.name)
                        it.mainClass.set(app.mainClass)
                        it.libsDirPath.set(libsDirPath)
                        it.javaLauncherPath.set(javaLauncherPath)
                        it.modulePath.set(libNames)
                    }
                    val batScript = tasks.register(taskName("batLauncher"), LauncherBatScript::class.java) {
                        it.scriptFile.set(layout.buildDirectory.file(buildDirName("scripts") + "/launcher.bat"))
                        it.module.set(app.module.name)
                        it.mainClass.set(app.mainClass)
                        it.libsDirPath.set(libsDirPath)
                        it.javaLauncherPath.set(javaLauncherPath)
                        it.modulePath.set(libNames)
                    }

                    if (HostMachine.current is Windows) {
                        launcherFile.set(batScript.flatMap { it.scriptFile })
                        launcherFilePath.set(app.appName.map { "$it.bat" })
                        withImage {
                            includeFile(app.appName, bashScript.flatMap { it.scriptFile })
                        }
                    } else {
                        launcherFile.set(bashScript.flatMap { it.scriptFile })
                        withImage {
                            includeFile(app.appName.map { "$it.bat" }, batScript.flatMap { it.scriptFile })
                        }
                    }
                }
            }

            val app = extensions.create("application", DefaultJvmCliApplication::class.java)
            applications.register(app)
            project.jvmKotlin.sourceSets.getByName("main").kotlin.srcDirs(app.kotlin)
        }
    }
}