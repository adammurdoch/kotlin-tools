package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Installation
import org.gradle.api.file.RegularFileProperty

interface MutableInstallation : Installation {
    val launcherFile: RegularFileProperty
}