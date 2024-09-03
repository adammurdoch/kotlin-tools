package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.file.RegularFileProperty

interface MutableDistribution: Distribution {
    /**
     * The launcher file to copy into the distribution image.
     */
    val launcherFile: RegularFileProperty

    fun taskName(baseName: String): String

    fun buildDirName(baseName: String): String

    fun withImage(action: DistributionImage.() -> Unit)
}