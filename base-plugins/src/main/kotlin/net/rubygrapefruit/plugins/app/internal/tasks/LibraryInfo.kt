package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import java.io.File

class LibraryInfo(
    @get:Input
    val componentId: String,
    @get:InputFile
    val file: File
) {
    override fun toString(): String {
        return "{componentId: $componentId, file: ${file.name}}"
    }
}