package net.rubygrapefruit.plugins.app

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

interface Distribution {
    /**
     * The directory to create the distribution image in.
     */
    val imageDirectory: DirectoryProperty

    /**
     * The final distribution image. You can use this to use the distribution from other tasks, e.g. an `install` or `zip` task
     */
    val imageOutputDirectory: Provider<Directory>

    /**
     * The launcher file in the distribution image. You can use this to use the launcher from other tasks, e.g. a `run` task.
     */
    val launcherOutputFile: Provider<RegularFile>
}
