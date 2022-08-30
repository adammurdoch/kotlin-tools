package net.rubygrapefruit.app

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface Distribution {
    val imageDirectory: DirectoryProperty

    /**
     * The launcher file to copy into the distribution image.
     */
    val launcherFile: RegularFileProperty

    val launcherFilePath: Property<String>

    /**
     * The launcher file in the distribution image. You can use this to use the launcher from other tasks, eg a `run` task.
     */
    val launcherOutputFile: RegularFileProperty
}
