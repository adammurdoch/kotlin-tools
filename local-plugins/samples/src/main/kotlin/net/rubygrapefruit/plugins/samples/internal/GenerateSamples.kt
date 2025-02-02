package net.rubygrapefruit.plugins.samples.internal

import kotlinx.serialization.json.Json
import net.rubygrapefruit.plugins.app.Versions
import net.rubygrapefruit.plugins.lifecycle.Coordinates
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class GenerateSamples : DefaultTask() {
    @get:Input
    abstract val coordinates: Property<Coordinates>

    @get:Input
    @get:Optional
    abstract val repositoryPath: Property<String>

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

        val samples = mutableListOf<SampleDetails>()

        for (file in sourceDirectory.get().asFile.listFiles()) {
            if (file.isDirectory) {
                val sampleName = file.name.toString()
                println("Generating sample '$sampleName'")
                val sampleDestDir = outputDirectory.resolve(file.name)
                sampleDestDir.mkdirs()
                sampleDestDir.resolve("settings.gradle.kts").writeText("")

                file.copyRecursively(sampleDestDir)

                val buildScript = sampleDestDir.resolve("build.gradle.kts")
                val text = buildScript.readText()
                var updatedText = text.replace("samples.multiplatform()", """kotlin("multiplatform").version("${Versions.kotlin.version}")""")

                val localRepo = repositoryPath.orNull
                if (localRepo != null) {
                    updatedText = updatedText.replace(
                        "kotlin {", """
                        repositories {
                            maven {
                                url = file("$localRepo").toURI()
                            }
                        }
                        
                        kotlin {
                    """.trimIndent()
                    )
                }

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
                updatedText = updatedText.replace("samples.coordinates()", """"${coordinates.get().formatted}"""")

                buildScript.writeText(updatedText)

                val sample = SampleDetails(sampleDestDir.absolutePath, sampleName)
                samples.add(sample)
            }
        }

        val json = Json {
            prettyPrint = true
        }
        manifest.get().asFile.writeText(json.encodeToString(samples))
    }
}