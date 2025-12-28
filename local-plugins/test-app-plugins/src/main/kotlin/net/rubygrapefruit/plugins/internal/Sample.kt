package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.machine.info.Architecture.Arm64
import net.rubygrapefruit.machine.info.Machine

sealed interface Sample {
    val name: String
}

sealed interface Lib : Sample

class JvmLib internal constructor(override val name: String) : Lib

class KmpLib internal constructor(override val name: String) : Lib

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

class JvmCliApp internal constructor(override val name: String) : CliApp {
    override val distribution = CliAppDistribution("dist")

    override val otherDistributions: List<CliAppDistribution>
        get() = emptyList()
}

class NativeCliApp internal constructor(override val name: String) : CliApp {
    override val distribution = CliAppDistribution("dist")

    override val otherDistributions: List<CliAppDistribution> = nativeDistributions { CliAppDistribution(it) }
}

sealed interface UiApp : App

class JvmUiApp internal constructor(override val name: String) : UiApp {
    override val distribution = UiAppDistribution("dist")

    override val otherDistributions: List<UiAppDistribution>
        get() = emptyList()
}

class NativeUiApp internal constructor(override val name: String) : UiApp {
    override val distribution = UiAppDistribution("dist")

    override val otherDistributions: List<UiAppDistribution> = nativeDistributions { UiAppDistribution(it) }
}

private fun <T : AppDistribution> nativeDistributions(factory: (String) -> T): List<T> {
    val host = Machine.thisMachine
    return if (host.isMacOS && host.architecture == Arm64) {
        listOf(factory("macosX64DebugDist"))
    } else {
        emptyList()
    }
}