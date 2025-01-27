package net.rubygrapefruit.plugins.samples.internal

import net.rubygrapefruit.plugins.samples.SamplesExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class SamplesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            val model = extensions.create("samples", SamplesExtension::class.java)
            model.samplesDirectory.convention(layout.projectDirectory.dir("src/samples"))

            val samples = tasks.register("samples", GenerateSamples::class.java) { t ->
                t.sourceDirectory.set(model.samplesDirectory)
                t.outputDirectory.set(layout.buildDirectory.dir("samples"))
                t.manifest.set(layout.buildDirectory.file("samples-manifest.txt"))
            }
            tasks.register("verifySamples", VerifySamples::class.java) { t ->
                t.manifest.set(samples.flatMap { it.manifest })
            }
        }
    }
}