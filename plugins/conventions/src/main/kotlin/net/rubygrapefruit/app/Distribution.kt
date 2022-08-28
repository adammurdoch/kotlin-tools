package net.rubygrapefruit.app

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface Distribution {
    val imageDirectory: DirectoryProperty

    val launcherFile: RegularFileProperty

    val launcherDirectory: DirectoryProperty

    val launcherFilePath: Property<String>

    val libraries: ConfigurableFileCollection

    val launcherOutputFile: RegularFileProperty
}
