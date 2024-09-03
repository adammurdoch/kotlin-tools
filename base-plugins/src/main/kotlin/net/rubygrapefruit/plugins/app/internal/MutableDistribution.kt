package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface MutableDistribution: Distribution {
    /**
     * The launcher file to copy into the distribution image.
     */
    val launcherFile: RegularFileProperty

    /**
     * The destination location in the distribution image for the launcher file.
     */
    val launcherFilePath: Property<String>

    fun taskName(baseName: String): String

    fun buildDirName(baseName: String): String

    fun withImage(action: DistributionImage.() -> Unit)
}