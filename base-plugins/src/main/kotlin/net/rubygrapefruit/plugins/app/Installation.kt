package net.rubygrapefruit.plugins.app

import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * Represents an installation of an application.
 */
interface Installation {
    val outputs: Outputs

    interface Outputs {
        val imageDirectory: Provider<Directory>

        val launcherFile: Provider<RegularFile>
    }
}