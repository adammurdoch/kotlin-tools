package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Installation
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider

abstract class DefaultMutableInstallation : MutableInstallation {
    /**
     * The location to install the launcher file.
     */
    abstract val launcherFile: RegularFileProperty

    override val outputs: Installation.Outputs = object : Installation.Outputs {
        override val launcherFile: Provider<RegularFile>
            get() = launcherOutputFile

        override val imageDirectory: Provider<Directory>
            get() = imageOutputDirectory
    }
}