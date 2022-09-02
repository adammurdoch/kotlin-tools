package net.rubygrapefruit.app

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface Distribution {
    /**
     * The directory to create the distribution image in.
     */
    val imageDirectory: DirectoryProperty

    /**
     * The launcher file to copy into the distribution image.
     */
    val launcherFile: RegularFileProperty

    /**
     * The location in the distribution image to copy the launcher file to.
     */
    val launcherFilePath: Property<String>

    /**
     * The final distribution image. You can use this to use the distribution from other tasks, eg an `install` or `zip` task
     */
    val imageOutputDirectory: Provider<Directory>

    /**
     * The launcher file in the distribution image. You can use this to use the launcher from other tasks, eg a `run` task.
     */
    val launcherOutputFile: Provider<RegularFile>
}
