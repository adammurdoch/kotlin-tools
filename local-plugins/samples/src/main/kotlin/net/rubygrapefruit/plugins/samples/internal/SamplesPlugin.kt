package net.rubygrapefruit.plugins.samples.internal

import net.rubygrapefruit.plugins.samples.SamplesExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class SamplesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val model = extensions.create("samples", SamplesExtension::class.java)
            model.samplesDirectory.convention(layout.projectDirectory.dir("src/samples"))

            tasks.create("samples", GenerateSamples::class.java) { t ->
                t.sourceDirectory.set(model.samplesDirectory)
                t.outputDirectory.set(layout.buildDirectory.dir("samples"))
            }
        }
    }
}