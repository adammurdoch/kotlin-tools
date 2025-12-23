package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

internal class DefaultDistributionOutputs(
    override val imageDirectory: Provider<Directory>,
    override val launcherFile: Provider<RegularFile>
) : Distribution.Outputs
