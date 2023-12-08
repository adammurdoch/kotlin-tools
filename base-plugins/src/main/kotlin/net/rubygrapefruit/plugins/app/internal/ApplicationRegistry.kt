package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.Project

open class ApplicationRegistry(private val project: Project) {
    private var main: MutableApplication? = null
    private val whenAppSet = mutableListOf<Project.(Application) -> Unit>()
    private var distConfigured = false
    private val applyToDist = mutableListOf<Project.(DistributionImage) -> Unit>()

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
            t.onlyIf {
                app.canBuildDistributionForHostMachine
            }
            t.imageDirectory.set(dist.imageDirectory)
            t.rootDirPath.set(".")
        }
        dist.imageOutputDirectory.set(distTask.flatMap { t -> t.imageDirectory })
        dist.launcherOutputFile.set(distTask.flatMap { t -> t.imageDirectory.map { it.file(app.distribution.launcherFilePath.get()) } })
        applyToDistribution {
            it.includeFile(dist.launcherFilePath, dist.launcherFile)
        }

        for (builder in whenAppSet) {
            builder(project, app)
        }
        whenAppSet.clear()

        distTask.configure {
            for (builder in applyToDist) {
                builder(project, it)
            }
            applyToDist.clear()
            distConfigured = true
        }
    }

    fun applyToDistribution(builder: Project.(DistributionImage) -> Unit) {
        require(!distConfigured)
        applyToDist.add(builder)
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