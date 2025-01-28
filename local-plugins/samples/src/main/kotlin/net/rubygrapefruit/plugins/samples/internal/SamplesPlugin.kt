package net.rubygrapefruit.plugins.samples.internal

import net.rubygrapefruit.plugins.lifecycle.ComponentDetails
import net.rubygrapefruit.plugins.lifecycle.internal.ComponentLifecyclePlugin
import net.rubygrapefruit.plugins.samples.SamplesExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

abstract class SamplesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(ComponentLifecyclePlugin::class.java)

            val component = extensions.getByType(ComponentDetails::class.java)

            val model = extensions.create("samples", SamplesExtension::class.java)
            model.samplesDirectory.convention(layout.projectDirectory.dir("src/samples"))

            samplesVariant("samples", "samples", model) { t ->
                t.coordinates.set(component.releaseCoordinates)
            }
            samplesVariant("localSamples", "local-samples", model) { t ->
                t.coordinates.set(component.targetCoordinates)
                t.dependsOn(component.repository)
                t.repositoryPath.set(component.repository.elements.map { it.first().asFile.absolutePath })
            }
        }
    }

    private fun Project.samplesVariant(
        taskName: String,
        dirName: String,
        model: SamplesExtension,
        builder: (GenerateSamples) -> Unit = {}
    ): TaskProvider<VerifySamples> {
        val samples = tasks.register(taskName, GenerateSamples::class.java) { t ->
            t.sourceDirectory.set(model.samplesDirectory)
            t.outputDirectory.set(layout.buildDirectory.dir(dirName))
            t.manifest.set(layout.buildDirectory.file("${dirName}-manifest.txt"))
            builder(t)
        }
        return tasks.register("verify${taskName.capitalize()}", VerifySamples::class.java) { t ->
            t.manifest.set(samples.flatMap { it.manifest })
        }
    }
}