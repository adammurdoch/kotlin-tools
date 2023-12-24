package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import java.io.File

class LibraryInfo(
    @get:Input
    val module: String,
    @get:InputFile
    val file: File
)