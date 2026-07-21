package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.component.ComponentRegistry
import net.rubygrapefruit.plugins.app.internal.tasks.Distributions
import net.rubygrapefruit.plugins.app.internal.tasks.ShowApplication
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyBasePlugin()

            repositories.mavenCentral()

            val componentRegistry = target.extensions.create("componentRegistry", ComponentRegistry::class.java)

            componentRegistry.each<MutableApplication> {
                initialize { app ->
                    app.appName.convention(project.name)
                }

                derive { app ->
                    project.tasks.register("dist", Distributions::class.java) { task ->
                        task.devDistribution.set(app.devDistribution.map { dist -> DefaultDistributionOutputs(dist.outputs.imageDirectory, dist.outputs.launcherFile) })
                        task.releaseDistribution.set(app.releaseDistribution.map { dist -> DefaultDistributionOutputs(dist.outputs.imageDirectory, dist.outputs.launcherFile) })
                        task.allDistributions.set(app.distributions.map { dists ->
                            dists.filterIsInstance<BuildableDistribution>().map { dist -> DefaultDistributionOutputs(dist.outputs.imageDirectory, dist.outputs.launcherFile) }
                        })
                    }
                    project.tasks.register("showApplication", ShowApplication::class.java) { task ->
                        task.app.set(project.provider { app.metadata() })
                    }
                }

                each<MutableDistribution> {
                    prepare { dist, app ->
                        val imageBaseDirName = app.distributionContainer.dev.map {
                            // This distribution is the development distribution
                            if (it == dist) {
                                "dist"
                            } else {
                                "dist-images/${dist.name}"
                            }
                        }.orElse("dist-images/${dist.name}")

                        dist.imageDirectory.convention(project.layout.buildDirectory.dir(imageBaseDirName))
                        dist.launcherFilePath.convention(app.appName)
                        dist.rootDirPath.convention(".")

                        dist.distTask.configure { t ->
                            val canBuild = dist.canBuildOnHostMachine
                            t.onlyIf {
                                canBuild
                            }
                            t.description = "Builds the distribution image"
                            t.group = "Distribution"
                            t.imageDirectory.set(dist.imageDirectory)
                            t.rootDirPath.set(dist.rootDirPath)
                        }

                        dist.imageOutputDirectory.set(dist.distTask.flatMap { t -> t.imageDirectory })
                        dist.launcherOutputFile.set(dist.distTask.flatMap { t -> t.imageDirectory.file(dist.effectiveLauncherFilePath) })
                    }
                }

                each<HasDistributionImage> {
                    prepare { dist, _ ->
                        dist.distTask.configure { t ->
                            t.includeFile(dist.launcherFilePath, dist.launcherFile)
                        }
                    }
                }
            }
        }
    }
}