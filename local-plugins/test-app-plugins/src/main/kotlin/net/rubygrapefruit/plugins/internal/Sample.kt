package net.rubygrapefruit.plugins.internal

import net.rubygrapefruit.machine.info.Architecture
import net.rubygrapefruit.machine.info.Architecture.Arm64
import net.rubygrapefruit.machine.info.Machine
import java.nio.file.Path

sealed interface Sample {
    val name: String

    val sourceTree: SourceTree
}

sealed interface Lib : Sample {
    val jvm: JvmTarget?
}

class JvmTarget(val jvmVersion: Int, val libDirPath: String, val jarNamePrefix: String)

class JvmLib internal constructor(override val name: String, override val sourceTree: SourceTree, jvmVersion: Int) : Lib {
    override val jvm = JvmTarget(jvmVersion, "build/libs", name)
}

class KmpLib internal constructor(override val name: String, override val sourceTree: SourceTree, jvmVersion: Int?) : Lib {
    override val jvm = if (jvmVersion != null) JvmTarget(jvmVersion, "build/libs", "$name-jvm") else null
}

sealed interface App : Sample {
    /**
     * The default distribution.
     */
    val distribution: AppDistribution

    /**
     * Other distributions to test.
     */
    val otherDistributions: List<AppDistribution>

    val jvm: JvmTarget?
}

sealed interface CliApp : App

class JvmCliApp internal constructor(
    override val name: String,
    override val distribution: CliAppDistribution,
    override val sourceTree: SourceTree,
    jvmVersion: Int
) : CliApp {
    override val otherDistributions: List<CliAppDistribution>
        get() = emptyList()

    override val jvm = if (distribution.invocation is ScriptInvocationWithSystemJvm) JvmTarget(jvmVersion, "build/dist/lib", name) else null
}

class NativeCliApp internal constructor(
    override val name: String,
    sampleDir: Path,
    launcher: String?,
    args: List<String>,
    expectedOutput: List<String>,
    override val sourceTree: SourceTree
) : CliApp {
    override val distribution: CliAppDistribution

    override val otherDistributions: List<CliAppDistribution> = nativeDistributions(sampleDir) { distTask, distDir, architecture ->
        CliAppDistribution.ofBinary(name, distTask, distDir, launcher, args, expectedOutput, architecture)
    }

    override val jvm: JvmTarget?
        get() = null

    init {
        val distDir = sampleDir.resolve("build/dist")
        distribution = CliAppDistribution.ofBinary(name, "dist", distDir, launcher, args, expectedOutput, Machine.thisMachine.architecture)
    }
}

sealed interface UiApp : App

class JvmUiApp internal constructor(
    override val name: String,
    sampleDir: Path,
    launcher: String?,
    override val sourceTree: SourceTree
) : UiApp {
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

    // Uses embedded JVM
    override val jvm = null
}

class NativeUiApp internal constructor(
    override val name: String,
    sampleDir: Path,
    launcher: String?,
    override val sourceTree: SourceTree
) : UiApp {
    override val distribution = UiAppDistribution.of(name, "dist", sampleDir.resolve("build/dist"), launcher, Machine.thisMachine.architecture)

    override val otherDistributions: List<UiAppDistribution>
        get() = emptyList()

    override val jvm: JvmTarget?
        get() = null
}

private fun <T : AppDistribution> nativeDistributions(sampleDir: Path, factory: (String, Path, Architecture) -> T): List<T> {
    val host = Machine.thisMachine
    return if (host.isMacOS && host.architecture == Arm64) {
        listOf(factory("macosArm64ReleaseDist", sampleDir.resolve("build/dist-images/macosArm64Release"), Arm64))
    } else {
        emptyList()
    }
}