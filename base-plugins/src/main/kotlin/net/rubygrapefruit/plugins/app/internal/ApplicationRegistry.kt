package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.internal.tasks.Distributions
import net.rubygrapefruit.plugins.app.internal.tasks.ShowDistributions
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

        project.tasks.register("dist", Distributions::class.java) { task ->
            task.defaultDistribution.set(app.distribution.map { it as BuildableDistribution })
            task.allDistributions.set(app.distributions.map { it.filterIsInstance<BuildableDistribution>() })
        }

        app.distributionContainer.each { dist ->
            dist.imageDirectory.convention(project.layout.buildDirectory.dir(dist.imageBaseDir))
            dist.launcherFilePath.convention(app.appName)
            dist.rootDirPath.convention(".")

            val distImageTask = dist.distTask
            distImageTask.configure { t ->
                t.onlyIf {
                    dist.canBuildOnHostMachine
                }
                t.description = "Builds the distribution image"
                t.group = "Distribution"
                t.imageDirectory.set(dist.imageDirectory)
                t.rootDirPath.set(dist.rootDirPath)
                t.includeFile(dist.launcherFilePath, dist.launcherFile)
            }
            dist.imageOutputDirectory.set(distImageTask.flatMap { t -> t.imageDirectory })
            dist.launcherOutputFile.set(distImageTask.flatMap { t -> t.imageDirectory.map { it.file(dist.effectiveLauncherFilePath.get()) } })
        }

        project.tasks.register("showDistributions", ShowDistributions::class.java) { task ->
            task.app.set(app)
        }

        for (builder in whenAppSet) {
            builder(project, app)
        }
        whenAppSet.clear()
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