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
            val invocation = BinaryInvocation.of(name, distDir, launcher, args, expectedOutput)
            return CliAppDistribution(distTask, distDir, AppDistribution.Binaries(architecture, listOf(invocation.binary)), invocation)
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
        fun of(name: String, distTask: String, distDir: Path, launcher: String?, architecture: Architecture, otherBinaries: List<String> = emptyList()): UiAppDistribution {
            val binName = (launcher ?: name).capitalized()
            val contentsDir = distDir.resolve("${binName}.app/Contents")
            val launcher = contentsDir.resolve("MacOS/$binName")
            return UiAppDistribution(
                distTask,
                distDir,
                AppDistribution.Binaries(architecture, listOf(launcher) + otherBinaries.map { contentsDir.resolve(it) }),
                launcher
            )
        }
    }
}
