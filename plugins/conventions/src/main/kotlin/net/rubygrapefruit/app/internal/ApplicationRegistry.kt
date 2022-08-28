package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.CliApplication
import net.rubygrapefruit.app.JvmCliApplication
import net.rubygrapefruit.app.tasks.DistributionImage
import org.gradle.api.Project

abstract class ApplicationRegistry(private val project: Project) {
    private var main: CliApplication? = null
    private val whenAppSet = mutableListOf<Project.(CliApplication) -> Unit>()

    fun register(app: CliApplication) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple applications in the same project is not implemented.")
        }
        main = app

        app.appName.convention(project.name)
        app.distribution.imageDirectory.convention(project.layout.buildDirectory.dir("dist-image"))

        val distTask = project.tasks.register("dist", DistributionImage::class.java) { t ->
            t.imageDirectory.set(app.distribution.imageDirectory)
            t.launcherFile.set(app.distribution.launcherFile)
            t.launcherName.set(app.appName)
            t.libraries.from(app.distribution.libraries)
            t.content.from(app.distribution.content)
        }

        app.distribution.launcherOutputFile.set(distTask.flatMap { t -> t.imageDirectory.map { it.file(t.launcherName.get()) } })

        for (builder in whenAppSet) {
            builder(project, app)
        }
        whenAppSet.clear()
    }

    fun withApp(builder: Project.(CliApplication) -> Unit) {
        val main = this.main
        if (main != null) {
            builder(project, main)
        } else {
            whenAppSet.add(builder)
        }
    }

    fun withJvmApp(builder: Project.(JvmCliApplication) -> Unit) {
        withApp { app ->
            if (app is JvmCliApplication) {
                builder(project, app)
            }
        }
    }
}