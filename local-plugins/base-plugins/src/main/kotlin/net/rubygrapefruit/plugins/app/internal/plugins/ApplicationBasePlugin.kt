package net.rubygrapefruit.plugins.app.internal.plugins

import com.google.devtools.ksp.gradle.KspAATask
import kotlinx.serialization.json.Json
import net.rubygrapefruit.plugins.app.Versions
import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.component.ComponentRegistry
import net.rubygrapefruit.plugins.app.internal.tasks.Distributions
import net.rubygrapefruit.plugins.app.internal.tasks.ShowApplication
import net.rubygrapefruit.plugins.app.metadata.MainFunction
import net.rubygrapefruit.plugins.app.metadata.Names
import org.gradle.api.Plugin
import org.gradle.api.Project

class ApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyBasePlugin()
            plugins.apply("com.google.devtools.ksp")

            repositories.mavenCentral()

            val componentRegistry = target.extensions.create("componentRegistry", ComponentRegistry::class.java)

            componentRegistry.each<HasMainFunction> {
                initialize { function ->
                    // This has to be done eagerly
                    dependencies.add(function.kspConfigurationName, Versions.libs.coordinates("source-processor"))
                }
                derive { function ->
                    val kspTask = tasks.named(function.kspTaskName, KspAATask::class.java)
                    val mainFunction = kspTask.flatMap {
                        it.kspConfig.resourceOutputDir.map {
                            val metadataFile = it.file(Names.metadataFileName + ".json").asFile
                            val functions = Json.decodeFromString<List<MainFunction>>(metadataFile.readText())
                            if (functions.size == 1) {
                                functions.first()
                            } else {
                                throw IllegalStateException("Did not find exactly one main() function")
                            }
                        }
                    }
                    registerSibling(ApplicationMainFunction(mainFunction))
                }
            }

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