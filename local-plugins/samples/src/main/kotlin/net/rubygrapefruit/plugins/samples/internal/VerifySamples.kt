package net.rubygrapefruit.plugins.samples.internal

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
        val samples = manifest.get().asFile.readLines().map { File(it) }
        for (sample in samples) {
            println("Verifying '$sample'")
            GradleRunner.create()
                .withProjectDir(sample)
                .withArguments("build")
                .forwardOutput()
                .build()
        }
    }
}