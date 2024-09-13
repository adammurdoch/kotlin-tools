package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface MutableDistribution : Distribution {
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

    val effectiveLauncherFilePath: Provider<String>

    val imageOutputDirectory: DirectoryProperty

    val imageBaseDir: Provider<String>

    fun taskName(baseName: String): String

    fun buildDirName(baseName: String): String
}