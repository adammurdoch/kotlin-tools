package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface HasLauncherExecutable : Distribution, HasTargetMachine {

    val buildType: BuildType

    /**
     * The launcher file to copy into the distribution image.
     */
    val launcherFile: RegularFileProperty

    /**
     * The location for the launcher file in the distribution image.
     */
    val launcherFilePath: Property<String>
}