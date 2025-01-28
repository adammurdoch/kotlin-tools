package net.rubygrapefruit.plugins.samples.internal

import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.testkit.runner.GradleRunner
import java.io.File

abstract class VerifySamples : DefaultTask() {
    @get:InputFile
    abstract val manifest: RegularFileProperty

    @TaskAction
    fun verify() {
        val samples = Json.decodeFromString<List<SampleDetails>>(manifest.get().asFile.readText())
        for (sample in samples) {
            println("Verifying sample '${sample.name}'")
            GradleRunner.create()
                .withProjectDir(File(sample.dir))
                .withArguments("build")
                .forwardOutput()
                .build()
        }
    }
}