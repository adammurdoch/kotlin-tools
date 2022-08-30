package net.rubygrapefruit.app

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty

interface Distribution {
    val imageDirectory: DirectoryProperty

    /**
     * The launcher file to copy into the distribution image.
     */
    val launcherFile: RegularFileProperty

    /**
     * Additional files and directories to copy into the root of the distribution image.
     */
    val content: ConfigurableFileCollection

    /**
     * The launcher file in the distribution image. You can use this to use the launcher from other tasks, eg a `run` task.
     */
    val launcherOutputFile: RegularFileProperty
}
