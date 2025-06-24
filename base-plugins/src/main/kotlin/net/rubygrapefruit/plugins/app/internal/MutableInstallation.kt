package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Installation
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty

interface MutableInstallation : Installation {
    /**
     * Output from the installer task
     */
    val imageOutputDirectory: DirectoryProperty

    /**
     * Output from the installer task
     */
    val launcherOutputFile: RegularFileProperty
}