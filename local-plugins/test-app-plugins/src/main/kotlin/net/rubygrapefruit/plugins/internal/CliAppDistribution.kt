package net.rubygrapefruit.plugins.internal

import org.gradle.internal.extensions.stdlib.capitalized
import java.nio.file.Path

sealed interface AppDistribution {
    val distTask: String

    val distDir: Path
}

class CliAppDistribution(
    override val distTask: String,
    override val distDir: Path,
    val invocation: CliAppInvocation
) : AppDistribution

class UiAppDistribution(
    override val distTask: String,
    override val distDir: Path,
    val launcher: Path
) : AppDistribution {
    companion object {
        fun of(name: String, distTask: String, sampleDir: Path, launcher: String?): UiAppDistribution {
            val binName = (launcher ?: name).capitalized()
            return UiAppDistribution(distTask, sampleDir, sampleDir.resolve("${binName}.app/Contents/MacOS/$binName"))
        }
    }
}
