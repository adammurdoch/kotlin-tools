package net.rubygrapefruit.plugins.release.internal

import net.rubygrapefruit.plugins.lifecycle.VersionNumber
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

internal abstract class UpdateVersion : DefaultTask() {
    @get:Input
    abstract val nextVersion: Property<VersionNumber>

    @get:Internal
    abstract val buildFile: RegularFileProperty

    @TaskAction
    fun update() {
        println("Next version: ${nextVersion.get()}")
        val file = buildFile.get().asFile
        val text = file.readText()
        val pattern = Regex("nextVersion\\s*=\\s*\"([^\"]+)\"")
        val match = pattern.find(text)
        val updatedText = if (match != null) {
            text.replaceRange(match.groups[1]!!.range, nextVersion.get().toString())
        } else {
            text + """

                release {
                    nextVersion = "${nextVersion.get()}"
                }

            """.trimIndent()
        }
        file.writeText(updatedText)
    }
}