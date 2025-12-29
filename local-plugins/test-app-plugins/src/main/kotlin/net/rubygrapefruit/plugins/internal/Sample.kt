package net.rubygrapefruit.plugins.internal

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

class JvmCliApp internal constructor(override val name: String, sampleDir: Path) : CliApp {
    override val distribution = CliAppDistribution("dist", sampleDir.resolve("build/dist"))

    override val otherDistributions: List<CliAppDistribution>
        get() = emptyList()
}

class NativeCliApp internal constructor(override val name: String, sampleDir: Path) : CliApp {
    override val distribution = CliAppDistribution("dist", sampleDir.resolve("build/dist"))

    override val otherDistributions: List<CliAppDistribution> = nativeDistributions(sampleDir, ::CliAppDistribution)
}

sealed interface UiApp : App

class JvmUiApp internal constructor(override val name: String, sampleDir: Path) : UiApp {
    override val distribution = UiAppDistribution("dist", sampleDir.resolve("build/dist"))

    override val otherDistributions: List<UiAppDistribution>
        get() = emptyList()
}

class NativeUiApp internal constructor(override val name: String, sampleDir: Path) : UiApp {
    override val distribution = UiAppDistribution("dist", sampleDir.resolve("build/dist"))

    override val otherDistributions: List<UiAppDistribution> = nativeDistributions(sampleDir, ::UiAppDistribution)
}

private fun <T : AppDistribution> nativeDistributions(sampleDir: Path, factory: (String, Path) -> T): List<T> {
    val host = Machine.thisMachine
    return if (host.isMacOS && host.architecture == Arm64) {
        listOf(factory("macosX64DebugDist", sampleDir.resolve("build/dist-images/macosX64Debug")))
    } else {
        emptyList()
    }
}