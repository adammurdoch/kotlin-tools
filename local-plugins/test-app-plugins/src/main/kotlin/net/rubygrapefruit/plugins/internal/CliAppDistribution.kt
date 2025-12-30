package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.machine.info.Architecture
import org.gradle.internal.extensions.stdlib.capitalized
import java.nio.file.Path

sealed interface AppDistribution {
    val distTask: String

    val distDir: Path

    val binaries: Binaries?

    class Binaries(val architecture: Architecture, val binaries: List<Path>)
}

class CliAppDistribution(
    override val distTask: String,
    override val distDir: Path,
    override val binaries: AppDistribution.Binaries?,
    val invocation: CliAppInvocation
) : AppDistribution {
    companion object {
        fun ofBinary(
            name: String,
            distTask: String,
            distDir: Path,
            launcher: String?,
            args: List<String>,
            expectedOutput: String?,
            architecture: Architecture
        ): CliAppDistribution {
            val binary = distDir.resolve(launcher ?: name)
            return CliAppDistribution(distTask, distDir, AppDistribution.Binaries(architecture, listOf(binary)), BinaryInvocation(binary, args, expectedOutput))
        }
    }
}

class UiAppDistribution(
    override val distTask: String,
    override val distDir: Path,
    override val binaries: AppDistribution.Binaries,
    val launcher: Path
) : AppDistribution {
    companion object {
        fun of(name: String, distTask: String, sampleDir: Path, launcher: String?, architecture: Architecture): UiAppDistribution {
            val binName = (launcher ?: name).capitalized()
            val launcher = sampleDir.resolve("${binName}.app/Contents/MacOS/$binName")
            return UiAppDistribution(distTask, sampleDir, AppDistribution.Binaries(architecture, listOf(launcher)), launcher)
        }
    }
}
