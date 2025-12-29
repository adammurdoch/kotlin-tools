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

class JvmCliApp internal constructor(override val name: String, sampleDir: Path, launcher: String?, args: List<String>) : CliApp {
    override val distribution: CliAppDistribution

    override val otherDistributions: List<CliAppDistribution>
        get() = emptyList()

    init {
        val distDir = sampleDir.resolve("build/dist")
        distribution = CliAppDistribution(
            "dist",
            distDir,
            ScriptInvocation(distDir.resolve(launcher ?: name), args)
        )
    }
}

class NativeCliApp internal constructor(override val name: String, sampleDir: Path, launcher: String?, args: List<String>) : CliApp {
    override val distribution: CliAppDistribution

    override val otherDistributions: List<CliAppDistribution> = nativeDistributions(sampleDir) { distTask, distDir ->
        CliAppDistribution(distTask, distDir, BinaryInvocation(distDir.resolve(launcher ?: name), args))
    }

    init {
        val distDir = sampleDir.resolve("build/dist")
        distribution = CliAppDistribution(
            "dist",
            distDir,
            BinaryInvocation(distDir.resolve(launcher ?: name), args)
        )
    }
}

sealed interface UiApp : App

class JvmUiApp internal constructor(override val name: String, sampleDir: Path, launcher: String?) : UiApp {
    override val distribution = UiAppDistribution.of(name, "dist", sampleDir.resolve("build/dist"), launcher)

    override val otherDistributions: List<UiAppDistribution>
        get() = emptyList()
}

class NativeUiApp internal constructor(override val name: String, sampleDir: Path, launcher: String?) : UiApp {
    override val distribution = UiAppDistribution.of(name, "dist", sampleDir.resolve("build/dist"), launcher)

    override val otherDistributions: List<UiAppDistribution> = nativeDistributions(sampleDir) { distTask, distDir ->
        UiAppDistribution.of(name, distTask, distDir, launcher)
    }
}

private fun <T : AppDistribution> nativeDistributions(sampleDir: Path, factory: (String, Path) -> T): List<T> {
    val host = Machine.thisMachine
    return if (host.isMacOS && host.architecture == Arm64) {
        listOf(factory("macosX64DebugDist", sampleDir.resolve("build/dist-images/macosX64Debug")))
    } else {
        emptyList()
    }
}