package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.DefaultMutableInstallation
import net.rubygrapefruit.plugins.app.internal.MutableApplication
import net.rubygrapefruit.plugins.app.internal.MutableDistribution
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.tasks.InstallDistribution
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

open class CliApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<MutableApplication> { app ->
                val installation = objects.newInstance(DefaultMutableInstallation::class.java)
                app.localInstallation.set(installation)
                app.installations.add(app.localInstallation)

                val targetDirectory = File(System.getProperty("user.home"), "bin")
                installation.installDirectory.fileProvider(app.appName.map { name -> targetDirectory.resolve("images/$name") })
                installation.launcherFile.fileProvider(app.devDistribution.flatMap { dist ->
                    (dist as MutableDistribution).effectiveLauncherFilePath.map { path ->
                        targetDirectory.resolve("links/${path.substringAfterLast("/")}")
                    }
                })

                val install = tasks.register("install", InstallDistribution::class.java) { task ->
                    task.description = "Installs the application"
                    task.devDistribution.set(app.devDistribution.map { it.outputs })
                    task.releaseDistribution.set(app.releaseDistribution.map { it.outputs })
                    task.targetImageDirectory.set(installation.installDirectory)
                    task.targetLauncherLink.set(installation.launcherFile)
                }
                installation.imageOutputDirectory.set(install.flatMap { it.targetImageDirectory })
                installation.launcherOutputFile.set(install.flatMap { it.targetLauncherLink })
            }
        }
    }
}