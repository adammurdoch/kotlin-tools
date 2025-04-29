package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Installation
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider

abstract class DefaultMutableInstallation : Installation {
    abstract val imageDirectory: DirectoryProperty

    abstract val launcherFile: RegularFileProperty
    abstract val launcherOutputFile: RegularFileProperty

    override val outputs: Installation.Outputs = object : Installation.Outputs {
        override val launcherFile: Provider<RegularFile>
            get() = launcherOutputFile
    }
}