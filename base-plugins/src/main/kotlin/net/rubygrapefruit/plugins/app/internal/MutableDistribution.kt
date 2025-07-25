package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface MutableDistribution : BuildableDistribution {
    val canBuildOnHostMachine: Boolean

    /**
     * The prefix to prepend to all paths in the distribution.
     */
    val rootDirPath: Property<String>

    /**
     * The launcher file to copy into the distribution image.
     */
    val launcherFile: RegularFileProperty

    /**
     * The destination location in the distribution image for the launcher file.
     */
    val launcherFilePath: Property<String>

    /**
     * The root dir + launcher file path
     */
    val effectiveLauncherFilePath: Provider<String>

    /**
     * Output from the dist task.
     */
    val imageOutputDirectory: DirectoryProperty

    /**
     * Output from the dist task.
     */
    val launcherOutputFile: RegularFileProperty

    fun taskName(baseName: String): String

    fun buildDirName(baseName: String): String
}