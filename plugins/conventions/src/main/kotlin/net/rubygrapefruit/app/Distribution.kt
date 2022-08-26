package net.rubygrapefruit.app

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty

interface Distribution {
    val imageDirectory: DirectoryProperty

    val launcherFile: RegularFileProperty
}