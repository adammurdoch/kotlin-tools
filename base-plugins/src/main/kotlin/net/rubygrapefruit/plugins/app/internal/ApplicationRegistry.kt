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
            task.defaultDistribution.set(app.distribution.map { dist -> DefaultDistributionOutputs(dist.outputs.imageDirectory, dist.outputs.launcherFile) })
            task.allDistributions.set(app.distributions.map { dists -> dists.filterIsInstance<BuildableDistribution>().map { dist -> DefaultDistributionOutputs(dist.outputs.imageDirectory, dist.outputs.launcherFile) } })
        }

        app.distributionContainer.each {
            val imageBaseDirName = app.distributionContainer.distribution.map {
                // This distribution is the development distribution
                if (this == it) {
                    "dist"
                } else {
                    "dist-images/$name"
                }
            }.orElse("dist-images/$name")

            imageDirectory.convention(project.layout.buildDirectory.dir(imageBaseDirName))
            launcherFilePath.convention(app.appName)
            rootDirPath.convention(".")

            distTask.configure { t ->
                val canBuild = canBuildOnHostMachine
                t.onlyIf {
                    canBuild
                }
                t.description = "Builds the distribution image"
                t.group = "Distribution"
                t.imageDirectory.set(imageDirectory)
                t.rootDirPath.set(rootDirPath)
            }

            imageOutputDirectory.set(distTask.flatMap { t -> t.imageDirectory })

            val launcherPath = effectiveLauncherFilePath
            launcherOutputFile.set(distTask.flatMap { t -> t.imageDirectory.map { it.file(launcherPath.get()) } })
        }
        app.distributionContainer.eachOfType<HasDistributionImage> {
            distTask.configure { t ->
                t.includeFile(launcherFilePath, launcherFile)
            }
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