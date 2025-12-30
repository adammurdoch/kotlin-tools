package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.machine.info.Architecture
import net.rubygrapefruit.machine.info.Architecture.Arm64
import net.rubygrapefruit.machine.info.Machine
import java.nio.file.Path

sealed interface Sample {
    val name: String
}

sealed interface Lib : Sample

class JvmLib internal constructor(override val name: String, sampleDir: Path) : Lib

class KmpLib internal constructor(override val name: String, sampleDir: Path) : Lib

sealed interface App : Sample {
    /**
     * The default distribution.
     */
    val distribution: AppDistribution

    /**
     * Other distributions to test.
     */
    val otherDistributions: List<AppDistribution>
}

sealed interface CliApp : App

class JvmCliApp internal constructor(
    override val name: String,
    override val distribution: CliAppDistribution
) : CliApp {
    override val otherDistributions: List<CliAppDistribution>
        get() = emptyList()
}

class NativeCliApp internal constructor(
    override val name: String,
    sampleDir: Path,
    launcher: String?,
    args: List<String>,
    expectedOutput: String?
) : CliApp {
    override val distribution: CliAppDistribution

    override val otherDistributions: List<CliAppDistribution> = nativeDistributions(sampleDir) { distTask, distDir, architecture ->
        CliAppDistribution.ofBinary(name, distTask, distDir, launcher, args, expectedOutput, architecture)
    }

    init {
        val distDir = sampleDir.resolve("build/dist")
        distribution = CliAppDistribution.ofBinary(name, "dist", distDir, launcher, args, expectedOutput, Machine.thisMachine.architecture)
    }
}

sealed interface UiApp : App

class JvmUiApp internal constructor(override val name: String, sampleDir: Path, launcher: String?) : UiApp {
    override val distribution = UiAppDistribution.of(
        name,
        "dist",
        sampleDir.resolve("build/dist"),
        launcher,
        Machine.thisMachine.architecture,
        listOf("jvm/bin/java")
    )

    override val otherDistributions: List<UiAppDistribution>
        get() = emptyList()
}

class NativeUiApp internal constructor(override val name: String, sampleDir: Path, launcher: String?) : UiApp {
    override val distribution = UiAppDistribution.of(name, "dist", sampleDir.resolve("build/dist"), launcher, Machine.thisMachine.architecture)

    override val otherDistributions: List<UiAppDistribution> = nativeDistributions(sampleDir) { distTask, distDir, architecture ->
        UiAppDistribution.of(name, distTask, distDir, launcher, architecture)
    }
}

private fun <T : AppDistribution> nativeDistributions(sampleDir: Path, factory: (String, Path, Architecture) -> T): List<T> {
    val host = Machine.thisMachine
    return if (host.isMacOS && host.architecture == Arm64) {
        listOf(factory("macosX64DebugDist", sampleDir.resolve("build/dist-images/macosX64Debug"), Architecture.X64))
    } else {
        emptyList()
    }
}