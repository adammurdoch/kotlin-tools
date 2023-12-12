package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Application
import org.gradle.api.Project

open class ApplicationRegistry(private val project: Project) {
    private var main: MutableApplication? = null
    private val whenAppSet = mutableListOf<Project.(Application) -> Unit>()

    fun register(app: MutableApplication) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple applications in the same project is not implemented.")
        }
        main = app

        app.appName.convention(project.name)

        for (builder in whenAppSet) {
            builder(project, app)
        }
        whenAppSet.clear()

        app.distributionContainer.each { dist ->
            dist.imageDirectory.convention(project.layout.buildDirectory.dir(dist.name("dist-image")))
            dist.launcherFilePath.convention(app.appName)

            val distTask = dist.distTask
            distTask.configure { t ->
                t.onlyIf {
                    dist.canBuildForHostMachine
                }
                t.description = "Builds the distribution image"
                t.group = "Distribution"
                t.imageDirectory.set(dist.imageDirectory)
                t.rootDirPath.set(".")
            }
            dist.imageOutputDirectory.set(distTask.flatMap { t -> t.imageDirectory })
            dist.launcherOutputFile.set(distTask.flatMap { t -> t.imageDirectory.map { it.file(dist.launcherFilePath.get()) } })

            distTask.configure {
                it.includeFile(dist.launcherFilePath, dist.launcherFile)
            }
        }
    }

    fun applyToApp(builder: Project.(Application) -> Unit) {
        val main = this.main
        if (main != null) {
            builder(project, main)
        } else {
            whenAppSet.add(builder)
        }
    }

    inline fun <reified T : Application> withApp(crossinline builder: Project.(T) -> Unit) {
        applyToApp { app ->
            if (app is T) {
                builder(project, app)
            }
        }
    }
}