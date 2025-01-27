package net.rubygrapefruit.plugins.samples.internal

import net.rubygrapefruit.plugins.app.Versions
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

abstract class GenerateSamples : DefaultTask() {
    @get:Input
    abstract val coordinates: Property<String>

    @get:InputFiles
    abstract val sourceDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:OutputFile
    abstract val manifest: RegularFileProperty

    @TaskAction
    fun generate() {
        val outputDirectory = outputDirectory.get().asFile
        outputDirectory.deleteRecursively()

        val samples = mutableListOf<File>()

        for (file in sourceDirectory.get().asFile.listFiles()) {
            if (file.isDirectory) {
                println("Generating sample '${file.name}'")
                val sampleDestDir = outputDirectory.resolve(file.name)
                sampleDestDir.mkdirs()
                sampleDestDir.resolve("settings.gradle.kts").writeText("")

                file.copyRecursively(sampleDestDir)

                val buildScript = sampleDestDir.resolve("build.gradle.kts")
                val text = buildScript.readText()
                var updatedText = text.replace("samples.multiplatform()", """kotlin("multiplatform").version("${Versions.kotlin.version}")""")

                val multiplatform = updatedText != text
                if (multiplatform) {
                    updatedText = updatedText.replace(
                        "kotlin {", """
                        repositories {
                            mavenCentral()
                        }

                        kotlin {
                            targets {
                                jvm()
                                macosArm64 {
                                    binaries {
                                        executable()
                                    }
                                }
                            }
                    """.trimIndent()
                    )
                }
                updatedText = updatedText.replace("samples.coordinates()", """"${coordinates.get()}"""")

                buildScript.writeText(updatedText)

                samples.add(sampleDestDir)
            }
        }

        manifest.get().asFile.writeText(samples.joinToString("\n"))
    }
}