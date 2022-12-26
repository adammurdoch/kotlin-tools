package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.Application
import net.rubygrapefruit.app.tasks.DistributionImage
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

abstract class ApplicationRegistry(private val project: Project) {
    private var main: MutableApplication? = null
    private var mainDistTask: TaskProvider<DistributionImage>? = null
    private val whenAppSet = mutableListOf<Project.(Application) -> Unit>()

    fun register(app: MutableApplication) {
        if (main != null) {
            throw UnsupportedOperationException("Support for multiple applications in the same project is not implemented.")
        }
        main = app
        val dist = app.distribution

        app.appName.convention(project.name)
        dist.imageDirectory.convention(project.layout.buildDirectory.dir("dist-image"))
        dist.launcherFilePath.convention(app.appName)

        val distTask = project.tasks.register("dist", DistributionImage::class.java) { t ->
            t.imageDirectory.set(dist.imageDirectory)
            t.rootDirPath.set(".")
            t.includeFile(dist.launcherFilePath, dist.launcherFile)
        }
        dist.imageOutputDirectory.set(distTask.flatMap { t -> t.imageDirectory })
        dist.launcherOutputFile.set(distTask.flatMap { t -> t.imageDirectory.map { it.file(app.distribution.launcherFilePath.get()) } })
        mainDistTask = distTask

        for (builder in whenAppSet) {
            builder(project, app)
        }
        whenAppSet.clear()
    }

    fun applyToDistribution(builder: Project.(DistributionImage) -> Unit) {
        mainDistTask!!.configure {
            builder(project, it)
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