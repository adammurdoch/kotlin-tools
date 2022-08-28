package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.CliApplication
import net.rubygrapefruit.app.Distribution
import net.rubygrapefruit.app.JvmCliApplication
import net.rubygrapefruit.app.tasks.DistributionImage
import org.gradle.api.Project

internal abstract class ApplicationRegistry(private val project: Project) {
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
            t.launcherDirectory.set(app.distribution.launcherDirectory)
            t.launcherFilePath.set(app.distribution.launcherFilePath)
            t.launcherName.set(app.appName)
            t.libraries.from(app.distribution.libraries)
        }

        app.distribution.launcherOutputFile.set(distTask.flatMap { t -> t.imageDirectory.map { it.file(t.launcherName.get()) } })

        for (builder in whenAppSet) {
            builder(project, app)
        }
        whenAppSet.clear()
    }

    fun applyLauncherTo(distribution: Distribution, builder: Project.(Distribution) -> Unit) {
        distribution.launcherFile.set(project.provider { null })
        distribution.launcherDirectory.set(project.provider { null })
        distribution.launcherFilePath.set(project.provider { null })
        builder(project, distribution)
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